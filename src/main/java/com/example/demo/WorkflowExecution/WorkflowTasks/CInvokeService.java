package com.example.demo.WorkflowExecution.WorkflowTasks;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowParser.WorkflowParserObjects.CInvokeServiceDefinition;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IParameter;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class CInvokeService extends IBaseTaskAction {

    private final CInvokeServiceDefinition mTask;

    public CInvokeService(IWorkflow pWorkflow, CInvokeServiceDefinition pTask) {
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

    }
}
