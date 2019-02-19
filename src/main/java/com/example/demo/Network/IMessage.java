package com.example.demo.Network;

import com.example.demo.WorkflowParser.WorkflowObjects.IWorkflow;

import java.util.function.Supplier;

public interface IMessage extends Supplier<String> {

    IWorkflow workflow();
}
