package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.exception.MalformedParamException;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.exception.UnwantedResult;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.garesearch.repository.garesearch.ApplicationRepository;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.nio.charset.MalformedInputException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    public List<Map> getStudentApplications (String status) throws Exception {
        // TODO : retrieving opid from spring security or something
        String opid = "4d9c6082-c107-4828-95bf-d998953f8f80";

        List<String> statuses = switch (status) {
            case "saved" -> List.of("saved");
            case "active" -> List.of("submitted", "accepted", "inreview");
            case "inactive" -> List.of("withdrawn", "denied", "closed");
            default -> List.of();
        };

        try {


            // must match 'opid' and 'status' fields
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("opid").is(opid)
                                    .and("status").in(statuses)
                    ),
                    Aggregation.project()
                            .andExpression("toObjectId(positionId)").as("positionIdObjectId"),
                    // join with 'positions' collection
                    Aggregation.lookup(
                            "positions",           // Collection to join
                            "positionIdObjectId",  // Local field
                            "_id",                 // Foreign field
                            "position"             // Alias for the joined field
                    ),
                    // flatten 'position' array
                    Aggregation.unwind("position", true),
                    Aggregation.project()
                            .andExpression("toObjectId(position.labId)").as("labIdObjectId"),
                    // join with 'labs' collection
                    Aggregation.lookup(
                            "labs",
                            "labIdObjectId",
                            "_id",
                            "lab"
                    ),
                    Aggregation.unwind("lab", true),
                    Aggregation.project()
                            .and("_id").as("applicationId")
                            .and("position.name").as("positionName")
                            .and("position.labId").as("labId")
                            .and("lab.name").as("labName")
                            .andInclude("status")
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
        String opid = "4d9c6082-c107-4828-95bf-d998953f8f80";

        // Check if position is valid and open
        Position foundPosition;
        try {
            foundPosition = positionRepository.findById(positionId)
                    .orElseThrow(() -> new ResourceNotFoundException("-", "PositionId " + positionId + " does not exist"));
        } catch (Exception e){
            // TODO : check what e.getMessage would actually say for bad param
            System.out.println(e.getMessage());

            if (e instanceof IllegalArgumentException && e.getMessage().contains("Invalid format")){
                throw new MalformedParamException("ERR_REQ_INVALID_PARAM_MALFORMID", "positionId is malformed")
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
                // TODO: create application
            } catch (Exception e){
                throw new Exception("Unable to process your request at this time", e);
            }
        }

        // case to submit application

        // TODO : excluded "-_id -__v' . could use DTO to ensure they dont get passed ? OR MongoTemplate query stuff
        ApplicantProfile foundProfile = applicantProfileRepository.findByOpidAndPositionId(opid, positionId)
                .orElseThrow(() -> new ResourceNotFoundException("-", "Applicant profile has not been set up yet. Please create your profile to easily apply to all available positions"));

        // TODO : Finish the rest . from applicationData onwards
        // updating application. and exception handling for that

    }

}
