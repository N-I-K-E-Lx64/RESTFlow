package com.restflow.core.Network;

import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;

public enum ERequestSender {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ERequestSender.class);

    private static final MediaType JSON = MediaType.get("application/json");

    private final OkHttpClient mClient = new OkHttpClient();

    public ResponseBody buildRequest(IRequest pRequest, IWorkflow pWorkflow) {

        Request.Builder builder = new Request.Builder();
        builder.url(pRequest.url());

        switch (pRequest.type()) {
            case GET:
                builder.get();
                break;

            case POST:
                String lJson = pRequest.fieldsAsJson();
                if (!lJson.equals("")) {
                    RequestBody lBody = RequestBody.create(JSON, lJson);
                    builder.post(lBody);
                }
                break;

            default:
                throw new RuntimeException(MessageFormat.format("Unknown Http-Type: [{0}]!", pRequest.type()));

        }

        Request lRequest = builder.build();

        try (Response lResponse = mClient.newCall(lRequest).execute()) {
            if (!lResponse.isSuccessful()) {
                pWorkflow.setStatus(EWorkflowStatus.ERROR);
                logger.error("Request could not be executed successfully!" + lResponse);
                throw new CWorkflowExecutionException("Request could not be executed successfully!");
            }

            return lResponse.body();
        } catch (IOException ex) {
            pWorkflow.setStatus(EWorkflowStatus.ERROR);
            logger.error("Request could not be executed successfully" + ex);
            throw new CWorkflowExecutionException("Request could not be executed successfully!", ex);
        }
    }
}
