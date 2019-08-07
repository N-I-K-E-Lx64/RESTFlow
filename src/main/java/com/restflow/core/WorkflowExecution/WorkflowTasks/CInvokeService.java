package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Network.ERequestSender;
import com.restflow.core.Network.ERequestTypeBuilder;
import com.restflow.core.Network.IMessage;
import com.restflow.core.Network.Objects.CRequest;
import com.restflow.core.Network.Objects.CUserParameterMessage;
import com.restflow.core.Network.Objects.IRequest;
import com.restflow.core.WorkflowExecution.Objects.CUserInteractionException;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class CInvokeService extends IBaseTaskAction {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final CInvokeServiceTask mTask;

    CInvokeService(IWorkflow pWorkflow, CInvokeServiceTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        //TODO : Better empty Check for Variables
        List<String> emptyVariables = mTask.parameters().values().stream()
                .filter(iParameter -> Objects.isNull(iParameter.value()))
                .map(IParameter::name)
                .collect(Collectors.toList());

        //Request kann nur ausgeführt werden, wenn alle Variablen belegt sind!
        if (!emptyVariables.isEmpty()) {
            mWorkflow.setStatus(EWorkflowStatus.SUSPENDED);
            mWorkflow.setEmptyVariables(emptyVariables);

            return true;
        }

        String lBaseUrl = mTask.api().baseUri().value();
        String lResourceUrl = mTask.api().resources().get(mTask.resourceIndex()).relativeUri().value();
        String lRequestType = mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).method();
        MediaType lRequestMediaType =
                MediaType.parseMediaType(mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).body().get(0).name());
        MediaType lResponseMediaType =
                MediaType.parseMediaType(mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).responses().get(0).body().get(0).name());

        IRequest lRequest = new CRequest(lBaseUrl, lResourceUrl,
                ERequestTypeBuilder.INSTANCE.createHttpMethodFromString(lRequestType), lRequestMediaType, lResponseMediaType, mTask.parameters());

        try {
            processSuccess(Objects.requireNonNull(ERequestSender.INSTANCE.doRequestWithWebClient(lRequest, mWorkflow)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param iMessage
     */
    @Override
    public void accept(IMessage iMessage) {

        CUserParameterMessage lMessage = (CUserParameterMessage) iMessage;

        CParameter lParameter = (CParameter) mTask.parameters().get(lMessage.parameterName());
        if (Objects.isNull(lParameter)) {
            throw new CUserInteractionException(
                    MessageFormat.format("Parameter [{0}] does not exist!", lMessage.parameterName()));
        } else if (!Objects.isNull(lParameter.value())) {
            throw new CUserInteractionException(
                    MessageFormat.format("Parameter [{0}] already set!", lMessage.parameterName()));
        }

        lParameter.setValue(iMessage.get());

        mWorkflow.emptyVariables().remove(lMessage.parameterName());

        if (mWorkflow.emptyVariables().isEmpty()) {
            mWorkflow.setStatus(EWorkflowStatus.ACTIVE);
        }
    }

    private void processSuccess(IRequest pRequest) throws IOException {

        if (pRequest.responseMediaType().equals(MediaType.APPLICATION_JSON)) {
            if (!(Objects.isNull(mTask.assignTask()))) {
                mTask.assignTask().setJsonSource(mapper.readTree(pRequest.response()));

                EWorkflowTaskFactory.INSTANCE.factory(mWorkflow, mTask.assignTask()).apply(mWorkflow.execution());
            }
        } else if (pRequest.responseMediaType().equals(MediaType.TEXT_PLAIN)) {
            mTask.assignTask().setStringSource(pRequest.response());

            EWorkflowTaskFactory.INSTANCE.factory(mWorkflow, mTask.assignTask()).apply(mWorkflow.execution());
        }
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
