package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
//    List<Message> findByRecipientOpidOrderByCreatedAtDesc(String recipientOpid);
}
