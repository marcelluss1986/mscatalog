package com.mdss.mscatalog.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError{

    private List<FieldMessage> errors = new ArrayList<>();

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addErrors(String fieldName, String fieldMessage){
        errors.add(new FieldMessage(fieldName, fieldMessage));
    }
}
