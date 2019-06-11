package com.restflow.core.Network;

import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowExecution.Objects.EWorkflowStatus;
import com.restflow.core.WorkflowExecution.Objects.IWorkflow;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.MessageFormat;

public enum ERequestSender {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ERequestSender.class);

    private static final MediaType JSON = MediaType.get("application/json");

    private final OkHttpClient mClient = new OkHttpClient();

    public IResponse buildRequest(IRequest pRequest, IWorkflow pWorkflow) {

        Request.Builder builder = new Request.Builder();
        builder.url(pRequest.baseUrl());

        switch (pRequest.type()) {
            case GET:
                builder.get();

                logger.info("Sending request to: " + pRequest.baseUrl());
                break;

            case POST:
                String lJson = pRequest.fieldsAsJson();
                if (!lJson.equals("")) {
                    RequestBody lBody = RequestBody.create(JSON, lJson);
                    builder.post(lBody);

                    logger.info("Sending request to: " + pRequest.baseUrl() + " with JSON: " + pRequest.fieldsAsJson());
                }
                break;

            default:
                throw new RuntimeException(MessageFormat.format("Unknown Http-Type: [{0}]!", pRequest.type()));

        }

        Request lRequest = builder.build();

        try (Response lResponse = mClient.newCall(lRequest).execute()) {
            if (!lResponse.isSuccessful()) {
                pWorkflow.setStatus(EWorkflowStatus.ERROR);
                logger.error("Request could not be executed successfully!" + lResponse);
                throw new CWorkflowExecutionException("Request could not be executed successfully!");
            }

            return new CResponse(lResponse.body().contentType().toString(), lResponse.body().string());
        } catch (IOException ex) {
            pWorkflow.setStatus(EWorkflowStatus.ERROR);
            logger.error("Request could not be executed successfully" + ex);
            throw new CWorkflowExecutionException("Request could not be executed successfully!", ex);
        }
    }

    public IResponse buildRequestWebClient(IRequest pRequest, IWorkflow pWorkflow) {

        WebClient client = WebClient.create(pRequest.baseUrl());

        BodyInserter<MultiValueMap, ClientHttpRequest> inserter = BodyInserters.fromMultipartData(pRequest.fields());

        String response = client
                .method(pRequest.type())
                .uri(pRequest.baseUrl())
                .body(inserter)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse ->
                        Mono.error(new CWebClientResponseException(pWorkflow,
                                "Response contains an error status code: " + clientResponse.statusCode().value()
                                        + " " + clientResponse.statusCode().getReasonPhrase())))
                .bodyToMono(String.class)
                .block();

        /*CResponse wird von nun an nicht mehr gebraucht, da mithilfe der Retrieve-Methode kein Zugriff auf die Response
        Header möglich ist.
        Anhand des RAML-Files kann allerdings der Media-Type extrahiert werden. Diese Information können wir ebenfalls
        für die Generierung der Request benutzen (*Muss aber nicht sein*). Mithilfe des extrahierten Media-Types wird
        anschließend die Art der Variable bestimmt!
         */
        return null;
    }

    @ExceptionHandler(CWebClientResponseException.class)
    public void handleWebClientResponseException(CWebClientResponseException ex) {
        ex.workflow().setStatus(EWorkflowStatus.ERROR);
        logger.error(ex.getMessage());
    }
}
