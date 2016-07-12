package br.ufla.lavrasinforma.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by paulo on 11/07/16.
 */
public class WebServiceConnector {

    private static final String URL_WEBSERVICE = "http://pvmarc.ddns.org/lavras-informa/";

    public static RequestQueue requestQueue;

    private static WebServiceConnector connector;
    public static WebServiceConnector getInstance() {
        if (connector == null) {
            connector = new WebServiceConnector();
        }

        return connector;
    }

    private Gson gson;

    public WebServiceConnector() {
        this.gson = new Gson();
    }

    private void asyncRequest(Context context, int method, String path, JsonElement request, final Callback<JsonElement> callback) {
        final Object tag = new Object();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestQueue.cancelAll(tag);
                callback.onCancel();
            }
        });
        builder.setMessage("Conectando com o servidor...");

        final AlertDialog dialog = builder.create();

        Response.Listener<JsonElement> listener = new Response.Listener<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                callback.onSuccess(response);
                dialog.dismiss();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(new WebServiceException("Falha ao realizar requisição", error));
                dialog.dismiss();
            }
        };


        GsonRequest r = new GsonRequest(gson, method, URL_WEBSERVICE + path, request, listener, errorListener);
        r.setTag(tag);
        requestQueue.add(r);

        dialog.show();
    }

    private void tratarErros(JsonElement element) throws WebServiceException {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("error")) {
                throw new WebServiceException(object.getAsJsonPrimitive("error").getAsString());
            }
        }
    }

    public void autenticar(Context context, String email, String senha, final Callback<Usuario> callback) {
        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("senha", senha);

        asyncRequest(context, Request.Method.POST, "autenticar.php", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    Usuario usuario = gson.fromJson(jsonElement, Usuario.class);
                    callback.onSuccess(usuario);
                } catch (Throwable e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onError(Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void autenticarFacebook(Context context, String token, final Callback<Usuario> callback) {
        JsonObject request = new JsonObject();
        request.addProperty("token", token);

        asyncRequest(context, Request.Method.POST, "autenticar-facebook.php", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    Usuario usuario = gson.fromJson(jsonElement, Usuario.class);
                    callback.onSuccess(usuario);
                } catch (Throwable e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onError(Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void autorizar(Context context, Usuario usuario, final Callback<Boolean> callback) {
        JsonElement request = gson.toJsonTree(usuario);

        asyncRequest(context, Request.Method.POST, "autorizar.php", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    Boolean status = jsonElement.getAsJsonObject().getAsJsonPrimitive("status").getAsBoolean();
                    callback.onSuccess(status);
                } catch (Throwable e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onError(Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void cadastrar(Context context, String email, String senha, String nome, final Callback<Boolean> callback) {
        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("senha", senha);
        request.addProperty("nome", nome);

        asyncRequest(context, Request.Method.POST, "cadastro.php", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    Boolean status = jsonElement.getAsJsonObject().getAsJsonPrimitive("status").getAsBoolean();
                    callback.onSuccess(status);
                } catch (Throwable e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }

            @Override
            public void onError(Throwable error) {
                callback.onError(error);
            }
        });
    }
}

class GsonRequest extends Request<JsonElement> {

    private Gson gson;
    private JsonElement request;
    private Response.Listener<JsonElement> listener;

    public GsonRequest(Gson gson, int method, String url, Response.Listener<JsonElement> listener, Response.ErrorListener error) {
        this(gson, method, url, null, listener, error);
    }

    public GsonRequest(Gson gson, int method, String url, JsonElement request, Response.Listener<JsonElement> listener, Response.ErrorListener error) {
        super(method, url, error);
        this.gson = gson;
        this.request = request;
        this.listener = listener;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (request == null) {
            return super.getBody();
        } else {
            String json = gson.toJson(request);
            Charset cs = Charset.forName(HttpHeaderParser.parseCharset(getHeaders()));
            return cs.encode(json).array();
        }
    }

    @Override
    public String getBodyContentType() {
        if (request == null) {
            return super.getBodyContentType();
        } else {
            return "application/json";
        }
    }

    @Override
    protected Response<JsonElement> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JsonParser parser = new JsonParser();
            return Response.success(parser.parse(json), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError("Falha ao ler codificação de caracteres", e));
        }
    }

    @Override
    protected void deliverResponse(JsonElement response) {
        listener.onResponse(response);
    }
}