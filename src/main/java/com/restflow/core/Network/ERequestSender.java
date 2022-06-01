package com.restflow.core.Network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.Network.Objects.CRequest;
import com.restflow.core.Network.Objects.IRequest;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public enum ERequestSender {

  INSTANCE;

  private static final Logger logger = LogManager.getLogger(ERequestSender.class);

  private static final String COLLABORATION_CONTROLLER = "/collaboration/sendMessage";

  /**
   * Function for sending a request to a specified Web service.
   *
   * @param pRequest  IRequest Object
   * @param pWorkflow Corresponding workflow Definition
   * @return IRequest object with the results of the request
   * @throws JsonProcessingException Will be thrown if the request body could not be created
   * @see CRequest
   */
  public IRequest doRequestWithWebClient(IRequest pRequest, IWorkflow pWorkflow)
      throws JsonProcessingException {

    WebClient client = WebClient.create(pRequest.baseUrl());

    String lResponse = client
        .method(pRequest.type())
        .uri(pRequest.resourceUrl())
        .header(HttpHeaders.CONTENT_TYPE, String.valueOf(pRequest.requestMediaType()))
        .accept(pRequest.requestMediaType())
        .body(BodyInserters.fromValue(pRequest.fieldsAsJson()))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse ->
            Mono.error(new CWebClientResponseException(pWorkflow,
                "Response contains an error status code: " + clientResponse.statusCode().value()
                    + " " + clientResponse.statusCode().getReasonPhrase())))
        .bodyToMono(String.class)
        .block();

    logger.info("Response: " + lResponse);
    pRequest.setResponse(Objects.requireNonNull(lResponse));

    return pRequest;
  }

  /**
   * Function for sending a collaboration message to specific system instance.
   *
   * @param pRequestUrl  URL of the target system instance
   * @param pRequestBody Collaboration message
   * @param pWorkflow    Corresponding workflow Definition
   * @see CCollaborationMessage
   */
  public void sendCollaborationJson(String pRequestUrl, CCollaborationMessage pRequestBody,
      IWorkflow pWorkflow) {

    WebClient client = WebClient.create();

    String lResponse = client
        .method(HttpMethod.POST)
        .uri(URI.create(pRequestUrl + COLLABORATION_CONTROLLER))
        .body(BodyInserters.fromValue(pRequestBody))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse ->
            Mono.error(new CWebClientResponseException(pWorkflow,
                "Response contains an error status code: " + clientResponse.statusCode().value()
                    + " " + clientResponse.statusCode().getReasonPhrase())))
        .bodyToMono(String.class)
        .block();

    logger.info(
        MessageFormat.format("Response to the request [{0}] is [{1}]", pRequestUrl, lResponse));
  }

  @ExceptionHandler(CWebClientResponseException.class)
  public void handleWebClientResponseException(CWebClientResponseException ex) {
    ex.workflow().setStatus(EWorkflowStatus.TERMINATED);
    logger.error(ex.getMessage());
  }
}
