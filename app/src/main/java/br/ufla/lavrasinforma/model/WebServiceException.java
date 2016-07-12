package br.ufla.lavrasinforma.model;

/**
 * Created by paulo on 11/07/16.
 */
public class WebServiceException extends Exception {

    public WebServiceException(String detailMessage) {
        super(detailMessage);
    }

    public WebServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
