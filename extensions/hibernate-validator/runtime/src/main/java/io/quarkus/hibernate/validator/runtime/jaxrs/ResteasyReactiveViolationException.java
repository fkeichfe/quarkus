package io.quarkus.hibernate.validator.runtime.jaxrs;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ResteasyReactiveViolationException extends ConstraintViolationException {
    private static final long serialVersionUID = 657697354453281559L;

    public ResteasyReactiveViolationException(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message, constraintViolations);
    }

    public ResteasyReactiveViolationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(constraintViolations);
    }
}
