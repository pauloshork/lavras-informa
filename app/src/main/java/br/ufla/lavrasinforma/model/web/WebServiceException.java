package br.ufla.lavrasinforma.model.web;

import com.android.volley.VolleyError;

/**
 * Exceção gerada pelo conector do webservice.
 * Created by paulo on 11/07/16.
 */
public class WebServiceException extends VolleyError {

    public WebServiceException(String detailMessage) {
        super(detailMessage);
    }

    public WebServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
