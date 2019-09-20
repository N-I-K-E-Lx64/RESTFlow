package com.restflow.core.Network.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowParser.EParameterFactory;

public class CUserParameterMessage implements IMessage {

    @JsonProperty("workflowInstance")
    private String workflowInstance;
    @JsonProperty("parameter")
    private String parameterName;
    @JsonProperty("type")
    private String parameterType;
    @JsonProperty("value")
    private String parameterValue;


    @Override
    public String getWorkflowInstance() {
        return workflowInstance;
    }

    @Override
    public Object get() {
        return EParameterFactory.INSTANCE.parseParameterValue(parameterValue, parameterType);
    }

    public String parameterName() {
        return parameterName;
    }
}
