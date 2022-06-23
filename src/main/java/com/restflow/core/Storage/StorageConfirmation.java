package com.restflow.core.Storage;

import java.io.File;
import org.springframework.lang.NonNull;

public record StorageConfirmation(String fileName, File file) {

  public StorageConfirmation(@NonNull final String fileName, @NonNull final File file) {
    this.fileName = fileName;
    this.file = file;
  }
}
