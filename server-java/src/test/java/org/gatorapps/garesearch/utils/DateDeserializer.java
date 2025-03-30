package org.gatorapps.garesearch.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectNode node = jp.getCodec().readTree(jp);
        String dateStr = node.get("$date").asText();
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }
}

