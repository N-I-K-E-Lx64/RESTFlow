package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.WorkflowExecution.Condition.EConditionType;
import com.restflow.core.WorkflowExecution.Objects.CConditionException;
import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
import com.restflow.core.WorkflowParser.WorkflowParserObjects.Variables.CJsonVariable;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CCondition implements ICondition {

    private final EConditionType mConditionType;
    private AtomicReference<IParameter> mFirstParameter = new AtomicReference<>();
    private AtomicReference<IParameter> mSecondParameter = new AtomicReference<>();

    public CCondition(@NonNull final EConditionType pConditionType, IParameter pFirstParameter, IParameter pSecondParaeter) {
        this.mConditionType = pConditionType;
        this.mFirstParameter.set(pFirstParameter);
        this.mSecondParameter.set(pSecondParaeter);
    }

    @NonNull
    @Override
    public Boolean execute() {

        Object lFirstParameter = mFirstParameter.get().value();
        Object lSecondParameter = mSecondParameter.get().value();

        if (lFirstParameter instanceof IVariable) {
            lFirstParameter = deserializeVariable((IVariable) lFirstParameter);
        }

        if (lSecondParameter instanceof IVariable) {
            lSecondParameter = deserializeVariable((IVariable) lSecondParameter);
        }

        if (Objects.isNull(lFirstParameter) || Objects.isNull(lSecondParameter)) {

            throw new CConditionException("No decision with a null value is possible!");

        }

        switch (mConditionType) {
            case SMALLER:
                return doubleParameter(lFirstParameter) < doubleParameter(lSecondParameter);

            case BIGGER:
                return doubleParameter(lFirstParameter) > doubleParameter(lSecondParameter);

            case GREATER_OR_EQUALS:
                return doubleParameter(lFirstParameter) >= doubleParameter(lSecondParameter);

            case LESS_OR_EQUALS:
                return (doubleParameter(lFirstParameter) <= doubleParameter(lSecondParameter));

            case EQUALS:
                return lFirstParameter.equals(lSecondParameter);

            case NOT_EQUALS:
                return !(lFirstParameter.equals(lSecondParameter));

            case STRING_CONTAINS:
                return stringParameter(lFirstParameter).contains(stringParameter(lSecondParameter));

            case NOT_STRING_CONTAINS:
                return !(stringParameter(lFirstParameter).contains(stringParameter(lSecondParameter)));

            default:
                throw new CConditionException(MessageFormat.format("Condition Type [{0}] unknown", mConditionType));
        }
    }

    private Double doubleParameter(Object pNumber) {
        return (Double) pNumber;
    }

    private String stringParameter(Object pString) {
        return (String) pString;
    }

    private String deserializeVariable(IVariable pVariable) {

        if (pVariable instanceof CJsonVariable) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(pVariable.value());
            } catch (JsonProcessingException ex) {
                throw new CWorkflowExecutionException("Variable cannot be parsed to string!", ex);
            }
        } else {
            return (String) pVariable.value();
        }
    }
}
