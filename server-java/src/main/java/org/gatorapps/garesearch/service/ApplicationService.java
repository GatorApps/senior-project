package org.gatorapps.garesearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.exception.MalformedParamException;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.exception.UnwantedResult;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.File;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.garesearch.repository.garesearch.ApplicationRepository;
import org.gatorapps.garesearch.repository.garesearch.FileRepository;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.gatorapps.garesearch.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    ApplicantProfileRepository applicantProfileRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    private LabService labService;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    @Qualifier("accountMongoTemplate")
    private MongoTemplate accountMongoTemplate;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    ValidateUserAuthInterceptor validateUserAuthInterceptor;
    @Autowired
    private FileRepository fileRepository;

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
                                    "submissionTimeStamp"),

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
                                    "submissionTimeStamp"),
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
                                    "submissionTimeStamp")
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
        // Check if position is valid and open
        Position foundPosition;
        try {
            foundPosition = positionRepository.findById(positionId)
                    .orElseThrow(() -> new ResourceNotFoundException("-", "PositionId " + positionId + " does not exist"));
        } catch (Exception e){
            // TODO : check what e.getMessage would actually say for bad param
            System.out.println(e.getMessage());

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
            if (!Objects.equals(foundApplication.get().getStatus(), "saved")) {
                throw new UnwantedResult("-", "You have already applied to this position");
            }
//            else if (Objects.equals(saveApp, "true")) {
//                throw new UnwantedResult("-", "You have already saved this position");
//            }
        }

        // Validate resumeId and transcriptId
        String resumeId = (String) application.get("resumeId");
        File resumeFile = fileRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("-", "Invalid resumeId (" + resumeId + "), please try again"));
        if (!resumeFile.getOpid().equals(opid)){
            throw new UnwantedResult("-", "Invalid resumeId (" + resumeId + "), please try again");
        }

        String transcriptId = (String) application.get("transcriptId");
        File transcriptFile = fileRepository.findById(transcriptId)
                .orElseThrow(() -> new ResourceNotFoundException("-", "Invalid transcriptId (" + transcriptId + "), please try again"));
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

            System.out.println(application);

//            validationUtil.validate(newApp);
            garesearchMongoTemplate.save(newApp);
        } catch (Exception e){
//            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time", e);
        }

        // case to save application
//        if (Objects.equals(saveApp, "true")) {
//            try {
//                Application newApp = new Application();
//                newApp.setOpid(opid);
//                newApp.setPositionId(positionId);
//                newApp.setSubmissionTimeStamp(new Date());
//                newApp.setStatus("saved");
//
//                validationUtil.validate(newApp);
//                garesearchMongoTemplate.save(newApp);
//                return;
//            } catch (Exception e){
//                throw new Exception("Unable to process your request at this time", e);
//            }
//        }

//        Query profileQuery = new Query(Criteria.where("opid").is(opid).and("positionId").is(positionId));
//        profileQuery.fields().exclude("_id").exclude("__v");
//
//        ApplicantProfile foundProfile = garesearchMongoTemplate.findOne(profileQuery, ApplicantProfile.class);
//        if (foundProfile == null) {
//            throw new ResourceNotFoundException("-", "Applicant profile has not been set up yet. Please create your profile to easily apply to all available positions");
//        }
//
//        Map<String, Object> applicationData = convertToMap(foundProfile);
//        applicationData.put("opid", opid);
//        applicationData.put("positionId", positionId);
//        applicationData.put("submissionTimeStamp", new Date());
//        applicationData.put("status", "submitted");
//
//        try {
//            Update update = new Update();
//            applicationData.forEach(update::set);
//
//            Query applicationQuery = new Query(Criteria.where("opid").is(opid).and("positionId").is(positionId));
//
//            garesearchMongoTemplate.findAndModify(
//                    applicationQuery,
//                    update,
//                    FindAndModifyOptions.options().upsert(true).returnNew(true),
//                    Application.class
//            );
//        } catch (Exception e) {
//            throw new Exception("Unable to process your request at this time", e);
//        }

    }

    public boolean alreadyApplied(String opid, String positionId) {
        return applicationRepository.existsByOpidAndPositionId(opid, positionId);
    }

    public Application getApplication(String opid, String labId, String applicationId) throws Exception {
        labService.checkPermission(opid, labId);
        return applicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Application Not Found"));
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
        } catch (Exception e){
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to modify the requested application");
            }
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time", e);
        }
    }

}