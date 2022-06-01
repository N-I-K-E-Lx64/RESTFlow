package com.restflow.core.WorkflowParser;

import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CConversionService {

  private final ConversionService conversionService;

  @Autowired
  public CConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  public <T> T convertValue(@NonNull final String pValue, @NonNull final Class<T> pType) {
    if (conversionService.canConvert(String.class, pType)) {
      return conversionService.convert(pValue, pType);
    } else {
      throw new CWorkflowParseException(
          MessageFormat.format("Cannot convert {0} to type {1}!", pValue, pType));
    }
  }
}
