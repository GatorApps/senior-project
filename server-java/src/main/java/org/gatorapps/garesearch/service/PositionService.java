package org.gatorapps.garesearch.service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.SearchOptions;
import com.mongodb.client.model.search.TextSearchOperator;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;


    public List<Map> getSearchResults (String searchParams) throws Exception {
        try {

//            List<Bson> pipeline = Arrays.asList(
//                    Aggregates.search(
//                            new Document("index", "positionSearchIndex") // Specify the Atlas Search index name
//                                    .append("text", new Document("$search", searchParams))
//                    )
//            );
//
//            Aggregation aggregation = Aggregation.newAggregation(
//                    Aggregation.match(
//                            Criteria.where("$text").is(new Document("$search", searchParams).append("index", "positionSearchIndex"))
//                    ),
//                    Aggregation.project()
//                            .andExpression("toObjectId(labId)").as("labIdObjectId")
//                            .andExpression("{ $toString: '$_id' }").as("positionId")
//                            .and("name").as("positionName")
//                            .and("description").as("positionDescription"),
//
//                    // join with 'labs' collection
//                    Aggregation.lookup(
//                            "labs",      // Collection to join
//                            "labIdObjectId",  // Local field
//                            "_id",                 // Foreign field
//                            "lab"             // Alias for the joined field
//                    ),
//                    // flatten 'lab' array
//                    Aggregation.unwind("lab", true),
//                    Aggregation.project()
//                            .andInclude("positionId")
//                            .andInclude("positionName")
//                            .andInclude("positionDescription")
//                            .and("lab.name").as("labName")
//                            .andExclude("_id")
//            );

//            AggregationResults<Map> results = garesearchMongoTemplate.aggregate(
//                    aggregation, "positions", Map.class);

            AggregationResults<Document> results = garesearchMongoTemplate.aggregate(Aggregation.newAggregation(pipeline), "positions", Document.class);

            if (results.getMappedResults().isEmpty()){
                throw new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time");
            }
            return results.getMappedResults();
        } catch (Exception e){
            System.out.println(e.getMessage());
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
