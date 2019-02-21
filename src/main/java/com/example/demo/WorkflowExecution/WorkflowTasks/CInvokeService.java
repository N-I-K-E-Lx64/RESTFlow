package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;
import unirest.HttpResponse;
import unirest.JsonNode;
import unirest.Unirest;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CInvokeService extends IBaseTaskAction {

    private final CInvokeServiceTask mTask;

    public CInvokeService(IWorkflow pWorkflow, CInvokeServiceTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

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


        return false;
    }

    @Override
    public void accept(Queue<ITaskAction> iTaskActions, IMessage iMessage) {

        CParameter lParameter = (CParameter) mTask.parameters().get(iMessage.parameterName());
        if (Objects.isNull(lParameter))
            throw new RuntimeException(MessageFormat.format("Parameter [{0}] ist nicht vorhanden.", iMessage.parameterName()));

        lParameter.setValue(iMessage.parameterValue());
    }

    public void buildRequest() {
        String lUrl = mTask.api().baseUri().value() + mTask.api().resources().get(mTask.methodIndex()).relativeUri().value();

        CRequest lRequest = new CRequest(lUrl);

        if (mTask.parameters().size() > 0) {
            Map<String, Object> lFields = new HashMap<>();
            mTask.parameters().forEach((key, value) -> {
                lFields.put(key, value.value());
            });

            lRequest.setFields(lFields);
        }

        switch (mTask.api().resources().get(mTask.methodIndex()).methods().get(0).method().toUpperCase()) {
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

    //TODO :  UserVariablen-Belegung genauso wie gameAction in CGameActionController

    private void sendGetRequest(CRequest pRequest) {
        CompletableFuture<HttpResponse<JsonNode>> future = Unirest.get(pRequest.url())
                .headers(pRequest.headers())
                .queryString(pRequest.fields())
                .asJsonAsync(response -> {
                    //response.ifFailure(failure ->);
                    //response.ifSuccess(success ->);
                });
    }

    private void sendPostRequest(CRequest pRequest) {
        CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(pRequest.url())
                .headers(pRequest.headers())
                .queryString(pRequest.fields())
                .asJsonAsync(response -> {
                    //response.ifFailure(failure ->);
                    //response.ifSuccess(success ->);
                });
    }


    public class CRequest {

        private final String mUrl;
        private Map<String, String> mHeaders;
        private Map<String, Object> mFields;

        public CRequest(String pUrl) {
            this.mUrl = pUrl;
        }

        public void setFields(Map<String, Object> pFields) {
            this.mFields = pFields;
        }

        public String url() {
            return mUrl;
        }

        public Map<String, String> headers() {
            return mHeaders;
        }

        public Map<String, Object> fields() {
            return mFields;
        }
    }
}
