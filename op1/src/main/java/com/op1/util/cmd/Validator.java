package com.op1.util.cmd;

public interface Validator<T> {

    public T validate(String option) throws ValidationException;
}
