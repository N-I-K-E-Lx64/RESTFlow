package com.restflow.core.WorkflowParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.CVariable;
import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CConversionService {

  private final ConversionService conversionService;

  private static final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  public CConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  public <T> T convertStringValue(@NonNull final String pValue, @NonNull final Class<T> pType)
      throws JsonProcessingException {
    if (conversionService.canConvert(String.class, pType)) {
      return conversionService.convert(pValue, pType);
    } else if (pType == JsonNode.class) {
      return (T) mapper.readTree(pValue);
    } else {
      throw new CWorkflowParseException(
          MessageFormat.format("Cannot convert {0} to type {1}!", pValue, pType));
    }
  }

  public <S, T> T convertValue(@NonNull final S pValue, @NonNull final Class<T> pType) {
    if (conversionService.canConvert(pValue.getClass(), pType)) {
      return this.conversionService.convert(pValue, pType);
    } else if (pValue.getClass() == CVariable.class) {
      return this.conversionService.convert(((CVariable<?>) pValue).value(), pType);
    } else {
      throw new CWorkflowExecutionException(
          MessageFormat.format("Cannot convert type {0} to type {1}",
              pValue.getClass().getSimpleName(), pType));
    }
  }
}
