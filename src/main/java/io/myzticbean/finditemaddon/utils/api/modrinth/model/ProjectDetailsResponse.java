package io.myzticbean.finditemaddon.utils.api.modrinth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDetailsResponse {

    private String id;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("author_id")
    private String authorId;

    private boolean featured;
    private String name;

    @JsonProperty("version_number")
    private String versionNumber;

    private String changelog;

    @JsonProperty("changelog_url")
    private String changelogUrl;

    @JsonProperty("date_published")
    private OffsetDateTime datePublished;

    private int downloads;

    @JsonProperty("version_type")
    private String versionType;

    private String status;

    @JsonProperty("requested_status")
    private String requestedStatus;

    private List<FileInfo> files;
    private List<Dependency> dependencies;

    @JsonProperty("game_versions")
    private List<String> gameVersions;

    private List<String> loaders;

    // 🔹 Nested class: FileInfo
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FileInfo {
        private String filename;
        private String url;
        private Map<String, String> hashes;

        @JsonProperty("primary")
        private boolean primary;

        private long size;
    }

    // 🔹 Nested class: Dependency
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dependency {
        @JsonProperty("version_id")
        private String versionId;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("dependency_type")
        private String dependencyType;
    }

}
