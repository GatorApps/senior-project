package org.gatorapps.garesearch.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.fasterxml.jackson.databind.JsonNode;
import org.gatorapps.garesearch.testModel.garesearch.supportingclasses.User;

import java.io.IOException;

public class UserDeserializer  extends JsonDeserializer<User> {
    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String opid = node.get("opid").asText();
        String role = node.get("role").asText();
        return new User(opid, role);
    }
}
