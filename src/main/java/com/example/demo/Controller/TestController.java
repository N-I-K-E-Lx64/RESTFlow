package com.example.demo.Controller;

import com.example.demo.WorkflowParser.EWorkflowParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    Logger logger = LogManager.getLogger(TestController.class);

    @RequestMapping("/")
    public String start() {
        logger.info("Start Process");

        try {
            EWorkflowParser.INSTANCE.parseWorkflow();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
        return "Test";
    }
}
