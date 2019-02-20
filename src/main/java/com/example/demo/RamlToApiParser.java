package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;

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

    public Api convertRamlToApi(File pRamlFile) throws FileNotFoundException {
        logger.info("RAML-File exists?: " + pRamlFile.exists());
        logger.info("RAML-File can be read?: " + pRamlFile.canRead());
        if (pRamlFile.exists() && pRamlFile.canRead()) {
            RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(pRamlFile);

            if (ramlModelResult.hasErrors()) {
                for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                    logger.error(validationResult.getMessage());
                }
            } else {
                return ramlModelResult.getApiV10();
            }
        } else {
            throw new FileNotFoundException("RAMLFile could not be opened!");
        }
        return null;
    }
}
