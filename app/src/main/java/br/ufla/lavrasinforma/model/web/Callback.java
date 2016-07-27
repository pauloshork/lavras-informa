package br.ufla.lavrasinforma.model.web;

/**
 * Created by paulo on 12/07/16.
 */
public abstract class Callback<RESULT> {

    protected final Callback<?> passthrough;

    public Callback() {
        this(null);
    }

    public Callback(Callback<?> passthrough) {
        this.passthrough = passthrough;
    }

    public abstract void onSuccess(RESULT result);

    public void onCancel() {
        if (passthrough != null) {
            passthrough.onCancel();
        }
    }

    public void onError(Throwable error) {
        if (passthrough != null) {
            passthrough.onError(error);
        }
    }
}
