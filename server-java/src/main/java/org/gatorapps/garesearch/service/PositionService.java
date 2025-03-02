package org.gatorapps.garesearch.service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.search.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;


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
                                    SearchOperator.autocomplete(SearchPath.fieldPath("description"), searchParams)
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
                    Aggregates.project(Projections.fields(
                            Projections.computed("labIdObjectId", new Document().append("$toObjectId", "$labId")),
                            Projections.computed("positionId", new Document().append("$toString", "$_id")),
                            Projections.computed("positionName", "$name"),
                            Projections.computed("positionDescription", "$description"),
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
                            Projections.include("positionId", "positionName", "positionDescription"),
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
        } catch (Exception e){
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
                                    SearchOperator.autocomplete(SearchPath.fieldPath("description"), searchParams)
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
        } catch (Exception e){
            throw new Exception("Unable to process your request at this time", e);
        }
    }

    public Position getPublicPosting (String id){
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));
    }


    public Optional<Position> getPosting (String id){
        // TODO

        return positionRepository.findById(id);
    }


    public Optional<Position> createPosting (Position position){
        // TODO

        return Optional.of(positionRepository.save(position));
    }

    public Optional<Position> updatePosting (Position position){
        // TODO

        return Optional.of(positionRepository.save(position));
    }

}
