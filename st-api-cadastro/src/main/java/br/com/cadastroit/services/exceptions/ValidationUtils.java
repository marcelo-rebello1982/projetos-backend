package br.com.cadastroit.services.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ValidationUtils {

	public static Map<String, Object> processFieldErrors(ConstraintViolationException ex) {

		Map<String, Object> map = new HashMap<String, Object>();

		Set<ConstraintViolation<?>> fieldErrors = ex.getConstraintViolations();

		for (ConstraintViolation<?> fieldError : fieldErrors) {
			map.put(fieldError.getPropertyPath().toString(), fieldError.getMessage());
		}

		return map;
	}

	public static Map<String, Object> processFieldErrors(MethodArgumentNotValidException ex, MessageSource messageSource) {

		Map<String, Object> map = new HashMap<String, Object>();

		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();

		for (FieldError fieldError : fieldErrors)
			map.put(fieldError.getField(), fieldError.getDefaultMessage());

		return map;
	}

}
