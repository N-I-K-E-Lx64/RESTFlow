package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CAssignTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CJsonVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CStringVariable;
import org.springframework.lang.NonNull;

import java.util.Queue;

public class CAssign extends IBaseTaskAction {

    private final CAssignTask mTask;

    CAssign(IWorkflow pWorkflow, CAssignTask pTask) {
        super(pWorkflow);
        mTask = pTask;
    }

    @Override
    public void accept(IMessage iMessage) {

    }

    @Override
    public Boolean apply(Queue<ITaskAction> iTaskActions) {

        if (mTask.target() instanceof CJsonVariable) {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode lParameterNode = mapper.createObjectNode();
            lParameterNode.putPOJO(mTask.source().name(), mTask.source().value());
            mTask.target().setValue(lParameterNode);
        } else if (mTask.target() instanceof CStringVariable) {
            mTask.target().setValue(mTask.source().value());
        }

        return false;
    }

    @NonNull
    @Override
    public String title() {
        return mTask.title();
    }
}
