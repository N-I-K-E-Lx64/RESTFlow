package com.restflow.core.WorkflowExecution;

import com.restflow.core.ModelingTool.model.WorkflowModel;
import com.restflow.core.WorkflowExecution.Objects.CWorkflow;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService implements BiConsumer<WorkflowModel, String>,
    Function<String, IWorkflow>, Supplier<Set<IWorkflow>> {

  private static final Logger logger = LogManager.getLogger(WorkflowService.class);

  private final WorkflowConversionService workflowConversionService;

  // private final Map<UUID, IWorkflow> workflowDefinitions;
  private final Map<String, IWorkflow> executingWorkflows;

  @Autowired
  public WorkflowService(WorkflowConversionService workflowConversionService) {
    this.workflowConversionService = workflowConversionService;

    // this.workflowDefinitions = new ConcurrentHashMap<>();
    this.executingWorkflows = new ConcurrentHashMap<>();
  }

  @Override
  public void accept(WorkflowModel workflowModel, String instanceId) {
    if (executingWorkflows.containsKey(instanceId)) {
      throw new RuntimeException(
          MessageFormat.format("A workflow instance with id {0} already exists!", instanceId));
    }

    // Convert the workflow model to a usable IWorkflow object, set its instance name and start the execution
    IWorkflow workflow = this.workflowConversionService.convertWorkflowModel(workflowModel)
        .setInstanceName(instanceId)
        .start();
    // this.workflowDefinitions.put(workflow.modelReference(), workflow);

    // Put the object in the state
    executingWorkflows.put(instanceId, workflow);

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

  public void remove(@NonNull final String instanceId) {
    this.executingWorkflows.remove(instanceId);

    logger.info("Removed workflow instance " + instanceId);
  }

  /**
   * Restarts a workflow instance by creating a deep copy of the old instance. During this process
   * the execution queue is resettet.
   *
   * @param instanceId ID of the instance that should be restarted.
   */
  public void restart(@NonNull final String instanceId) {
    if (!executingWorkflows.containsKey(instanceId)) {
      throw new RuntimeException(
          MessageFormat.format("Workflow instance {0} is not part of the active instances!",
              instanceId));
    }

    IWorkflow oldInstance = executingWorkflows.get(instanceId);
    // Creates a deep copy
    IWorkflow newInstance = new CWorkflow(oldInstance,
        this.workflowConversionService.apply(oldInstance.modelReference()));

    executingWorkflows.replace(instanceId, oldInstance, newInstance.start());
  }
}
