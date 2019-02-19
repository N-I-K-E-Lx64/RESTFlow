package com.example.demo.Network;

import com.example.demo.WorkflowParser.WorkflowParserObjects.IWorkflow;

import java.util.function.Supplier;

public interface IMessage extends Supplier<String> {

    IWorkflow workflow();
}
