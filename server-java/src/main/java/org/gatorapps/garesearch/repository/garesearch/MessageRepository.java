package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findByRecipientOpidOrderBySentTimeStampDesc(String recipientOpid, Pageable pageable);
    @Query(value = "{'recipientOpid': ?0, 'sentTimeStamp': { $gt: ?1 }}", count = true)
    long countNewerMessages(String recipientOpid, Date sentTimeStamp);

}
