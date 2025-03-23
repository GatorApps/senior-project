package org.gatorapps.garesearch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class SESService {

    private final SesClient sesClient;

    @Value("${aws.ses.default-sender-name}")
    private String senderName;

    @Value("${aws.ses.default-sender-address}")
    private String senderEmail;

    public SESService(
            @Value("${aws.accessKeyId}") String accessKeyId,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.ses.region}") String region
    ) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);
        this.sesClient = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder().toAddresses(to).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).charset("UTF-8").build())
                            .body(Body.builder()
                                    .html(Content.builder().data(body).charset("UTF-8").build())
                                    .build())
                            .build())
                    .source(String.format("%s <%s>", senderName, senderEmail))
                    .build();

            sesClient.sendEmail(request);
        } catch (SesException e) {
            System.err.println("Failed to send email: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error sending email via AWS SES", e);
        }
    }

    public void sendEmailToOpid(String recipientOpid, String subject, String body) {
        sendEmail("lukeli379@gmail.com", subject, body);
    }
}
