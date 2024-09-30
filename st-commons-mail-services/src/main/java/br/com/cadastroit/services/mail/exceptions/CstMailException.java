package br.com.cadastroit.services.mail.exceptions;

import org.springframework.mail.MailException;

public class CstMailException extends MailException {

    public CstMailException(String msg) {
        super(msg);
    }

    public CstMailException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
