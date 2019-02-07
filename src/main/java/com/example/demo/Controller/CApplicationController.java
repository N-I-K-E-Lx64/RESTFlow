package com.example.demo.Controller;

import com.example.demo.EWorkflowStorage;
import com.example.demo.Storage.StorageService;
import com.example.demo.WorkflowParser.EWorkflowParser;
import com.example.demo.WorkflowParser.WorkflowObjects.IWorkflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class CApplicationController {

    private static final Logger logger = LogManager.getLogger(CApplicationController.class);

    @Autowired
    private StorageService mStorageService;

    @GetMapping("/parseWorkflow/{workflowName:.+}")
    public String requestParsing(@PathVariable String workflowName) {
        //TODO : Check if it throws an Exception
        try {
            File lFile = new File(mStorageService.loadAsResource(workflowName).getURI());
            logger.info("Successfully load WorkflowFile!");
            IWorkflow lWorkflow = EWorkflowParser.INSTANCE.parseWorkflow(lFile);
            EWorkflowStorage.INSTANCE.add(lWorkflow);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO : Return Nachricht überarbeiten
        return "Success";
    }

    @GetMapping("/startWorkflow/{workflowName:.+}")
    public String startWorkflow(@PathVariable String workflowName) {

        IWorkflow lWorkflow = EWorkflowStorage.INSTANCE.apply(workflowName);
        lWorkflow.start();

        //TODO : Return Nachricht überarbeiten
        return "Success";
    }


}
