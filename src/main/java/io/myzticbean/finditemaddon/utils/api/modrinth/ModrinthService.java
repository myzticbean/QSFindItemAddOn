package io.myzticbean.finditemaddon.utils.api.modrinth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.myzticbean.finditemaddon.utils.api.modrinth.model.ProjectDetailsResponse;
import io.myzticbean.finditemaddon.utils.log.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class ModrinthService {

    private static final String MODRINTH_API_BASE_URL = "https://api.modrinth.com/v2/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ModrinthService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<ProjectDetailsResponse> getProjectVersions(String projectId) {
        var url = MODRINTH_API_BASE_URL + "project/" + projectId + "/version";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        try {
            Logger.logDebugInfo("Outbound Request: " + url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Logger.logDebugInfo("Inbound Response: " + response.body());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<ProjectDetailsResponse>>() {});
            } else {
                Logger.logError("Modrinth API request failed with status code: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Logger.logError(e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            Logger.logError(e);
        }
        return Collections.emptyList();
    }
}
