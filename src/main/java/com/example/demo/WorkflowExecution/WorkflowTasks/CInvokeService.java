package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.*;
import com.example.demo.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class CInvokeService extends IBaseTaskAction {

    private static final ObjectMapper mMapper = new ObjectMapper();
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
                    return Objects.isNull(parameter.getValue().value());
                }).map(map -> map.getValue())
                .collect(Collectors.toList());

        //Request kann nur ausgeführt werden, wenn alle Variablen belegt sind!
        if (emptyVariables.size() > 0) {
            return true;
        }

        String lUrl = mTask.api().baseUri().value() + mTask.api().resources().get(mTask.resourceIndex()).relativeUri().value();

        ERequestType lRequestType = ERequestType.INSTANCE.get(mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).method());

        IRequest lRequest = new CRequest(lUrl, lRequestType, mTask.parameters());

        processSuccess(ERequestSender.INSTANCE.buildRequest(lRequest));

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

    private void processSuccess(Response pResponse) {

        JsonNode lResponseNode;

        try {
            lResponseNode = mMapper.readTree(pResponse.body().string());
        } catch (IOException e) {
            throw new CWorkflowExecutionException("Can't parse Response Body into a Json Node", e);
        }

        if (!Objects.isNull(mTask.assignTask())) {
            mTask.assignTask().setJsonSource(lResponseNode);

            EWorkflowTaskFactory.INSTANCE.factory(mWorkflow, mTask.assignTask()).apply(mWorkflow.getQueue());
        }

        if (mTask.isValidatorRequired()) {
            List<ValidationResult> lValidationResults =
                    mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).responses().get(0).body().get(0)
                            .validate(lResponseNode.toString());


            if (lValidationResults.size() > 0) {
                mWorkflow.setWorkflowStatus(false);
            }
        }
    }
}
