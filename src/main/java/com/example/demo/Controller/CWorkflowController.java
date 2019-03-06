package com.example.demo.Controller;

import com.example.demo.ERunningWorkflows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/workflows")
public class CWorkflowController {

    @RequestMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
    public Set<String> listWorkflows() {

        return ERunningWorkflows.INSTANCE.get();
    }
}
