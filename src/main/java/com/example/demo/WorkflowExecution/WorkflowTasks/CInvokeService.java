package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;
import org.raml.v2.api.model.common.ValidationResult;
import unirest.HttpResponse;
import unirest.JsonNode;
import unirest.Unirest;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CInvokeService extends IBaseTaskAction {

    private final CInvokeServiceTask mTask;

    CInvokeService(IWorkflow pWorkflow, CInvokeServiceTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        //TODO : Better empty Check for Variables
        List<IParameter> emptyVariables = mTask.parameters().entrySet().stream()
                .filter(parameter -> {
                    if (Objects.isNull(parameter.getValue().value())) {
                        return true;
                    }
                    return false;
                }).map(map -> map.getValue())
                .collect(Collectors.toList());

        //Request kann nur ausgeführt werden, wenn alle Variablen belegt sind!
        if (emptyVariables.size() > 0) {
            return true;
        }

        buildRequest();

        return false;
    }

    /**
     * @param iMessage
     */
    @Override
    public void accept(IMessage iMessage) {
        //TODO : Check if Parameter is already set!
        CParameter lParameter = (CParameter) mTask.parameters().get(iMessage.parameterName());
        if (Objects.isNull(lParameter))
            throw new RuntimeException(MessageFormat.format("Parameter [{0}] ist nicht vorhanden.", iMessage.parameterName()));

        lParameter.setValue(iMessage.parameterValue());
    }

    private void processSuccess(HttpResponse pResponse) {

        if (!Objects.isNull(mTask.assignTask())) {
            mTask.assignTask().source().setValue(pResponse.getBody());

            EWorkflowTaskFactory.INSTANCE.factory(mWorkflow, mTask.assignTask()).apply(null);
        }

        if (mTask.isValidatorRequired()) {
            List<ValidationResult> lValidationResults =
                    mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).body().get(0).validate(pResponse.getBody().toString());

            if (lValidationResults.size() > 0) {
                mWorkflow.setWorkflowStatus(false);
            }
        }
    }

    private void buildRequest() {
        String lUrl = mTask.api().baseUri().value() + mTask.api().resources().get(mTask.resourceIndex()).relativeUri().value();

        CRequest lRequest = new CRequest(lUrl);

        if (mTask.parameters().size() > 0) {
            Map<String, Object> lFields = new HashMap<>();
            mTask.parameters().forEach((key, value) -> {
                lFields.put(key, value.value());
            });

            lRequest.setFields(lFields);
        }

        switch (mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).method().toUpperCase()) {
            case "GET":
                sendGetRequest(lRequest);
                break;

            case "POST":
                sendPostRequest(lRequest);
                break;
        }
    }

    //TODO : Application-Event-Publisher, der das Ergebnis publisht. Falls das Ergebnis eingetroffen ist (im Success-Fall)
    // accept-Funktion hier im CInvokeService aufrufen und Assign und Validation durchführen!

    private void sendGetRequest(CRequest pRequest) {
        CompletableFuture<HttpResponse<JsonNode>> future = Unirest.get(pRequest.url())
                //.headers(pRequest.headers())
                .queryString(pRequest.fields())
                .asJsonAsync(response -> {
                    //response.ifFailure(failure ->);
                    response.ifSuccess(this::processSuccess);
                });
    }

    private void sendPostRequest(CRequest pRequest) {
        CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(pRequest.url())
                //.headers(pRequest.headers())
                .queryString(pRequest.fields())
                .asJsonAsync(response -> {
                    response.ifFailure(System.out::println);
                    response.ifSuccess(this::processSuccess);
                });
    }


    private class CRequest {

        private final String mUrl;
        private Map<String, String> mHeaders;
        private Map<String, Object> mFields;

        CRequest(String pUrl) {
            this.mUrl = pUrl;
        }

        void setFields(Map<String, Object> pFields) {
            this.mFields = pFields;
        }

        void setHeaders(Map<String, String> pHeaders) {
            this.mHeaders = pHeaders;
        }

        String url() {
            return mUrl;
        }

        Map<String, String> headers() {
            return mHeaders;
        }

        Map<String, Object> fields() {
            return mFields;
        }
    }
}
