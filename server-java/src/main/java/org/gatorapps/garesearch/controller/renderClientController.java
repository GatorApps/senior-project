package org.gatorapps.garesearch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/renderClient")
public class renderClientController {

    @GetMapping("/appAlert")
    public ResponseEntity<Map<String, Object>> getAppAlert() {
        Object[] appAvailability = {
                "0",
                new String[]{"info", "Scheduled Maintenance", "GatorApps is scheduled for necessary maintenance on Saturday, September 2nd, 2023 from 2AM to 6AM EST. Apps will have limited availability. We are always working for your better experience!", null}
        };

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of("appAvailability", appAvailability)
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

    @GetMapping("/leftMenuItems")
    public ResponseEntity<Map<String, Object>> getLeftMenuItems() {
        List<Map<String, Object>> leftMenuItems = List.of(
                Map.of(
                        "heading", "Heading 1",
                        "items", List.of(
                                Map.of("label", "Item 1", "route", "/1_1"),
                                Map.of("label", "Item 2", "route", "/1_2"),
                                Map.of("label", "Item 3 (Expandable)", "subItems", List.of(
                                        Map.of("label", "Subitem 1", "route", "/1_3_s1"),
                                        Map.of("label", "Subitem 2", "route", "/1_3_s2", "newTab", true),
                                        Map.of("label", "Subitem 3", "route", "/1_3_s3")
                                )),
                                Map.of("label", "Item 4 (Opens in new tab)", "route", "/1_4", "newTab", true),
                                Map.of("label", "Item 5 (Expandable)", "subItems", List.of(
                                        Map.of("label", "Subitem 1", "route", "/1_5_s1"),
                                        Map.of("label", "Subitem 2", "route", "/1_5_s2")
                                )),
                                Map.of("label", "Item 6", "route", "/1_6")
                        )
                ),
                Map.of(
                        "heading", "Heading 2",
                        "items", List.of(
                                Map.of("label", "Item 1 (Opens in new tab)", "route", "/2_1", "newTab", true),
                                Map.of("label", "Item 2", "route", "/2_2"),
                                Map.of("label", "Item 3 (Expandable)", "subItems", List.of(
                                        Map.of("label", "Subitem 1", "route", "/2_3_s1"),
                                        Map.of("label", "Subitem 2", "route", "/2_3_s2", "newTab", true),
                                        Map.of("label", "Subitem 3", "route", "/2_3_s3")
                                )),
                                Map.of("label", "Item 4", "route", "/2_4")
                        )
                ),
                Map.of(
                        "heading", "Heading 3",
                        "items", List.of(
                                Map.of("label", "Item 1", "route", "/3_1"),
                                Map.of("label", "Item 2 (Opens in new tab)", "route", "/3_2", "newTab", true),
                                Map.of("label", "Item 3 (Expandable)", "subItems", List.of(
                                        Map.of("label", "Subitem 1", "route", "/3_3_s1"),
                                        Map.of("label", "Subitem 2", "route", "/3_3_s2", "newTab", true),
                                        Map.of("label", "Subitem 3", "route", "/3_3_s3")
                                )),
                                Map.of("label", "Item 4", "route", "/3_4")
                        )
                )
        );

        // Construct the response payload
        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of("leftMenuItems", leftMenuItems)
        );

        // Return the response with a custom status code (e.g., 200 OK)
        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

}
