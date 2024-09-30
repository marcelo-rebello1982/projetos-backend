package br.com.cadastroit.services.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

public class GenericException extends PersistenceException {


	private static final long serialVersionUID = 1475901192341575891L;

	public GenericException(String message) {

		super(message);
	}

	public GenericException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
		message = "[Generic Internal errors] = ";
		throw new GenericException(message);
	}

	public static String buildMessage(String message, Throwable cause) {

		if (cause.getCause() != null && cause.getCause().getClass().equals(ConstraintViolationException.class)) {
			ConstraintViolationException e = (ConstraintViolationException) cause.getCause();
			if (e.getCause() != null && e.getCause().getClass().equals(SQLIntegrityConstraintViolationException.class)) {
				SQLIntegrityConstraintViolationException c = (SQLIntegrityConstraintViolationException) e.getCause();
				if (c.getCause() != null && c.getCause().getClass().equals(SQLIntegrityConstraintViolationException.class)) {
					ConstraintViolationException sql = (ConstraintViolationException) c.getCause();
					message += "[Internal errors] = " + sql.getMessage() + "[ConstraintName] = " + e.getConstraintName() + "[ORACLE] = " + e.getSQL();
				} else {

					message = e.getCause().getCause().getLocalizedMessage().substring(0, 10).concat(e.getCause().toString());
					message = message.substring(0, 10) + " restricao exclusiva "
							+ message.substring(message.indexOf('('), message.lastIndexOf(')') + 1) + " violada ";
					message = "[Erro no processamento de dados] = " + message + ", SQL = " + e.getSQL() + ", Violacao = " + message;
				}
			} else {
				message += ". [Generic Internal errors] = " + e.getMessage();
			}
		}
		throw new GenericException(message);
	}

	public GenericException(Throwable cause) {

		super(cause);
		String message = "";
		if (cause.getCause() != null && cause.getCause().getClass().equals(PersistenceException.class)) {
			PersistenceException e = (PersistenceException) cause.getCause();
			if (e.getCause() != null && e.getCause().getClass().equals(ConstraintViolationException.class)) {
				ConstraintViolationException c = (ConstraintViolationException) e.getCause();
				if (c.getCause() != null && c.getCause().getClass().equals(SQLIntegrityConstraintViolationException.class)) {
					SQLIntegrityConstraintViolationException sql = (SQLIntegrityConstraintViolationException) c.getCause();
					message += ". [Internal errors] = " + sql.getMessage() + ", ConstraintName = " + c.getConstraintName() + ", SQL = " + c.getSQL();
				} else {
					message += ". [Internal errors] = " + c.getMessage() + ", Constraint Name = " + c.getConstraintName() + ", SQL = "
							+ c.getMessage();
				}
			} else {
				message += ". [Generic Internal errors] = " + e.getMessage();
			}
		}
		throw new GenericException(message);
	}
}
