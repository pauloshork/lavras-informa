package br.ufla.lavrasinforma.model.web;

/**
 * Created by paulo on 12/07/16.
 */
public abstract class Callback<RESULT> {

    public abstract void onSuccess(RESULT result);

    public abstract void onCancel();

    public abstract void onError(Throwable error);
}
