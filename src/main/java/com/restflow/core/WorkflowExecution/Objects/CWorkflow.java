package com.restflow.core.WorkflowExecution.Objects;

import com.restflow.core.Network.IMessage;
import com.restflow.core.WorkflowExecution.WorkflowTasks.EWorkflowTaskFactory;
import com.restflow.core.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CInvokeServiceTask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.ITask;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IVariable;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CWorkflow implements IWorkflow {

    private final String mTitle;
    private final String mDescription;

    private Queue<ITaskAction> mExecution = new ConcurrentLinkedQueue<>();

    private AtomicReference<ITaskAction> mCurrentTask = new AtomicReference<>();

    private Map<String, IVariable> mVariables;

    private List<String> mEmptyVariables = new LinkedList<>();

    private EWorkflowStatus mStatus;

    /**
     * Clone Constructor
     * @param that
     */
    public CWorkflow(@NonNull final IWorkflow that, @NonNull final Queue<ITask> tasks) {
        this.mTitle = that.title();
        this.mDescription = that.description();
        this.mVariables = Collections.synchronizedMap(resetVariable(that.variables()));
        generateExecutionOrder(resetInput(tasks));

        this.mStatus = EWorkflowStatus.WORKING;
    }

    public CWorkflow(String pTitle, String pDescription, Map<String, IVariable> pVariables) {
        this.mTitle = pTitle;
        this.mDescription = pDescription;
        this.mVariables = Collections.synchronizedMap(pVariables);

        this.mStatus = EWorkflowStatus.WORKING;
    }

    @NonNull
    @Override
    public String title() {
        return mTitle;
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
    public List<String> emptyVariables() {
        return mEmptyVariables;
    }

    @NonNull
    @Override
    public Queue<ITaskAction> execution() {
        return mExecution;
    }

    @Override
    public void setQueue(@NonNull Queue<ITaskAction> pExecution) {
        this.mExecution = pExecution;
    }

    @Override
    public void setStatus(@NonNull EWorkflowStatus pStatus) {
        this.mStatus = pStatus;
    }

    @Override
    public void setEmptyVariables(List<String> pEmptyVariables) {
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
        pVariables.forEach((key, value) -> {
            value.setValue(null);
        });

        return pVariables;
    }

    @NonNull
    @Override
    public IWorkflow start() {

        if (mExecution.isEmpty()) {
            throw new CWorkflowExecutionException("Workflow " + this.mTitle + " enthält keine Tasklist!");
        }

        this.executeStep();
        return this;
    }

    /**
     * Methode, die immer eine Aufgabe der Queue ausführt
     */
    @Override
    public void executeStep() {

        if (mStatus == EWorkflowStatus.WORKING) {
            mCurrentTask.set(mExecution.element());

            //Den Head der Queue ausführen und wenn true geliefert wird, muss auf eine Nachricht gewartet werden
            if (mExecution.element().apply(mExecution)) {
                return;
            }

            //... wenn false übergeben wird, wird Element aus der Queue entfernt
            mExecution.remove();
            this.postAction();
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

        setStatus(EWorkflowStatus.FINISHED);
    }

    @Override
    public void accept(IMessage pMessage) {

        //Kopf der Queue holen und Nachricht mit aktueller Ausführungsqueue weitergeben
        mExecution.peek().accept(pMessage);
        this.postAction();
    }
}
