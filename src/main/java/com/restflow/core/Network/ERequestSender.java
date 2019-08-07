package com.restflow.core.Network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.Network.Objects.CCollaborationMessage;
import com.restflow.core.Network.Objects.IRequest;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Objects;

public enum ERequestSender {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ERequestSender.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public IRequest doRequestWithWebClient(IRequest pRequest, IWorkflow pWorkflow) throws JsonProcessingException {

        WebClient client = WebClient.create(pRequest.baseUrl());

        String lResponse = client
                .method(pRequest.type())
                .uri(pRequest.resourceUrl())
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(pRequest.requestMediaType()))
                .accept(pRequest.requestMediaType())
                .body(BodyInserters.fromObject(pRequest.fieldsAsJson()))
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

    @ExceptionHandler(CWebClientResponseException.class)
    public void handleWebClientResponseException(CWebClientResponseException ex) {
        ex.workflow().setStatus(EWorkflowStatus.TERMINATED);
        logger.error(ex.getMessage());
    }
}
