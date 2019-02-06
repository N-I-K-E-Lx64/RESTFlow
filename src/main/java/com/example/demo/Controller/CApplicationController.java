package com.example.demo.Controller;

import com.example.demo.Storage.StorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CApplicationController {

    private static final Logger logger = LogManager.getLogger(CApplicationController.class);

    @Autowired
    private StorageService mStorageService;

    @GetMapping("/parseWorkflow/{workflowName:.+}")
    public void requestParsing(@PathVariable String workflowName) {
        //TODO : Check if it throws an Exception
        mStorageService.loadAsResource(workflowName);
    }


}
