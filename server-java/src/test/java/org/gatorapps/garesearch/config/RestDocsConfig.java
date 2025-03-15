package org.gatorapps.garesearch.config;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import java.util.regex.Pattern;

public class RestDocsConfig {

    public static RestDocumentationResultHandler getDefaultDocHandler(String docName){
        return MockMvcRestDocumentation.document(docName,
                Preprocessors.preprocessRequest(
                        Preprocessors.modifyHeaders()
                                .set(HttpHeaders.AUTHORIZATION, "<Authentication Token>")
                                .remove("Host"),
                        Preprocessors.replacePattern(Pattern.compile("\"opid\"\\s*:\\s*\"[^\"]*\""), "\"opid\": \"<User OPID>\""),
                        Preprocessors.replacePattern(Pattern.compile("\"role\"\\s*:\\s*\"[^\"]*\""), "\"role\": \"<User Role>\""),
                        Preprocessors.replacePattern(Pattern.compile("&(?!\\s*$)"), "&\n"),
                        Preprocessors.prettyPrint()
                ),
                Preprocessors.preprocessResponse(
                        Preprocessors.modifyHeaders()
                                .remove("Host")
                                .remove("Vary")
                                .remove("Content-Type")
                                .remove("Content-Length"),
                        Preprocessors.prettyPrint()
                )
        );
    }
}
