package com.restflow.core.Storage;

import org.raml.v2.api.model.v10.api.Api;
import org.springframework.lang.NonNull;

import java.util.List;

public record ApiStorage(String fileName, Api api, List<String> resources) {

	public ApiStorage(@NonNull final String fileName, @NonNull final Api api, @NonNull final List<String> resources) {
		this.fileName = fileName;
		this.api = api;
		this.resources = resources;
	}
}
