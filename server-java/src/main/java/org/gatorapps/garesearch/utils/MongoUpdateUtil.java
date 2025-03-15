package org.gatorapps.garesearch.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;


@Component
public class MongoUpdateUtil {
    static List<String> fieldsToSkip = List.of("postedTimeStamp", "submissionTimeStamp", "lastUpdatedTimeStamp", "id", "class");

    public static Update createUpdate(Object entity){

        Update update = new Update();
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
            String fieldName = descriptor.getName();
            System.out.println(fieldName);

            if (fieldsToSkip.contains(fieldName)) {
                continue;
            }

            Object fieldValue = wrapper.getPropertyValue(fieldName);
            if (fieldValue != null) {
                update.set(fieldName, fieldValue);
            }

        }
        return update;
    }


}
