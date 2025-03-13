package org.gatorapps.garesearch.service;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.*;
import com.mongodb.client.model.search.*;
import jakarta.validation.ConstraintViolationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.gatorapps.garesearch.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    private ValidationUtil validationUtil;


    // add logic NOT archived ? / not closed
    public List<Map> getSearchResults (String searchParams) throws Exception {
        try {
            Bson searchStage = Aggregates.search(
                    SearchOperator.compound()
                            .should(List.of(
                                    SearchOperator.autocomplete(SearchPath.fieldPath("name"), searchParams)
                                            .fuzzy(FuzzySearchOptions.fuzzySearchOptions()
                                                    .maxEdits(2)
                                                    .prefixLength(1)
                                                    .maxExpansions(256)),
                                    SearchOperator.autocomplete(SearchPath.fieldPath("rawDescription"), searchParams)
                                            .fuzzy(FuzzySearchOptions.fuzzySearchOptions()
                                                    .maxEdits(2)
                                                    .prefixLength(1)
                                                    .maxExpansions(256))
                            )),
                    SearchOptions.searchOptions()
                            .index("positionSearchIndex")
                            .option("scoreDetails", true)
            );

            List<Bson> pipeline = Arrays.asList(
                    searchStage,
                    Aggregates.match(Filters.eq("status", "open")),
                    Aggregates.project(Projections.fields(
                            Projections.computed("labIdObjectId", new Document().append("$toObjectId", "$labId")),
                            Projections.computed("positionId", new Document().append("$toString", "$_id")),
                            Projections.computed("positionName", "$name"),
                            Projections.computed("positionDescription", "$description"),
                            Projections.include("postedTimeStamp", "lastUpdatedTimeStamp"),
                            Projections.computed("score", new Document("$meta", "searchScore"))
                            )),
                    Aggregates.sort(Sorts.descending("score")),
                    Aggregates.lookup(
                            "labs",      // Collection to join
                            "labIdObjectId",  // Local field
                            "_id",            // Foreign field
                            "lab"             // Alias for the joined field
                    ),
                    Aggregates.unwind("$lab", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                    Aggregates.project(Projections.fields(
                            Projections.include("positionId",
                                    "positionName",
                                    "positionDescription",
                                    "postedTimeStamp"),
                            Projections.computed("labName", "$lab.name"),
                            Projections.excludeId()
                    ))
            );

            AggregateIterable<Document> results = garesearchMongoTemplate.getCollection("positions").aggregate(pipeline);

            // convert to List
            List<Map> resultList = new ArrayList<>();
            results.forEach(resultList::add);

            if (resultList.isEmpty()){
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "No Positions found. Try Expanding your search terms");
            }
            return resultList;
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }


    public List<Map> getSearchIndexerResults (String searchParams) throws Exception {
        try {
            Bson searchStage = Aggregates.search(
                    SearchOperator.compound()
                            .should(List.of(
                                    SearchOperator.autocomplete(SearchPath.fieldPath("name"), searchParams)
                                            .fuzzy(FuzzySearchOptions.fuzzySearchOptions()
                                                    .maxEdits(2)
                                                    .prefixLength(1)
                                                    .maxExpansions(256)),
                                    SearchOperator.autocomplete(SearchPath.fieldPath("rawDescription"), searchParams)
                                            .fuzzy(FuzzySearchOptions.fuzzySearchOptions()
                                                    .maxEdits(2)
                                                    .prefixLength(1)
                                                    .maxExpansions(256))
                            )),
                    SearchOptions.searchOptions()
                            .index("positionSearchIndex")
                            .option("scoreDetails", true)
            );

            List<Bson> pipeline = Arrays.asList(
                    searchStage,
                    Aggregates.match(Filters.eq("status", "open")),
                    Aggregates.sort(Sorts.descending("score")),
                    Aggregates.project(Projections.fields(
                            Projections.computed("positionId", new Document().append("$toString", "$_id")),
                            Projections.computed("positionName", "$name"),
                            Projections.excludeId()
                    ))
            );

            AggregateIterable<Document> results = garesearchMongoTemplate.getCollection("positions").aggregate(pipeline);

            // convert to List
            List<Map> resultList = new ArrayList<>();
            results.forEach(resultList::add);

            if (resultList.isEmpty()){
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "No Positions found. Try Expanding your search terms");
            }
            return resultList;
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException){
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public Map getPublicPosting (String positionId) throws Exception {
        try {
            // must match 'opid'
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("_id").is(new ObjectId(positionId))
                    ),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("positionId")
                            .andExpression("toObjectId(labId)").as("labIdObjectId")
                            .and("name").as("positionName")
                            .and("description").as("positionDescription")
                            .andInclude("status",
                                    "postedTimeStamp",
                                    "lastUpdatedTimeStamp"),
                    // join with 'labs' collection
                    Aggregation.lookup(
                            "labs",      // Collection to join
                            "labIdObjectId",           // Local field
                            "_id",             // Foreign field
                            "lab"             // Alias for the joined field
                    ),
                    // flatten 'lab' array
                    Aggregation.unwind("lab", true),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("labId")
                            .and("lab.name").as("labName")
                            .andInclude(
                                    "positionId",
                                    "positionName",
                                    "positionDescription",
                                    "status",
                                    "postedTimeStamp",
                                    "lastUpdatedTimeStamp")
                            .andExclude("_id")
            );

            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
                    aggregation, "positions", Map.class);

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


    public Optional<Position> getPosting (String id){
        // TODO

        return positionRepository.findById(id);
    }


    public void savePosting(String opid, Position position) throws Exception {
        try {



            position.setRawDescription(position.getDescription());

            // manual command to validate because JPA annotations do not get checked on .save
            //validationUtil.validate(position);

            positionRepository.save(position);
        } catch (Exception e){
            if (e instanceof ConstraintViolationException){
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

}
