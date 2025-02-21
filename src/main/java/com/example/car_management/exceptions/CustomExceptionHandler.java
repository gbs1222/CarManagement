package com.example.car_management.exceptions;

import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof GraphQLException) {
            return GraphQLError.newError()
                    .message(ex.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        } else if (ex instanceof ConstraintViolationException cve) {
            List<String> violations = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());

            return GraphQLError.newError()
                    .message("Validation Error: " + String.join(", ", violations))
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }

        return GraphQLError.newError()
                .message("Internal Server Error")
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}
