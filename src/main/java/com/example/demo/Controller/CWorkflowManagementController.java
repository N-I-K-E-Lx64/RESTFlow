package com.example.demo.Controller;

import com.example.demo.EWorkflowStorage;
import com.example.demo.Network.IMessage;
import com.example.demo.Storage.StorageService;
import com.example.demo.WorkflowParser.CParameterFactory;
import com.example.demo.WorkflowParser.EWorkflowParser;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/workflow")
public class CWorkflowManagementController {

    private static final Logger logger = LogManager.getLogger(CWorkflowManagementController.class);

    private final StorageService mStorageService;

    @Autowired
    public CWorkflowManagementController(StorageService storageService) {
        mStorageService = storageService;
        EWorkflowParser.INSTANCE.init(storageService);
    }

    @RequestMapping(value = "/parseWorkflow", method = RequestMethod.POST)
    public void parseWorkflow(@RequestParam("workflow") String workflow, @RequestParam("workflowFile") String workflowFile) {

        Resource lWorkflowResource = mStorageService.loadAsResource(workflowFile, workflow);
        if (FilenameUtils.getExtension(lWorkflowResource.getFilename()).equals("json")) {
            try {
                EWorkflowStorage.INSTANCE.add(EWorkflowParser.INSTANCE.parseWorkflow(lWorkflowResource));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @RequestMapping(value = "/start/{workflow:.+}")
    public void startWorkflow(@PathVariable String workflow) {

        EWorkflowStorage.INSTANCE.apply(workflow).start();
    }

    @RequestMapping(value = "/setUserParameter", method = RequestMethod.POST)
    public void setUserVariable(@RequestBody CMessage pMessage) {

        final IWorkflow lWorkflow = EWorkflowStorage.INSTANCE.apply(pMessage.workflow());

        lWorkflow.accept(pMessage);
    }


    /**
     * Class for representing the messages!
     */
    public static final class CMessage implements IMessage {

        @JsonProperty("workflow")
        private String mWorkflow;
        @JsonProperty("parameter")
        private String mParameter;
        @JsonProperty("type")
        private String mParameterType;
        @JsonProperty("value")
        private String mParameterValue;



        //TODO : Implement the get Method!
        @Override
        public String get() {
            return null;
        }

        public String workflow() {
            return mWorkflow;
        }

        public String parameterName() {
            return mParameter;
        }

        public Object parameterValue() {
            return CParameterFactory.getInstance().createParameterValue(mParameterType, mParameterValue);
        }


    }


}
