package com.restflow.core.Storage;

import org.springframework.lang.NonNull;

import java.io.File;
import java.nio.file.Path;

public record StorageConfirmation(String fileName, File file) {

	public StorageConfirmation(@NonNull final String fileName, @NonNull final File file) {
		this.fileName = fileName;
		this.file = file;
	}
}
