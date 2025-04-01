package org.gatorapps.garesearch.utils;

import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MongoUpdateUtilTests {
    @Test
    void updateUtil_Lab() {
        Lab lab = new Lab();
        lab.setId("abc0c01ab87e195493ae9c10");
        lab.setName("test lab");
        lab.setUsers(Arrays.asList(
                new User(lab.getId(), "Admin")
        ));
        lab.setDepartment("department");
        lab.setDescription("description for testing");
        lab.setWebsite("https://google.com");


        Update update = MongoUpdateUtil.createUpdate(lab);

        assertNotNull(update);
        assertEquals(1, update.getUpdateObject().keySet().size());
        assertTrue(update.getUpdateObject().containsKey("$set"));

        Map updatedMap = (Map) update.getUpdateObject().get("$set");

        assertEquals(5, updatedMap.size());


        assertTrue(updatedMap.containsKey("name"));
        assertEquals(lab.getName(), updatedMap.get("name"));

        assertTrue(updatedMap.containsKey("department"));
        assertEquals(lab.getDepartment(), updatedMap.get("department"));

        assertTrue(updatedMap.containsKey("description"));
        assertEquals(lab.getDescription(), updatedMap.get("description"));

        assertTrue(updatedMap.containsKey("website"));
        assertEquals(lab.getWebsite(), updatedMap.get("website"));

        assertTrue(updatedMap.containsKey("users"));
        assertEquals(lab.getUsers(), updatedMap.get("users"));

        assertFalse(updatedMap.containsKey("email"));

        fieldsNotIncludedCheck(updatedMap);
    }


    @Test
    void updateUtil_Position() {
        Position position = new Position();
        position.setId("1110c01ab87e195493ae9c10");
        position.setLabId("abc0c01ab87e195493ae9c10");
        position.setName("New Name");
        position.setPostedTimeStamp(new Date());
        position.setLastUpdatedTimeStamp(new Date());
        position.setDescription("description for testing");
        position.setRawDescription("description for testing");
        position.setStatus("open");

        Update update = MongoUpdateUtil.createUpdate(position);

        assertNotNull(update);
        assertEquals(1, update.getUpdateObject().keySet().size());
        assertTrue(update.getUpdateObject().containsKey("$set"));

        Map updatedMap = (Map) update.getUpdateObject().get("$set");

        assertEquals(5, updatedMap.size());

        assertTrue(updatedMap.containsKey("labId"));
        assertEquals(position.getLabId(), updatedMap.get("labId"));

        assertTrue(updatedMap.containsKey("name"));
        assertEquals(position.getName(), updatedMap.get("name"));

        assertTrue(updatedMap.containsKey("description"));
        assertEquals(position.getDescription(), updatedMap.get("description"));

        assertTrue(updatedMap.containsKey("rawDescription"));
        assertEquals(position.getRawDescription(), updatedMap.get("rawDescription"));

        assertTrue(updatedMap.containsKey("status"));
        assertEquals(position.getStatus(), updatedMap.get("status"));


        assertFalse(updatedMap.containsKey("supplementalQuestions"));

        fieldsNotIncludedCheck(updatedMap);
    }

    @Test
    void updateUtil_EmptyObject() {
        class EmptyEntity {}
        EmptyEntity entity = new EmptyEntity();
        Update update = MongoUpdateUtil.createUpdate(entity);

        assertNotNull(update);
        assertTrue(update.getUpdateObject().isEmpty());
    }




    @Getter
    @Setter
    static class TestEntity {
        private String id = "123";
        private String name = "testName";

        private Date postedTimeStamp = new Date();
        private Date submissionTimeStamp = new Date();
        private Date lastUpdatedTimeStamp = new Date();
    }

    @Test
    void updateUtil_SkipFields() {
        TestEntity testEntity = new TestEntity();

        Update update = MongoUpdateUtil.createUpdate(testEntity);

        assertNotNull(update);
        assertEquals(1, update.getUpdateObject().keySet().size());
        assertTrue(update.getUpdateObject().containsKey("$set"));

        Map updatedMap = (Map) update.getUpdateObject().get("$set");

        assertEquals(1, updatedMap.size());

        assertTrue(updatedMap.containsKey("name"));
        assertEquals(testEntity.getName(), updatedMap.get("name"));

        fieldsNotIncludedCheck(updatedMap);
    }






    void fieldsNotIncludedCheck(Map updatedMap){
        assertFalse(updatedMap.containsKey("id"));
        assertFalse(updatedMap.containsKey("postedTimeStamp"));
        assertFalse(updatedMap.containsKey("submissionTimeStamp"));
        assertFalse(updatedMap.containsKey("lastUpdatedTimeStamp"));
        assertFalse(updatedMap.containsKey("class"));
    }



}
