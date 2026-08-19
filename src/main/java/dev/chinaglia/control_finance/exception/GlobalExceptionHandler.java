package dev.chinaglia.control_finance.exception;

import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<ErrorMessageResponse>> handleValidacao(
	        MethodArgumentNotValidException exception) {

	    List<ErrorMessageResponse> erros = exception.getBindingResult()
	            .getFieldErrors()
	            .stream()
	            .map(error -> new ErrorMessageResponse(
	                    Instant.now(),
	                    HttpStatus.BAD_REQUEST.value(),
	                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
	                    error.getDefaultMessage(),
	                    null
	            ))
	            .toList();

	    return ResponseEntity
	            .badRequest()
	            .body(erros);
	}
	
	@ExceptionHandler(UsuarioJaCadastradoException.class)
	public ResponseEntity<ErrorMessageResponse> handleUsuarioJaCadastrado(UsuarioJaCadastradoException exception, HttpServletRequest request) {

	    ErrorMessageResponse error = new ErrorMessageResponse(
	            Instant.now(),
	            HttpStatus.CONFLICT.value(),
	            HttpStatus.CONFLICT.getReasonPhrase(),
	            exception.getMessage(),
	            request.getRequestURI()
	    );

	    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
}
