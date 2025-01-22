package org.gatorapps.templateapp.model.global;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "apps")
public class App {

    @Id
    private String id;

    @NotNull(message = "App code is required")
    @Field("code")
    @Indexed(unique = true) // Enforces unique constraint for "code"
    private Integer code;

    @NotBlank(message = "App name is required")
    @Field("name")
    @Indexed(unique = true) // Enforces unique constraint for "name"
    private String name;

    @NotBlank(message = "App displayName is required")
    @Field("displayName")
    private String displayName;

    @Field("origins")
    private List<String> origins;

    @Field("userInfoScope")
    private List<String> userInfoScope;

    @Field("authOptions")
    private List<String> authOptions;

    @Field("alert")
    private Alert alert;

    public static class Alert {

        @NotBlank(message = "Alert ID is required")
        @Field("alertId")
        private String alertId;

        @Field("displayAlert")
        private Boolean displayAlert;

        @Field("maintenanceMode")
        private Boolean maintenanceMode;

        @Field("severity")
        private String severity;

        @Field("title")
        private String title;

        @Field("message")
        private String message;

        @Field("actions")
        private List<Action> actions;

        public static class Action {

            @Field("title")
            private String title;

            @Field("action")
            private String action;

            // Getters and Setters
            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }
        }

        // Getters and Setters for Alert
        public String getAlertId() {
            return alertId;
        }

        public void setAlertId(String alertId) {
            this.alertId = alertId;
        }

        public Boolean getDisplayAlert() {
            return displayAlert;
        }

        public void setDisplayAlert(Boolean displayAlert) {
            this.displayAlert = displayAlert;
        }

        public Boolean getMaintenanceMode() {
            return maintenanceMode;
        }

        public void setMaintenanceMode(Boolean maintenanceMode) {
            this.maintenanceMode = maintenanceMode;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Action> getActions() {
            return actions;
        }

        public void setActions(List<Action> actions) {
            this.actions = actions;
        }
    }

    // Getters and Setters for App
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getOrigins() {
        return origins;
    }

    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }

    public List<String> getUserInfoScope() {
        return userInfoScope;
    }

    public void setUserInfoScope(List<String> userInfoScope) {
        this.userInfoScope = userInfoScope;
    }

    public List<String> getAuthOptions() {
        return authOptions;
    }

    public void setAuthOptions(List<String> authOptions) {
        this.authOptions = authOptions;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}
