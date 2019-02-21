package com.example.demo.WorkflowParser.WorkflowParserObjects;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.example.demo.WorkflowExecution.WorkflowTasks.EWorkflowTaskFactory;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CWorkflow implements IWorkflow {

    private final String mTitle;
    private final String mDescription;

    private Queue<ITaskAction> mExecution = new ConcurrentLinkedQueue<>();

    private AtomicReference<ITaskAction> mCurrentTask = new AtomicReference<>();

    private Map<String, IVariable> mVariables;

    private boolean mIsEverythingOkay;

    public CWorkflow(String pTitle, String pDescription, Map<String, IVariable> pVariables) {
        this.mTitle = pTitle;
        this.mDescription = pDescription;
        this.mVariables = Collections.synchronizedMap(pVariables);
        this.mIsEverythingOkay = true;
    }

    @NonNull
    @Override
    public String name() {
        return mTitle;
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
    public void setWorkflowStatus(@NonNull boolean pIsEverythingOkay) {
        this.mIsEverythingOkay = pIsEverythingOkay;
    }

    @Override
    public void generateExecutionOrder(@NonNull Queue<ITask> pTasks) {
        for (ITask lTask : pTasks) {
            mExecution.add(EWorkflowTaskFactory.INSTANCE.factory(this, lTask));
        }
    }

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

        if (mIsEverythingOkay) {
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
