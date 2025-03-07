package org.gatorapps.garesearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.exception.MalformedParamException;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.exception.UnwantedResult;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.garesearch.repository.garesearch.ApplicationRepository;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.gatorapps.garesearch.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApplicationService {
    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ApplicantProfileRepository applicantProfileRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    ValidateUserAuthInterceptor validateUserAuthInterceptor;

    public Map<String, Object> convertToMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }


    // TODO : write in the manual validation commands where needed

    public Map getStudentApplication (String opid, String applicationId) throws Exception {
        // find by both opid and applicationId to ensure correct user is accessing the application
        try {
            // must match 'opid'
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("opid").is(opid)
                                    .and("_id").is(new ObjectId(applicationId))
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

            if (results.getMappedResults().isEmpty()){
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time");
            }
            return results.getMappedResults().get(0);
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

    public void submitApplication (String positionId, String saveApp) throws Exception {
        // TODO : retrieving opid from spring security or something
        String opid = "127ad6f9-a0ff-4e3f-927f-a70b64c542e4";

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
            } else if (Objects.equals(saveApp, "true")) {
                throw new UnwantedResult("-", "You have already saved this position");
            }
        }

        // case to save application
        if (Objects.equals(saveApp, "true")) {
            try {
                Application newApp = new Application();
                newApp.setOpid(opid);
                newApp.setPositionId(positionId);
                newApp.setSubmissionTimeStamp(new Date());
                newApp.setStatus("saved");

                validationUtil.validate(newApp);
                garesearchMongoTemplate.save(newApp);
                return;
            } catch (Exception e){
                throw new Exception("Unable to process your request at this time", e);
            }
        }

        Query profileQuery = new Query(Criteria.where("opid").is(opid).and("positionId").is(positionId));
        profileQuery.fields().exclude("_id").exclude("__v");

        ApplicantProfile foundProfile = garesearchMongoTemplate.findOne(profileQuery, ApplicantProfile.class);
        if (foundProfile == null) {
            throw new ResourceNotFoundException("-", "Applicant profile has not been set up yet. Please create your profile to easily apply to all available positions");
        }

        Map<String, Object> applicationData = convertToMap(foundProfile);
        applicationData.put("opid", opid);
        applicationData.put("positionId", positionId);
        applicationData.put("submissionTimeStamp", new Date());
        applicationData.put("status", "submitted");

        try {
            Update update = new Update();
            applicationData.forEach(update::set);

            Query applicationQuery = new Query(Criteria.where("opid").is(opid).and("positionId").is(positionId));

            garesearchMongoTemplate.findAndModify(
                    applicationQuery,
                    update,
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    Application.class
            );
        } catch (Exception e) {
            throw new Exception("Unable to process your request at this time", e);
        }

    }

}
