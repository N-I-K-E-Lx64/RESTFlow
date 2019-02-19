package com.example.demo.WorkflowParser.WorkflowObjects;

import com.example.demo.Network.IMessage;
import com.example.demo.WorkflowExecution.WorkflowTasks.ITaskAction;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CWorkflow implements IWorkflow {

    private final String mTitle;
    private final String mDescription;

    private Queue<ITaskAction> mExecution = new ConcurrentLinkedQueue<>();

    private AtomicReference<ITaskAction> mCurrentTask = new AtomicReference<>();

    private Map<String, JsonNode> variables = Collections.synchronizedMap(new HashMap<>());

    public CWorkflow(@NonNull String pTitle, String pDescription) {
        this.mTitle = pTitle;
        this.mDescription = pDescription;
    }

    @NonNull
    @Override
    public String title() {
        return mTitle;
    }

    @Override
    public Queue<ITaskAction> getQueue() {
        return mExecution;
    }

    @Override
    public void setQueue(Queue<ITaskAction> pExecution) {
        this.mExecution = pExecution;
    }

    @Override
    public IWorkflow start() {

        if (mExecution.isEmpty()) {
            throw new CWorkflowExecutionException("Workflow " + this.title() + "enthält keine Tasklist!");
        }

        this.executeStep();
        return this;
    }

    /**
     * Methode, die immer eine Aufgabe der Queue ausführt
     */
    @Override
    public void executeStep() {

        mCurrentTask.set(mExecution.element());

        // Den Head der Queue ausführen und wenn true geliefert wird, muss auf eine Nachricht gewartet werden
        if (mExecution.element().apply(mExecution)) {
            return;
        }

        //... wenn false gelieert wird Element aus der Queue entfernen
        mExecution.remove();
        this.postAction();
    }

    /**
     * Methode, die nach jeder Ausführung einer Task-Action ausgeführt wird, um zu überprüfen, ob das Spiel fortgesetzt wird.
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

        // Kopf der Queue holen und Nachricht mit aktueller Ausführungsqueue weitergeben
        mExecution.remove().accept(mExecution, pMessage);
        this.postAction();
    }
}
