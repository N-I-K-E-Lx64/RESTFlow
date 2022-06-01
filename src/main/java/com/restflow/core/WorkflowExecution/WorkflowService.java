package com.restflow.core.WorkflowExecution;

import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowConversionService;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService implements BiConsumer<WorkflowModel, String>,
    Function<String, IWorkflow>, Supplier<Set<IWorkflow>> {

  private static final Logger logger = LogManager.getLogger(WorkflowService.class);

  private final WorkflowConversionService workflowConversionService;

  private final Map<String, IWorkflow> executingWorkflows;

  @Autowired
  public WorkflowService(WorkflowConversionService workflowConversionService) {
    this.workflowConversionService = workflowConversionService;

    this.executingWorkflows = new ConcurrentHashMap<>();
  }

  @Override
  public void accept(WorkflowModel workflowModel, String instanceId) {
		if (executingWorkflows.containsKey(instanceId)) {
			throw new RuntimeException(
					MessageFormat.format("A workflow instance with id {0} already exists!", instanceId));
		}

    // Convert the workflow to an executable IWorkflow object
    IWorkflow convertedWorkflow = this.workflowConversionService.convertWorkflowModel(
        workflowModel);

    // Put the object in the state and start the execution
    executingWorkflows.put(instanceId, convertedWorkflow.start());

    logger.info("Add new workflow-instance: " + instanceId);
  }

  @Override
  public IWorkflow apply(String instanceId) {
    if (executingWorkflows.containsKey(instanceId)) {
      return executingWorkflows.get(instanceId);
    } else {
      throw new RuntimeException(
          MessageFormat.format("Workflow instance {0} could not be found!", instanceId));
    }
  }

  @Override
  public Set<IWorkflow> get() {
    return new HashSet<>(this.executingWorkflows.values());
  }
}
