package org.gatorapps.garesearch.service;

import org.bson.types.ObjectId;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LabService {
    @Autowired
    LabRepository labRepository;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    public Map getPublicProfile (String labId) throws Exception {
        try {
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("_id").is(new ObjectId(labId))
                    ),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("labId")
                            .and("name").as("labName")
                            .and("description").as("labDescription")
                            .andInclude("department",
                                    "website",
                                    "email"),
                    // join with 'positions' collection
                    Aggregation.lookup(
                            "positions",      // Collection to join
                            "labId",              // Local field
                            "labId",             // Foreign field
                            "positions"             // Alias for the joined field
                   ),

//
                    Aggregation.unwind("positions", true),
                    Aggregation.match(Criteria.where("positions.status").is("open")),

                    Aggregation.project()
                            .and("labId").as("labId")
                            .and("labName").as("labName")
                            .and("labDescription").as("labDescription")
                            .and("department").as("department")
                            .and("website").as("website")
                            .and("email").as("email")
                            .and("positions.name").as("positions.positionName")
                            .and("positions.description").as("positions.positionDescription")
                            .andExpression("positions.postedTimeStamp").as("positions.postedTimeStamp")
                            .andExpression("positions.lastUpdatedTimeStamp").as("positions.lastUpdatedTimeStamp")
                            .andExpression("{ $toString: '$positions._id' }").as("positions.positionId")
                            .andExclude("_id"),

                    Aggregation.group("labId", "labName", "labDescription", "department", "website", "email")
                            .push("positions").as("positions")
            );

            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
                    aggregation, "labs", Map.class);

            if (results.getMappedResults().isEmpty()){
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time");
            }


            Map lab = (Map) results.getMappedResults().get(0).get("_id");
            lab.put("positions", results.getMappedResults().get(0).get("positions"));
            return lab;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time", e);
        }

    }

    public Optional<Lab> getProfile (String id){
        // TODO

        return labRepository.findById(id);
    }


    public Optional<Lab> createProfile (Lab lab){
        // TODO

        return Optional.of(labRepository.save(lab));
    }

    public Optional<Lab> updateProfile (Lab lab){
        // TODO

        return Optional.of(labRepository.save(lab));
    }
}
