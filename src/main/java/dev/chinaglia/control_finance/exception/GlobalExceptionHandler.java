package dev.chinaglia.control_finance.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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

	    ErrorMessageResponse error = new ErrorMessageResponse(Instant.now(),HttpStatus.CONFLICT.value(),HttpStatus.CONFLICT.getReasonPhrase(),exception.getMessage(),request.getRequestURI());

	    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorMessageResponse> handleUsuarioJaCadastrado(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {

	    ErrorMessageResponse error = new ErrorMessageResponse(Instant.now(),HttpStatus.METHOD_NOT_ALLOWED.value(),HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),exception.getMessage(),request.getRequestURI());

	    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	
	@ExceptionHandler(CategoriaNaoEncontradaException.class)
	public ResponseEntity<ErrorMessageResponse> handleCategoriaNaoEncontrada(CategoriaNaoEncontradaException exception, HttpServletRequest request) {
	    ErrorMessageResponse error = new ErrorMessageResponse( Instant.now(), HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND.getReasonPhrase(),exception.getMessage(),request.getRequestURI());
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(PrioridadeNaoEncontradaException.class)
	public ResponseEntity<ErrorMessageResponse> handlePrioridadeNaoEncontrada(PrioridadeNaoEncontradaException exception, HttpServletRequest request) {

	    ErrorMessageResponse error = new ErrorMessageResponse(Instant.now(),HttpStatus.CONFLICT.value(),HttpStatus.CONFLICT.getReasonPhrase(),exception.getMessage(),request.getRequestURI());

	    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(DespesaNaoEncontradaException.class)
	public ResponseEntity<ErrorMessageResponse> handleDespesaNaoEncontrada(DespesaNaoEncontradaException exception, HttpServletRequest request) {

	    ErrorMessageResponse error = new ErrorMessageResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),exception.getMessage(),request.getRequestURI());

	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(UsuarioNaoEncontradoException.class)
	public ResponseEntity<ErrorMessageResponse> handleDespesaNaoEncontrada(UsuarioNaoEncontradoException exception, HttpServletRequest request) {

	    ErrorMessageResponse error = new ErrorMessageResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),exception.getMessage(),request.getRequestURI());

	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(UsuarioNaoAutenticadoException.class)
	public ResponseEntity<ErrorMessageResponse> handleUsuarioNaoAutenticado(UsuarioNaoAutenticadoException exception,HttpServletRequest request) {

	    ErrorMessageResponse error = new ErrorMessageResponse(Instant.now(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), exception.getMessage(),request.getRequestURI());

	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}
	
}
