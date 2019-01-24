package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.File;
import java.io.FileNotFoundException;

public class RamlToApiParser {

    private static RamlToApiParser INSTANCE;
    Logger logger = LogManager.getLogger(RamlToApiParser.class);

    private RamlToApiParser() {
    }

    public static RamlToApiParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RamlToApiParser();
        }
        return INSTANCE;
    }

    public void convertRamlToApi(String RamlFileLocation) throws FileNotFoundException {
        File ramlFile = new File(RamlFileLocation);
        if (ramlFile.exists() && ramlFile.canRead()) {
            RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFile);

            if (ramlModelResult.hasErrors()) {
                for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                    logger.error(validationResult.getMessage());
                }
            } else {
                EApiStorage.INSTANCE.add(ramlModelResult.getApiV10());
            }
        } else {
            throw new FileNotFoundException("RAMLFile could not be opened!");
        }
    }
}
