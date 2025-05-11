package com.example.webhookapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookResponse {

	@JsonProperty("webhook")
    private String webhook;

    @JsonProperty("accessToken")
    private String accessToken;

    public String getWebhook() {
        return webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
