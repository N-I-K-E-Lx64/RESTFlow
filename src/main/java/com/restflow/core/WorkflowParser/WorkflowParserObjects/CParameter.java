package com.restflow.core.WorkflowParser.WorkflowParserObjects;

import com.restflow.core.WorkflowParser.CConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.lang.NonNull;

@Configurable
public class CParameter<T> implements IParameter<T> {

    private T mValue;
    private final Class<T> mType;
    private final String mParameterId;
    private final Boolean mIsUserParameter;

    @Autowired
    private CConversionService conversionService;

    /**
     * CTor for creating a generic parameter
     *
     * @param pParameterId     ID to identify the parameter
     * @param pIsUserParameter Boolean that describes whether the value of this parameter must be set by a user.
     * @param pType            Representation of the parameter type
     */
    public CParameter(@NonNull final String pParameterId,
                      @NonNull final Boolean pIsUserParameter,
                      @NonNull final Class<T> pType) {
        this.mParameterId = pParameterId;
        this.mIsUserParameter = pIsUserParameter;
        this.mType = pType;
        this.mValue = null;
    }

    @NonNull
    @Override
    public String id() {
        return mParameterId;
    }

    @NonNull
    @Override
    public T value() {
        return mValue;
    }

    @Override
    public IParameter<T> setValue(String pValue) {
        mValue = conversionService.convertValue(pValue, mType);

        return this;
    }

    public Boolean isUserParameter() {
        return mIsUserParameter;
    }
}
