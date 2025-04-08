package org.gatorapps.garesearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.dto.ApplicationWithUserInfo;
import org.gatorapps.garesearch.exception.MalformedParamException;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.exception.UnwantedResult;
import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.File;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.ApplicationRepository;
import org.gatorapps.garesearch.repository.garesearch.FileRepository;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    private LabService labService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    @Qualifier("accountMongoTemplate")
    private MongoTemplate accountMongoTemplate;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileRepository fileRepository;

    @Value("${app.frontend-host}")
    private String frontendHost;
    @Autowired
    private PositionService positionService;

    public Map<String, Object> convertToMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }

    public Application getStudentApplication (String opid, String applicationId) throws Exception {
        // find by both opid and applicationId to ensure correct user is accessing the application
        try {
            return applicationRepository.findByOpidAndId(opid, applicationId.trim()).orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Application Not Found"));
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public List<Map> getStudentApplications(String opid) throws Exception {
        try {
            // must match 'opid'
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("opid").is(opid)
                    ),
                    Aggregation.project()
                            .andExpression("toObjectId(positionId)").as("positionIdObjectId")
                            .andInclude("status",
                                    "submissionTimeStamp",
                                    "positionId"),

                    // join with 'positions' collection
                    Aggregation.lookup(
                            "positions",      // Collection to join
                            "positionIdObjectId",  // Local field
                            "_id",                 // Foreign field
                            "position"             // Alias for the joined field
                    ),
                    // flatten 'position' array
                    Aggregation.unwind("position", true),
                    Aggregation.project()
                            .andExpression("toObjectId(position.labId)").as("labIdObjectId")
                            .and("position.name").as("positionName")
                            .andInclude("status",
                                    "submissionTimeStamp",
                                    "positionId"),
                    // join with 'labs' collection
                    Aggregation.lookup(
                            "labs",
                            "labIdObjectId",
                            "_id",
                            "lab"
                    ),
                    Aggregation.unwind("lab", true),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("applicationId")
                            .and("lab.name").as("labName")
                            .andInclude("positionName",
                                    "status",
                                    "submissionTimeStamp",
                                    "positionId")
                            .andExclude("_id")
            );

            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
                    aggregation, "applications", Map.class);

            return results.getMappedResults();
        } catch (Exception e) {
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public void submitApplication (String opid, String positionId, Map<String, Object> application) throws Exception {
        // Validate applicantOpid
        Optional<User> applicantOptional = userService.getUserByOpid(opid);
        if (applicantOptional.isEmpty()) {
            throw new ResourceNotFoundException("", "Applicant not found");
        }
        User applicant = applicantOptional.get();

        // Check if position is valid and open
        Position foundPosition;
        try {
            foundPosition = positionRepository.findById(positionId)
                    .orElseThrow(() -> new ResourceNotFoundException("-", "PositionId " + positionId + " does not exist"));
        } catch (Exception e){
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            if (e instanceof IllegalArgumentException && e.getMessage().contains("Invalid format")){
                throw new MalformedParamException("ERR_REQ_INVALID_PARAM_MALFORMID", "positionId is malformed");
            }
            throw new Exception("Unable to process your request at this time", e);
        }

        if (!Objects.equals(foundPosition.getStatus(), "open")){
            throw new UnwantedResult("-", "Position " + positionId + " is not open");
        }

        // Check if application not already submitted
        Optional<Application> foundApplication = applicationRepository.findByOpidAndPositionId(opid, positionId);

        if (foundApplication.isPresent()) {
            throw new UnwantedResult("-", "You have already applied to this position");
        }

        // Validate resumeId
        String resumeId = (String) application.get("resumeId");
        File resumeFile = fileRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("-", "Invalid resumeId (" + resumeId + "), please try again"));
        // Check user owns the resume file
        if (!resumeFile.getOpid().equals(opid)){
            throw new UnwantedResult("-", "Invalid resumeId (" + resumeId + "), please try again");
        }

        // Validate transcriptId
        String transcriptId = (String) application.get("transcriptId");
        File transcriptFile = fileRepository.findById(transcriptId)
                .orElseThrow(() -> new ResourceNotFoundException("-", "Invalid transcriptId (" + transcriptId + "), please try again"));
        // Check user owns the transcript file
        if (!transcriptFile.getOpid().equals(opid)){
            throw new UnwantedResult("-", "Invalid transcriptId (" + transcriptId + "), please try again");
        }

        // Submit application
        try {
            Application newApp = new Application();
            newApp.setOpid(opid);
            newApp.setPositionId(positionId);
            newApp.setResumeId(resumeId);
            newApp.setTranscriptId(transcriptId);
            newApp.setSupplementalResponses((String) application.get("supplementalResponses"));
            newApp.setStatus("submitted");

            garesearchMongoTemplate.save(newApp);
        } catch (Exception e){
            throw new Exception("Unable to process your request at this time", e);
        }
      
        // Send applicant confirmation message
        messageService.sendMessage(null, opid, "Your application is in!" ,
                String.format("<p>Way to go, %s!<br><br>You have successfully submitted an application to <a href=\"%s\">%s</a>. Please remember to track your application status on the <a href=\"%s\">My Applications</a> module and reach out directly to the <a href=\"%s\">lab</a> you're applying to should you have any questions.<br><br>Best of luck!</p>",
                        applicant.getFirstName(), String.format("%s/posting?postingId=%s", frontendHost, positionId), foundPosition.getName(), String.format("%s/myapplications", frontendHost), String.format("%s/lab?labId=%s", frontendHost, foundPosition.getLabId())));
    }

    public boolean alreadyApplied(String opid, String positionId) {
        return applicationRepository.existsByOpidAndPositionId(opid, positionId);
    }

    public ApplicationWithUserInfo getApplication(String opid, String labId, String applicationId) throws Exception {
        labService.checkPermission(opid, labId);

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Application Not Found"));

        String applicantOpid = application.getOpid();

        // Fetch user info
        Query query = new Query(Criteria.where("opid").is(applicantOpid));
        Map user = accountMongoTemplate.findOne(query, Map.class, "users");

        ApplicationWithUserInfo result = new ApplicationWithUserInfo();
        result.setApplicationId(application.getId());
        result.setOpid(application.getOpid());
        result.setPositionId(application.getPositionId());
        result.setResumeId(application.getResumeId());
        result.setTranscriptId(application.getTranscriptId());
        result.setSupplementalResponses(application.getSupplementalResponses());
        result.setSubmissionTimeStamp(application.getSubmissionTimeStamp());
        result.setStatus(application.getStatus());

        if (user != null) {
            result.setFirstName((String) user.get("firstName"));
            result.setLastName((String) user.get("lastName"));

            List<String> emails = (List<String>) user.get("emails");
            if (emails != null && !emails.isEmpty()) {
                result.setEmail(emails.get(0));
            }
        }

        return result;
    }

    public List<Map> getApplicationList(String opid, String positionId) throws Exception {
        try {
            Position position = positionRepository.findById(positionId).orElseThrow(() ->  new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));

            labService.checkPermission(opid, position.getLabId());

            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("positionId").is(positionId)
                    ),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("applicationId")
                            .andInclude("opid",
                                    "positionId",
                                    "submissionTimeStamp",
                                    "status")
                            .andExclude("_id")
            );

            List<Map> applications = garesearchMongoTemplate.aggregate(aggregation, "applications", Map.class).getMappedResults();

            // extract unique opid from applications
            Set<String> opids = applications.stream()
                    .map(app -> (String) app.get("opid"))
                    .collect(Collectors.toSet());

            // find associated user
            Query query = new Query(Criteria.where("opid").in(opids));
            List<Map> users = accountMongoTemplate.find(query, Map.class, "users");

            // key : opid , value: user details
            Map<String, Map> userMap = users.stream()
                    .collect(Collectors.toMap(user -> (String) user.get("opid"), user -> user));

            // combine user info and applications
            for (Map app : applications){
                String appOpid = (String) app.get("opid");
                Map user = userMap.get(appOpid);
                if (user != null) {
                    app.put("labId", position.getLabId());
                    app.put("firstName", userMap.get(appOpid).get("firstName"));
                    app.put("lastName", userMap.get(appOpid).get("lastName"));

                    List<String> emails = (List<String>) user.get("emails");
                    if (emails != null && !emails.isEmpty()){
                        app.put("email", emails.get(0));
                    } else {
                        app.put("email", null);
                    }
                }
            }

            return applications;

        } catch (Exception e){
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to view these applications");
            }
            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time");
        }
    }

    public void updateStatus(String opid, String labId, String applicationId, String status) throws Exception {
        try {
            labService.checkPermission(opid, labId);

            Application application = applicationRepository.findById(applicationId).orElseThrow(() ->  new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Application " + applicationId + " not found"));

            application.setStatus(status);
            applicationRepository.save(application);

            // Send applicant status update notice
            // Get applicant
            User foundApplicant = userService.getUserByOpid(application.getOpid())
                    .orElseThrow(() -> new ResourceNotFoundException("-", "Applicant with opid " + application.getOpid() + " does not exist"));
            // Get position
            Position foundPosition = positionService.getPosting(application.getPositionId());
            messageService.sendMessage(null, foundApplicant.getOpid(), "An update to your application status is available" ,
                    String.format("<p>Hello, %s!<br><br>A status update has been posted to your application to <a href=\"%s\">%s</a>. You may now check your application status on the <a href=\"%s\">My Applications</a> module. Please reach out directly to the <a href=\"%s\">lab</a> you're applying to should you have any questions.<br><br>Thank you for using RESEARCH.UF!</p>",
                            foundApplicant.getFirstName(), String.format("%s/posting?postingId=%s", frontendHost, application.getPositionId()), foundPosition.getName(), String.format("%s/myapplications", frontendHost), String.format("%s/lab?labId=%s", frontendHost, foundPosition.getLabId())));
        } catch (Exception e){
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to modify the requested application");
            }
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
//            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time", e);
        }
    }

}