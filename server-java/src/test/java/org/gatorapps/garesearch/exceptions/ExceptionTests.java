package org.gatorapps.garesearch.exceptions;


import org.gatorapps.garesearch.exception.FileValidationException;
import org.gatorapps.garesearch.exception.MalformedParamException;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.exception.UnwantedResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ExceptionTests {



    @Test
    void FileValidationExceptionTest(){
        String expectedMessage = "Custom error occurred";
        FileValidationException exception = new FileValidationException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());

        assertThrows(FileValidationException.class, () -> {
            throw exception;
        });
    }


    @Test
    void MalformedParamExceptionTest(){
        String expectedMessage = "malformed param exception thrown";
        String errCode = "MALFORMED_ERR_CODE";
        MalformedParamException exception = new MalformedParamException(errCode, expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(errCode, exception.getErrCode());

        assertThrows(MalformedParamException.class, () -> {
            throw exception;
        });
    }

    @Test
    void ResourceNotFoundExceptionTest(){
        String expectedMessage = "resource not found exception thrown";
        String errCode = "RESOURCE_NOT_FOUND_ERR_CODE";
        ResourceNotFoundException exception = new ResourceNotFoundException(errCode, expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(errCode, exception.getErrCode());

        assertThrows(ResourceNotFoundException.class, () -> {
            throw exception;
        });
    }


    @Test
    void UnwantedResultExceptionTest(){
        String expectedMessage = "unwanted result exception thrown";
        String errCode = "UNWANTED_ERR_CODE";
        UnwantedResult exception = new UnwantedResult(errCode, expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(errCode, exception.getErrCode());

        assertThrows(UnwantedResult.class, () -> {
            throw exception;
        });
    }

}
