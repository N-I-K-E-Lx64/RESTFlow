package com.example.demo.Controller;

import com.example.demo.WorkflowParser.CWorkflowParser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    @RequestMapping
    public String start() {

        try {
            CWorkflowParser.INSTANCE.parseWorkflow();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Success";
    }
}
