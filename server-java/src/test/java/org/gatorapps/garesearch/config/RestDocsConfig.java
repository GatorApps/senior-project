package org.gatorapps.garesearch.config;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

public class RestDocsConfig {

    public static RestDocumentationResultHandler getDefaultDocHandler(String docName){
        return MockMvcRestDocumentation.document(docName,
                Preprocessors.preprocessRequest(
                        Preprocessors.modifyHeaders()
                                .set(HttpHeaders.AUTHORIZATION, "<Authentication Token>")
                                .remove("Host"),
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
