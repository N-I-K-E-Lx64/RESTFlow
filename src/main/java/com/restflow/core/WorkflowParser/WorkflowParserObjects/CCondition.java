package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restflow.core.WorkflowExecution.Condition.EConditionType;
import com.restflow.core.WorkflowExecution.Objects.CConditionException;
import com.restflow.core.WorkflowExecution.Objects.CWorkflowExecutionException;
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

        } /*else if ((lFirstParameter instanceof String || lSecondParameter instanceof String) &&
                (mConditionType.equals(EConditionType.BIGGER) || mConditionType.equals(EConditionType.SMALLER) ||
                        mConditionType.equals(EConditionType.NUMBER_EQUALS) || mConditionType.equals(EConditionType.NOT_NUMBER_EQUALS))) {

            throw new CConditionException("No number related comparisons on strings possible!");

        } else if ((!(lFirstParameter instanceof String) || !(lSecondParameter instanceof String) &&
                (mConditionType.equals(EConditionType.STRING_EQUALS) || mConditionType.equals(EConditionType.NOT_STRING_EQUALS)) ||
                mConditionType.equals(EConditionType.STRING_CONTAINS) || mConditionType.equals(EConditionType.NOT_STRING_CONTAINS))) {

            throw new CConditionException("No string related comparisons on numbers possible!");
        }*/

        switch (mConditionType) {
            case SMALLER:
                return doubleParameter(lFirstParameter) < doubleParameter(lSecondParameter);

            case BIGGER:
                return doubleParameter(lFirstParameter) > doubleParameter(lSecondParameter);

            case NUMBER_EQUALS:
                return doubleParameter(lFirstParameter) == doubleParameter(lSecondParameter);

            case NOT_NUMBER_EQUALS:
                return !(doubleParameter(lFirstParameter) == doubleParameter(lSecondParameter));

            case STRING_EQUALS:
                return stringParameter(lFirstParameter).equals(stringParameter(lSecondParameter));

            case NOT_STRING_EQUALS:
                return !(stringParameter(lFirstParameter).equals(stringParameter(lSecondParameter)));

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

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(pVariable.value());
        } catch (JsonProcessingException ex) {
            throw new CWorkflowExecutionException("Variable cannot be parsed to string!", ex);
        }
    }
}
