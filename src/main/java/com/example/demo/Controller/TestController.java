package com.example.demo.Controller;

import com.example.demo.RamlToApiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
public class TestController {

    Logger logger = LogManager.getLogger(TestController.class);

    @RequestMapping("/")
    public String start() {
        logger.info("Start Process");

        try {
            RamlToApiParser.getInstance().convertRamlToApi("G:/GIT Repositorys/REST-Orchestration-Engine/res/RAML-Files/Market.raml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return "Test";
    }
}
