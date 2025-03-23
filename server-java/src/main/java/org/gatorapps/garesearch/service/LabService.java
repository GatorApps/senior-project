package org.gatorapps.garesearch.service;

import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.User;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.gatorapps.garesearch.utils.MongoUpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }

    }

    public List<Map> getLabNames(String opid) throws Exception {
        try {
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("users.opid").is(opid)
                    ),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("labId")
                            .and("name").as("labName")
                            .andExclude("_id")
            );

            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
                    aggregation, "labs", Map.class);

            if (results.getMappedResults().isEmpty()){
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time");
            }

            return results.getMappedResults();
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public void createProfile(String opid, Lab lab) throws Exception {
        try {
            lab.setUsers(Arrays.asList(
                    new User(opid, "Admin")
            ));
            labRepository.save(lab);
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public void updateProfile(String opid, Lab lab) throws Exception {
        try {
            checkPermission(opid, lab.getId());
            Update update = MongoUpdateUtil.createUpdate(lab);

            garesearchMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(new ObjectId(lab.getId()))), update, Lab.class);
        } catch (Exception e){
            if (e instanceof ConstraintViolationException || e instanceof AccessDeniedException){
                throw e;
            }
            System.out.println(e.getMessage());
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public Lab getProfile (String opid, String id) throws Exception {
        checkPermission(opid, id);
        return labRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Lab Not Found"));
    }

    public void checkPermission(String opid, String labId) throws Exception {
        Query query = new Query(Criteria.where("_id").is(labId)
                .and("users.opid").is(opid));
        if (!garesearchMongoTemplate.exists(query, Lab.class)){
            throw new AccessDeniedException("Insufficient permissions for the requested lab");
        }
    }
}
