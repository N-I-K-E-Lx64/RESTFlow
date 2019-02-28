package com.example.demo.WorkflowExecution.Objects;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskFactory;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.example.demo.WorkflowParser.WorkflowParserObjects.ITask;
import com.example.demo.WorkflowParser.WorkflowParserObjects.IVariable;
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

    public CWorkflow(String pTitle, String pDescription, Map<String, IVariable> pVariables) {
        this.mTitle = pTitle;
        this.mDescription = pDescription;
        this.mVariables = Collections.synchronizedMap(pVariables);
        this.mStatus = EWorkflowStatus.WORKING;
    }

    @NonNull
    @Override
    public String name() {
        return mTitle;
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
    public List<String> emptyVariables() {
        return mEmptyVariables;
    }

    @NonNull
    @Override
    public Queue<ITaskAction> getQueue() {
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

    }

    @Override
    public void accept(IMessage pMessage) {

        //Kopf der Queue holen und Nachricht mit aktueller Ausführungsqueue weitergeben
        mExecution.peek().accept(pMessage);
        this.postAction();
    }
}
