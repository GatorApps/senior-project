package org.gatorapps.garesearch.service;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.*;
import com.mongodb.client.model.search.*;
import jakarta.validation.ConstraintViolationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.exception.MalformedParamException;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.gatorapps.garesearch.utils.MongoUpdateUtil;
import org.gatorapps.garesearch.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    LabService labService;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    private ValidationUtil validationUtil;

    public Map<String, Object> getSearchResults(String searchParams, int page, int size) throws Exception {
        try {
            // search the Atlas Search Index
            Bson searchStage = Aggregates.search(
                    SearchOperator.compound()
                            .should(List.of(
                                    SearchOperator.text(SearchPath.fieldPath("name"), searchParams)
                                            .score(SearchScore.boost(5)),
                                    SearchOperator.autocomplete(SearchPath.fieldPath("name"), searchParams)
                                            .fuzzy(FuzzySearchOptions.fuzzySearchOptions()
                                                    .maxEdits(2)
                                                    .prefixLength(1)
                                                    .maxExpansions(256)),
                                    SearchOperator.autocomplete(SearchPath.fieldPath("rawDescription"), searchParams)
                                            .fuzzy(FuzzySearchOptions.fuzzySearchOptions()
                                                    .maxEdits(2)
                                                    .prefixLength(2)
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
                    Aggregates.sort(Sorts.orderBy(
                            Sorts.descending("score"),
                            Sorts.descending("postedTimeStamp"))),
                    Aggregates.skip(page*size),
                    Aggregates.limit(size),
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

            if (resultList.isEmpty()) {
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "No Positions found. Try Expanding your search terms");
            }

            List<Bson> countPipeline = Arrays.asList(
                    searchStage,
                    Aggregates.match(Filters.eq("status", "open")),
                    Aggregates.count("totalCount") // Count matching documents
            );
            AggregateIterable<Document> totalResults = garesearchMongoTemplate.getCollection("positions").aggregate(countPipeline);
            long totalCount = 0;
            for (Document doc : totalResults) {
                totalCount = doc.getInteger("totalCount");
            }
            long totalPages = totalCount / size + (totalCount % size == 0 ? 0 : 1);

            Map<String, Object> response = new HashMap<>();
            response.put("positions", resultList);
            response.put("totalPages", totalPages);
            response.put("totalCount", totalCount);

            return response;
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }


    public List<Map> getSearchIndexerResults(String searchParams) throws Exception {
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

            if (resultList.isEmpty()) {
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "No Positions found. Try Expanding your search terms");
            }
            return resultList;
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public Map getPublicPosting(String positionId) throws Exception {
        try {
            // must match 'id'
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
                            .andExpression("{ $toString: '$lab._id' }").as("labId")
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

            if (results.getMappedResults().isEmpty()) {
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Position Not Found");
            }
            return results.getMappedResults().get(0);
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public Position getPosting(String id) throws Exception {
        return positionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Position Not Found"));
    }

    public Position getPosting(String opid, String id) throws Exception {
        Position position = positionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Position Not Found"));

        try {
            labService.checkPermission(opid, position.getLabId());
        } catch (Exception e) {
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to modify the requested position");
            }
            throw new Exception("Unable to process your request at this time", e);
        }

        return position;
    }


    public List<Map> getPostingNames(String opid) throws Exception {
        try {
            // lab must have user with 'opid'
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("users.opid").is(opid)
                    ),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("labId"),
                    // join with 'positions' collection
                    Aggregation.lookup(
                            "positions",      // Collection to join
                            "labId",                 // Local field
                            "labId",               // Foreign field
                            "position"             // Alias for the joined field
                    ),
                    // flatten 'position' array
                    Aggregation.unwind("position", false),
                    Aggregation.project()
                            .andExpression("{ $toString: '$position._id' }").as("positionId")
                            .and("position.name").as("name")
                            .andExclude("_id")
            );

            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
                    aggregation, "labs", Map.class);

            return results.getMappedResults();
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }


    public List<Map> getPostingsList(String opid) throws Exception {
        try {
            // lab must have user with 'opid'
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(
                            Criteria.where("users.opid").is(opid)
                    ),
                    Aggregation.project()
                            .andExpression("{ $toString: '$_id' }").as("labId")
                            .and("name").as("labName"),
                    // join with 'positions' collection
                    Aggregation.lookup(
                            "positions",      // Collection to join
                            "labId",                 // Local field
                            "labId",               // Foreign field
                            "position"             // Alias for the joined field
                    ),
                    // flatten 'position' array
                    Aggregation.unwind("position", false),
                    Aggregation.project()
                            .andExpression("{ $toString: '$position._id' }").as("positionId")
                            .and("position.name").as("name")
                            .and("position.lastUpdatedTimeStamp").as("lastUpdatedTimeStamp")
                            .and("position.postedTimeStamp").as("postedTimeStamp")
                            .and("position.status").as("status")
                            .andInclude("labName")
                            .andInclude("labId")
                            .andExclude("_id")
            );

            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
                    aggregation, "labs", Map.class);

            return results.getMappedResults();
        } catch (Exception e) {
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public void updateStatus(String opid, String positionId, String status) throws Exception {
        try {
            Position position = positionRepository.findById(positionId).orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));

            labService.checkPermission(opid, position.getLabId());

            position.setStatus(status);
            positionRepository.save(position);
        } catch (Exception e) {
            System.out.println("in exception");
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to modify the requested position");
            }
            if (e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public void createPosting(String opid, Position position) throws Exception {
        try {
            labService.checkPermission(opid, position.getLabId());
            if (position.getDescription() != null) {
                position.setRawDescription(position.getDescription());
            }

            positionRepository.save(position);
        } catch (Exception e) {
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to modify the requested position");
            }
            if (e instanceof ConstraintViolationException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public void updatePosting(String opid, Position position) throws Exception {
        try {
            if (position.getId() == null){
                throw new MalformedParamException("ERR_REQ_MISSING_REQUIRED_PARAM", "Missing required req params: positionId");
            }

            labService.checkPermission(opid, position.getLabId());
            if (position.getDescription() != null) {
                position.setRawDescription(position.getDescription());
            }
            Update update = MongoUpdateUtil.createUpdate(position);

            garesearchMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(new ObjectId(position.getId()))), update, Position.class);
        } catch (Exception e) {
            System.out.println("in exception");
            if (e instanceof AccessDeniedException) {
                throw new AccessDeniedException("Insufficient permissions to modify the requested position");
            }
            if (e instanceof ConstraintViolationException || e instanceof  MalformedParamException) {
                throw e;
            }
            throw new Exception("Unable to process your request at this time", e);
        }
    }

}
