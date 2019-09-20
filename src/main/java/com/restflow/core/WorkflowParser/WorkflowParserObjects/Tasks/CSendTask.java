package com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskType;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

public class CSendTask implements ITask {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final String mTitle;
    private final String mTargetSystemUrl;
    private final String mTargetWorkflow;
    private final AtomicReference<IVariable> mSourceReference;
    private final int mActivityId;

    private final EWorkflowTaskType mTaskType;

    public CSendTask(@NonNull final String mTargetSystemUrl, @NonNull final String mTargetWorkflow,
                     @NonNull final IVariable pSourceVariable, final int mActivityId) {
        this.mTargetSystemUrl = mTargetSystemUrl;
        this.mTargetWorkflow = mTargetWorkflow;
        this.mSourceReference = new AtomicReference<>(pSourceVariable);
        this.mActivityId = mActivityId;

        mTitle = MessageFormat.format(
                "Sending variable [{0}] to [{1}]", pSourceVariable.name(), mTargetSystemUrl);

        mTaskType = EWorkflowTaskType.SEND;
    }

    @NonNull
    @Override
    public Object raw() {
        return this;
    }

    @NonNull
    @Override
    public String title() {
        return mTitle;
    }

    @NonNull
    @Override
    public EWorkflowTaskType taskType() {
        return mTaskType;
    }

    @NonNull
    public String targetSystemUrl() {
        return mTargetSystemUrl;
    }

    @NonNull
    public IVariable sourceVariable() {
        return mSourceReference.get();
    }

    @NonNull
    public CCollaborationMessage createCollaboration() throws JsonProcessingException {
        String lVariableValue = mapper.writeValueAsString(mSourceReference.get().value());

        return new CCollaborationMessage(mTargetWorkflow, lVariableValue, mActivityId);
    }
}
