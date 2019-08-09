package com.restflow.core.Controller;

import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.WorkflowDatabase.EActiveWorkflows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collaboration")
public class CollaborationController {

    private static final Logger logger = LogManager.getLogger(CollaborationController.class);

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public ResponseEntity<?> retrieveCollaboration(@RequestBody CCollaborationMessage pMessage) {

        EActiveWorkflows.INSTANCE.apply(pMessage.getWorkflowInstance()).accept(pMessage);
        logger.info(pMessage);

        return ResponseEntity.ok().body("test");
    }
}
