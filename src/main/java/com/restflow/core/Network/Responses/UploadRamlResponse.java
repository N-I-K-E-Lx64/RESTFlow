package com.restflow.core.Network.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public record UploadRamlResponse(@JsonProperty("fileName") String fileName,
                                 @JsonProperty("resources") List<String> resources) {

	public UploadRamlResponse(@NonNull final String fileName, @NonNull final List<String> resources) {
		this.fileName = fileName;
		this.resources = resources;
	}

	@Override
	public String fileName() {
		return fileName;
	}

	@Override
	public List<String> resources() {
		return resources;
	}
}
