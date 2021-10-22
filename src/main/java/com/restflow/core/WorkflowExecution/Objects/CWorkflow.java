package com.restflow.core.WorkflowExecution.Objects;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.ExecutionLogger;
import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskFactory;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CWorkflow implements IWorkflow {

    private String mInstanceName;
    private final String mDefinitionReference;
    private final String mDescription;

    private final Queue<ITaskAction> mExecution = new ConcurrentLinkedQueue<>();

    private final AtomicReference<ITaskAction> mCurrentTask = new AtomicReference<>();

    private final Map<String, IVariable> mVariables;

    private List<IParameter<?>> mEmptyVariables = new LinkedList<>();

    private EWorkflowStatus mStatus;

    // TODO : Create an interface!
    private final AtomicReference<CMonitoringInfo> mMonitoringInfo = new AtomicReference<>();

    /**
     * Clone Constructor
     *
     * @param that The object to copy.
     */
    public CWorkflow(@NonNull final IWorkflow that, @NonNull final Queue<ITask> tasks) {
        this.mInstanceName = that.instance();
        this.mDefinitionReference = that.definition();
        this.mDescription = that.description();
        this.mVariables = Collections.synchronizedMap(resetVariable(that.variables()));
        generateExecutionOrder(resetInput(tasks));

        this.mStatus = EWorkflowStatus.INITIATED;
    }

    public CWorkflow(String pTitle, String pDescription, Map<String, IVariable> pVariables) {
        this.mDefinitionReference = pTitle;
        this.mDescription = pDescription;
        this.mVariables = Collections.synchronizedMap(pVariables);

        this.mStatus = EWorkflowStatus.INITIATED;
    }

    @NonNull
    @Override
    public String instance() {
        return mInstanceName;
    }

    @Override
    public void setInstanceName(@NonNull final String pInstanceName) {
        this.mInstanceName = pInstanceName;
    }

    @NonNull
    @Override
    public String definition() {
        return mDefinitionReference;
    }

    @NonNull
    @Override
    public String description() {
        return mDescription;
    }

    @NonNull
    @Override
    public EWorkflowStatus status() {
        return mStatus;
    }

    @NonNull
    @Override
    public ITaskAction currentTask() {
        return mCurrentTask.get();
    }

    @NonNull
    @Override
    public Map<String, IVariable> variables() {
        return mVariables;
    }

    @NonNull
    @Override
    public List<IParameter<?>> emptyVariables() {
        return mEmptyVariables;
    }

    @NonNull
    @Override
    public Queue<ITaskAction> execution() {
        return mExecution;
    }

    @Override
    public void setQueue(@NonNull Queue<ITask> pExecution) {
        // Queue leeren
        mExecution.clear();
        // ITask Objekte in ITaskAction Objekte umwandeln
        generateExecutionOrder(pExecution);
        // Ausführung starten
        start();
    }

    @Override
    public void setStatus(@NonNull EWorkflowStatus pStatus) {
        this.mStatus = pStatus;
        this.mMonitoringInfo.get().setWorkflowStatus(pStatus).sendMessage();
    }

    @Override
    public void setEmptyVariables(@NonNull List<IParameter<?>> pEmptyVariables) {
        this.mEmptyVariables = pEmptyVariables;
    }

    @Override
    public void generateExecutionOrder(@NonNull Queue<ITask> pTasks) {
        for (ITask lTask : pTasks) {
            mExecution.add(EWorkflowTaskFactory.INSTANCE.factory(this, lTask));
        }
    }

    @Override
    public Queue<ITask> resetInput(@NonNull Queue<ITask> pTasks) {
        pTasks.forEach(task -> {
            if (task instanceof CInvokeServiceTask) {
                ((CInvokeServiceTask) task).resetInput();
            }
        });

        return pTasks;
    }

    @Override
    public Map<String, IVariable> resetVariable(@NonNull Map<String, IVariable> pVariables) {
        pVariables.forEach((key, value) -> value.setValue(null));

        return pVariables;
    }

    @NonNull
    @Override
    public IWorkflow start() {

        if (mExecution.isEmpty()) {
            throw new CWorkflowExecutionException("Workflow " + this.mDefinitionReference + " enthält keine Tasklist!");
        }

        // Execution has started
        mStatus = EWorkflowStatus.ACTIVE;

        // Create initial monitoring object
        mMonitoringInfo.set(new CMonitoringInfo(mInstanceName, LocalDateTime.now()));

        this.executeStep();
        return this;
    }

    /**
     * Methode, die immer eine Aufgabe der Queue ausführt
     */
    @Override
    public void executeStep() {

        if (mStatus == EWorkflowStatus.ACTIVE) {
            ITaskAction lCurrentTask = mExecution.element();
            // Update the current Task reference
            mCurrentTask.set(lCurrentTask);
            ExecutionLogger.INSTANCE.info(this.mInstanceName, "Executing: " + lCurrentTask.title());

            // Update the monitoring object with the new current task
            mMonitoringInfo.get().setCurrentActivity(lCurrentTask.title()).sendMessage();

            // Den Head der Queue ausführen und wenn true geliefert wird, muss auf eine Nachricht gewartet werden
            if (mExecution.element().apply(mExecution)) {
                return;
            }

            //... wenn false übergeben wird, wird Element aus der Queue entfernt
            if (!mExecution.isEmpty()) {
                mExecution.remove();
                this.postAction();
            }
        }
    }

    /**
     * Methode, die nach jeder Ausführung einer Task-Action ausgeführt wird, um zu überprüfen, ob der Workflow fortgesetzt wird.
     */
    @Override
    public void postAction() {

        //Wenn die Queue nicht leer ist, nächsten Schritt ausführen
        if (!mExecution.isEmpty()) {
            this.executeStep();
            return;
        }

        // Queue was successfully processed -> Set workflow status to COMPLETE
        this.setStatus(EWorkflowStatus.COMPLETE);

        // Update the monitoring object
        mMonitoringInfo.get().setWorkflowStatus(EWorkflowStatus.COMPLETE).sendMessage();
    }

    @Override
    public void accept(IMessage pMessage) {

        //Kopf der Queue holen und Nachricht mit aktueller Ausführungsqueue weitergeben
        assert mExecution.peek() != null;
        mExecution.peek().accept(pMessage);
        this.postAction();
    }
}
