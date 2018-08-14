package org.oagi.srt.gateway.http.configuration.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SrtResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity handleEmptyResultDataAccessException(
            EmptyResultDataAccessException ex, WebRequest webRequest) {
        logger.debug(ex.getMessage(), ex);
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest webRequest) {
        logger.debug(ex.getMessage(), ex);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("X-Error-Message", ex.getMessage());
        return new ResponseEntity(headers, HttpStatus.BAD_REQUEST);
    }

}
