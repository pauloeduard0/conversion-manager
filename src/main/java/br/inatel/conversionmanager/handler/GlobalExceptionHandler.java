package br.inatel.conversionmanager.handler;

import br.inatel.conversionmanager.exception.CurrencyConversionException;
import br.inatel.conversionmanager.exception.CurrencyNotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Method Argument Not Valid Exception");
        problemDetail.setType(URI.create("https://api.quotationmanagement.com/errors/bad-reques"));
        return problemDetail;
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ProblemDetail handleStockNotFoundException(CurrencyNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Stock Not Found for Registration");
        problemDetail.setType(URI.create("https://api.quotationmanagement.com/errors/not-found"));
        return problemDetail;
    }

    @ExceptionHandler(CurrencyConversionException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail handleStockManagerConnectionException(CurrencyConversionException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        problemDetail.setTitle("Stock Manager Connection Exception");
        problemDetail.setType(URI.create("https://api.quotationmanagement.com/errors/service-unavailable"));
        return problemDetail;
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidFormatException(InvalidFormatException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid Format Exception");
        problemDetail.setType(URI.create("https://api.quotationmanagement.com/errors/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleJsonMappingException(JsonMappingException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Json Mapping Exception");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleWebExchangeBindException(WebExchangeBindException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Web Exchange Bind Exception");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(JDBCConnectionException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail handleJDBCConnectionException(JDBCConnectionException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        problemDetail.setTitle("JDBC Connection Exception");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/service-unavailable"));
        return problemDetail;
    }

}
