package com.example.demo.WorkflowParser.WorkflowObjects;

import java.util.Queue;

public class CSwitchTask implements ITask {

    private ICondition condition;
    private Queue<ITask> conditionTrue;
    private Queue<ITask> conditionFalse;

    public CSwitchTask(ICondition condition, Queue<ITask> conditionTrue, Queue<ITask> conditionFalse) {
        this.condition = condition;
        this.conditionTrue = conditionTrue;
        this.conditionFalse = conditionFalse;
    }
}
