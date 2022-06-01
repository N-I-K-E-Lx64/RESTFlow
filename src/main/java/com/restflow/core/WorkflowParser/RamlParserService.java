package com.restflow.core.WorkflowParser;

import com.restflow.core.Storage.ApiStorage;
import com.restflow.core.Storage.StorageService;
import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RamlParserService implements BiFunction<String, String, Api> {

  private static final Logger logger = LogManager.getLogger(RamlParserService.class);

  private final StorageService storageService;

  private final Map<String, List<ApiStorage>> ramlFiles = new ConcurrentHashMap<>();

  @Autowired
  RamlParserService(StorageService storageService) {
    this.storageService = storageService;
  }

  /**
   * Parses the raml file into a usable api object
   *
   * @param ramlFile RAML file
   * @return Parsed api (Version 1.0)
   */
  public ApiStorage parseRaml(File ramlFile, String modelId) {
    RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFile);
    if (ramlModelResult.hasErrors()) {
      ramlModelResult.getValidationResults()
          .forEach(validationResult -> logger.error(validationResult.getMessage()));
      throw new RuntimeException(
          MessageFormat.format("Raml file {0} contains errors!", ramlFile.getName()));
    } else {
      final Api parsedApi = ramlModelResult.getApiV10();
      // The parsedApi object is null if the raml file describes a library instead of an API
      if (!Objects.isNull(parsedApi)) {
        final ApiStorage apiStorage = new ApiStorage(ramlFile.getName(), parsedApi,
            this.extractResources(parsedApi));
        // Save the api file in the state
        if (this.ramlFiles.containsKey(modelId)) {
          this.ramlFiles.get(modelId).add(apiStorage);
        } else {
          this.ramlFiles.put(modelId, Stream.of(apiStorage).collect(Collectors.toList()));
        }

        return apiStorage;
      } else {
        return null;
      }
    }
  }

  private List<String> extractResources(Api api) {
    if (!Objects.isNull(api)) {
      return api.resources()
          .stream()
          .map(resource -> resource.relativeUri().value())
          .toList();
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Returns a specific api description.
   *
   * @param modelId  Id of the corresponding model (name of the folder)
   * @param fileName Name of the file
   * @return Specified Api object or if it does not exist null
   */
  @Override
  public Api apply(String modelId, String fileName) {
    return this.ramlFiles.get(modelId)
        .stream()
        .filter(apiStorage -> Objects.equals(apiStorage.fileName(), fileName))
        .map(ApiStorage::api)
        .findFirst()
        .orElse(null);
  }

  /**
   * Returns a list of all ApiStorage-objects related to a specific model. If the state does not
   * have an entry for that model, but the corresponding folder is not empty, these api files are
   * being parsed and the resulting ApiStorage-objects are returned.
   *
   * @param modelId Id of the corresponding model (name of the folder)
   * @return List of ApiStorage-objects
   * @see ApiStorage
   */
  public List<ApiStorage> getRamlFilesForModel(String modelId) {
    if (ramlFiles.containsKey(modelId)) {
      return ramlFiles.get(modelId);
    } else {
      // TODO : This throws an error!
      List<File> storedFiles = this.storageService.loadAllFilesFromFolder(modelId);
      if (!storedFiles.isEmpty()) {
        return storedFiles.stream()
            .map(file -> parseRaml(file, modelId))
            .filter(Objects::nonNull)
            .toList();
      } else {
        throw new RuntimeException(
            MessageFormat.format("No api description could be found for model {0}!", modelId));
      }
    }
  }
}
