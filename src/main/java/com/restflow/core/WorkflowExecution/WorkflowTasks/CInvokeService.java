package com.restflow.core.WorkflowExecution.WorkflowTasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Network.ERequestSender;
import com.restflow.core.Network.ERequestTypeBuilder;
import com.restflow.core.Network.IMessage;
import com.restflow.core.Network.Objects.CRequest;
import com.restflow.core.Network.Objects.CUserParameterMessage;
import com.restflow.core.Network.Objects.IRequest;
import com.restflow.core.WorkflowExecution.Objects.CUserInteractionException;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.IParameter;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Tasks.CInvokeServiceTask;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

public class CInvokeService extends IBaseTaskAction {

  private static final ObjectMapper mapper = new ObjectMapper();
  private final CInvokeServiceTask mTask;

  CInvokeService(IWorkflow pWorkflow, CInvokeServiceTask pTask) {
    super(pWorkflow);
    mTask = pTask;
  }

  /**
   * This function generates and executes a request
   *
   * @param iTaskActions Execution queue
   * @return Boolean value that represents the need to pause execution of this workflow instance
   * until a particular message is received
   */
  @Override
  public Boolean apply(Queue<ITaskAction> iTaskActions) {

    // Fasst alle leeren Variablen in einer Liste zusammen
    List<IParameter<?>> emptyVariables = mTask.parameters().values().stream()
        .filter(iParameter -> Objects.isNull(iParameter.value()))
        //.map(IParameter::name)
        .collect(Collectors.toList());

    // Request kann nur ausgeführt werden, wenn alle Variablen belegt sind!
    if (!emptyVariables.isEmpty()) {
      mWorkflow.setStatus(EWorkflowStatus.SUSPENDED);
      mWorkflow.setEmptyVariables(emptyVariables);

      return true;
    }

    String lBaseUrl = mTask.api().baseUri().value();
    String lResourceUrl = mTask.api().resources().get(mTask.resourceIndex()).relativeUri().value();
    String lRequestType = mTask.api().resources().get(mTask.resourceIndex()).methods().get(0)
        .method();
    MediaType lRequestMediaType =
        MediaType.parseMediaType(
            mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).body().get(0)
                .name());
    MediaType lResponseMediaType =
        MediaType.parseMediaType(
            mTask.api().resources().get(mTask.resourceIndex()).methods().get(0).responses().get(0)
                .body().get(0).name());

    // Erstellt aus den extrahierten Informationen ein IRequest Objekt
    IRequest lRequest = new CRequest(lBaseUrl, lResourceUrl,
        ERequestTypeBuilder.INSTANCE.createHttpMethodFromString(lRequestType), lRequestMediaType,
        lResponseMediaType, mTask.parameters());

    // Führt die Anfrage aus
    try {
      processSuccess(Objects.requireNonNull(
          ERequestSender.INSTANCE.doRequestWithWebClient(lRequest, mWorkflow)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Processes an incoming message
   *
   * @param iMessage Incoming message
   * @see CUserParameterMessage
   */
  @Override
  public void accept(IMessage iMessage) {

    CUserParameterMessage lMessage = (CUserParameterMessage) iMessage;

    CParameter<?> lParameter = (CParameter<?>) mTask.parameters().get(lMessage.parameterName());
    if (Objects.isNull(lParameter)) {
      throw new CUserInteractionException(
          MessageFormat.format("Parameter [{0}] does not exist!", lMessage.parameterName()));
    } else if (Objects.nonNull(lParameter.value())) {
      throw new CUserInteractionException(
          MessageFormat.format("Parameter [{0}] already set!", lMessage.parameterName()));
    }

    lParameter.setValue(lMessage.get());

    mWorkflow.emptyVariables().remove(lParameter);

    if (mWorkflow.emptyVariables().isEmpty()) {
      mWorkflow.setStatus(EWorkflowStatus.ACTIVE);
    }
  }

  /**
   * Processes the result of a web service call
   *
   * @param pRequest IRequest object containing the results
   * @throws IOException Is thrown if the result cannot be stored in a variable
   */
  private void processSuccess(IRequest pRequest) throws IOException {
    mTask.target().setValue(pRequest.response());
  }

  @NonNull
  @Override
  public UUID id() {
    return mTask.id();
  }

  @NonNull
  @Override
  public String title() {
    return mTask.title();
  }
}
