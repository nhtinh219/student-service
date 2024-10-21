package com.kienlong.api.studentservice.error;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.kienlong.api.studentservice.i18n.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleJwtValidationException(HttpServletRequest request, JwtValidationException ex) {

        LOGGER.error(ex.getMessage(), ex);

        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        return error;

    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex, Locale locale) {

        LOGGER.error(ex.getMessage(), ex);

        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.addError(messageSource.getMessage(MessageCode.ERROR_BAD_CREDENTIALS.getCode(), null, locale));
        error.setPath(request.getServletPath());

        return error;

    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleAuthenticationException(HttpServletRequest request, AuthenticationException ex, Locale locale) {

        LOGGER.error(ex.getMessage(), ex);

        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.addError(messageSource.getMessage(MessageCode.ERROR_AUTH_REQUIRED.getCode(), null, locale));
        error.setPath(request.getServletPath());

        return error;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleAuthenticationException(HttpServletRequest request, AccessDeniedException ex, Locale locale) {

        LOGGER.error(ex.getMessage(), ex);

        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.addError(messageSource.getMessage(MessageCode.ERROR_ACCESS_DENIED.getCode(), null, locale));
        error.setPath(request.getServletPath());

        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {

        LOGGER.error(ex.getMessage(), ex);

        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        return error;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        LOGGER.error(ex.getMessage(), ex);

        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(((ServletWebRequest) request).getRequest().getServletPath());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(fieldError -> error.addError(fieldError.getDefaultMessage()));

        return new ResponseEntity<>(error, headers, status);
    }


}
