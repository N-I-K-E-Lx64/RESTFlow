package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.*;
import com.example.demo.WorkflowExecution.Objects.CUserInteractionException;
import com.example.demo.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.example.demo.WorkflowExecution.Objects.EWorkflowStatus;
import com.example.demo.WorkflowExecution.Objects.IWorkflow;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ResponseBody;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
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
                }).map(Map.Entry::getValue)
                .collect(Collectors.toList());

        //Request kann nur ausgeführt werden, wenn alle Variablen belegt sind!
        if (emptyVariables.size() > 0) {
            mWorkflow.setStatus(EWorkflowStatus.WAITING);
            mWorkflow.setEmptyVariables(emptyVariables.stream()
                    .map(variable -> variable.name())
                    .collect(Collectors.toList()));

            return true;
        }

        String lUrl = mTask.api().baseUri().value() + mTask.api().resources().get(mTask.resourceIndex()).relativeUri().value();

        ERequestType lRequestType = ERequestType.INSTANCE.get(mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).method());

        IRequest lRequest = new CRequest(lUrl, lRequestType, mTask.parameters());

        processSuccess(Objects.requireNonNull(ERequestSender.INSTANCE.buildRequest(lRequest, mWorkflow)));

        return false;
    }

    /**
     * @param iMessage
     */
    @Override
    public void accept(IMessage iMessage) {
        CParameter lParameter = (CParameter) mTask.parameters().get(iMessage.parameterName());
        if (Objects.isNull(lParameter)) {
            throw new CUserInteractionException(MessageFormat.format("Parameter [{0}] does not exist!", iMessage.parameterName()));
        } else if (!Objects.isNull(lParameter.value())) {
            throw new CUserInteractionException(MessageFormat.format("Parameter [{0}] already set!", iMessage.parameterName()));
        }

        lParameter.setValue(iMessage.parameterValue());

        mWorkflow.emptyVariables().remove(iMessage.parameterName());

        if (!(mWorkflow.emptyVariables().size() > 0)) {
            mWorkflow.setStatus(EWorkflowStatus.WORKING);
        }
    }

    private void processSuccess(ResponseBody pResponse) {

        JsonNode lResponseNode;

        try {
            lResponseNode = mMapper.readTree(pResponse.string());
        } catch (IOException ex) {
            throw new CWorkflowExecutionException("Can't parse Response Body into a Json Node", ex);
        }

        if (!Objects.isNull(mTask.assignTask())) {
            mTask.assignTask().setJsonSource(lResponseNode);

            EWorkflowTaskFactory.INSTANCE.factory(mWorkflow, mTask.assignTask()).apply(mWorkflow.execution());
        }
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
