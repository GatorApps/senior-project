package org.gatorapps.garesearch.service;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SESService {

    @Autowired
    private SesClient sesClient;

    @Value("${aws.ses.default-sender-name}")
    private String senderName;

    @Value("${aws.ses.default-sender-address}")
    private String senderEmail;

    @Value("${app.frontend-host}")
    private String frontendHost;

    @Value("${app.static-host}")
    private String staticHost;

    public void sendEmail(String to, String subject, String htmlBody, String textBody) {
//        if (textBody == null) {
//            textBody = Jsoup.parse(htmlBody).text();
//        }
//
//        try {
//            SendEmailRequest request = SendEmailRequest.builder()
//                    .destination(Destination.builder().toAddresses(to).build())
//                    .message(Message.builder()
//                            .subject(Content.builder().data(subject).charset("UTF-8").build())
//                            .body(Body.builder()
//                                    .html(Content.builder().data(htmlBody).charset("UTF-8").build())
//                                    .text(Content.builder().data(textBody).charset("UTF-8").build())
//                                    .build())
//                            .build())
//                    .source(String.format("%s <%s>", senderName, senderEmail))
//                    .build();
//
//            sesClient.sendEmail(request);
//        } catch (SesException e) {
//            System.err.println("Failed to send email: " + e.awsErrorDetails().errorMessage());
//            throw new RuntimeException("Error sending email via AWS SES", e);
//        }
    }

    public void sendBrandedEmail(String to, String subject, String content, List<Map<String, String>> links) {
        String linksHtml = links.stream()
                .map(link -> String.format("<a href=\"%s\">%s</a>", link.get("url"), link.get("text")))
                .collect(Collectors.joining("&nbsp;|&nbsp;"));
        String htmlBody = "<!DOCTYPE html> <html dir=\"ltr\" lang=\"en\"> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> <meta name=\"viewport\" content=\"width=device-width\"> <style type=\"text/css\"> @media only screen and (max-width: 620px) { table[class=body] p, table[class=body] ul, table[class=body] ol, table[class=body] td, table[class=body] span, table[class=body] a { font-size: 16px !important; } table[class=body] .bodycell { padding: 0 !important; width: 100% !important; } table[class=body] .maincell { padding: 10px !important; } } @media all { .ExternalClass { width: 100%; } .ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div { line-height: 100%; } } </style> </head> <body class=\"\" style=\"background-color:#ffffff; font-family:'Open Sans', 'Lucida Grande', 'Segoe UI', Arial, Verdana, 'Lucida Sans Unicode', Tahoma, 'Sans Serif'; font-size:14px; color: #444444; line-height:1.3; Margin:0; padding:0; -ms-text-size-adjust:100%; -webkit-font-smoothing:antialiased; -webkit-text-size-adjust:100%;\"> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse:separate; background-color:#ffffff; width:100%; box-sizing:border-box; mso-table-lspace:0pt; mso-table-rspace:0pt;\"> <tr> <td class=\"bodycell\" style=\"max-width:600px; width:100%; font-family:'Open Sans', 'Lucida Grande', 'Segoe UI', Arial, Verdana, 'Lucida Sans Unicode', Tahoma, 'Sans Serif'; font-size:14px; vertical-align:top; display:block; box-sizing:border-box; padding:10px; Margin:0 auto !important;\"> <table class=\"main\" style=\"background:#fff; width:100%; border-collapse:separate; mso-table-lspace:0pt; mso-table-rspace:0pt; \"> <tr> <td class=\"maincell\" style=\"font-family:sans-serif; font-size:14px; vertical-align:top; box-sizing:border-box; padding:20px;\">"
                + content.replace("\n", "<br>")
                + "</td> </tr> </table> <table class=\"logo\" style=\"width:100%; box-sizing:border-box; border-collapse:separate; mso-table-lspace:0pt; mso-table-rspace:0pt; \"> <tr> <td class=\"logocell\" style=\"text-align:center; vertical-align:top; box-sizing:border-box; padding:10px;\"> <img src=\""
                + String.format("%s/garesearch/email-signature.png", staticHost)
                + "\" alt=\"RESEARCH.UF logo\"> </td> </tr> </table> <table class=\"footer\" style=\"width:100%; box-sizing:border-box; border-collapse:separate; mso-table-lspace:0pt; mso-table-rspace:0pt; \"> <tr> <td class=\"footercell\" style=\"font-family:sans-serif; font-size:14px; vertical-align:top; color:#a8b9c6; font-size:12px; text-align:center; padding:10px; box-sizing:border-box; \">"
                + linksHtml
                + "</td> </tr> <tr> <td class=\"footercell\" style=\"font-family:sans-serif; font-size:14px; vertical-align:top; color:#a8b9c6; font-size:12px; text-align:center; padding:5px; box-sizing:border-box; \"> This is an automated message from GatorApps RESEARCH.UF. Please DO NOT reply directly as the no-reply email address is not monitored. For technical support, please contact support@gatorapps.org. </td> </tr> </table> </td> </tr> </table> </body> </html>";
        sendEmail(to, subject, htmlBody, null);
    }

}
