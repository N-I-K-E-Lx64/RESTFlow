package com.example.demo.WorkflowParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public enum ERamlParser {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ERamlParser.class);

    public Api parseRaml(Resource ramlResource) throws IOException {

        File ramlFile = ramlResource.getFile();

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFile);
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                logger.error(validationResult.getMessage());
            }
        } else {
            return ramlModelResult.getApiV10();
        }
        return null;
    }
}