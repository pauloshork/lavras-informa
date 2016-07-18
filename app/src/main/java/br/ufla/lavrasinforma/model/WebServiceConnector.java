package br.ufla.lavrasinforma.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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

import br.ufla.lavrasinforma.R;

/**
 * Classe que auxilia na comunicação com o webservice do aplicativo.
 * Created by paulo on 11/07/16.
 */
public class WebServiceConnector {

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

        final ProgressDialog dialog = ProgressDialog.show(context, null, "Conectando ao servidor...", true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                requestQueue.cancelAll(tag);
                callback.onCancel();
            }
        });

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

        String webservice = context.getResources().getString(R.string.lavras_informa_webservice);
        GsonRequest r = new GsonRequest(gson, method, webservice + path, request, listener, errorListener);
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

    public static void mostrarDialogoErro(Context context, Throwable error) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(error.getLocalizedMessage());
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    private static JsonObject criarUserCredentialsRequest(Context context, String username, String password) {
        JsonObject request = new JsonObject();
        request.addProperty("client_id", context.getResources().getString(R.string.lavras_informa_app_id));
        request.addProperty("client_secret", context.getResources().getString(R.string.lavras_informa_app_secret));
        request.addProperty("grant_type", "password");
        request.addProperty("username", username);
        request.addProperty("password", password);
        return request;
    }

    /**
     * Requisição para /login do webservice.
     * @param context Contexto da aplicação
     * @param email Endereço de email do usuário
     * @param senha Senha do usuário
     * @param callback Função ativada ao fim da requisição
     */
    public void autenticar(Context context, String email, String senha, final Callback<Usuario> callback) {
        JsonObject request = criarUserCredentialsRequest(context, email, senha);

        asyncRequest(context, Request.Method.POST, "/login", request, new Callback<JsonElement>() {
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

    /**
     * Requisição para /loginFacebook do webservice
     * @param context Contexto da aplicação
     * @param userId UserId fornecido pela API do facebook
     * @param token AccessToken fornecido pela API do facebook
     * @param callback Função ativada ao fim da requisição
     */
    public void autenticarFacebook(Context context, String userId, String token, final Callback<Usuario> callback) {
        JsonObject request = criarUserCredentialsRequest(context, userId, token);

        asyncRequest(context, Request.Method.POST, "/loginFacebook", request, new Callback<JsonElement>() {
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

    public void autorizar(Context context, Usuario usuario, final Callback<Void> callback) {
        JsonElement request = gson.toJsonTree(usuario);

        asyncRequest(context, Request.Method.POST, "/validarToken", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    callback.onSuccess(null);
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

    /**
     * Requisição para /cadastro e /login do webservice.
     * @param context Contexto da aplicação
     * @param email Endereço de email do usuário
     * @param senha Senha do usuário
     * @param nome Nome do usuário
     * @param callback Função ativada ao fim da requisição
     */
    public void cadastrar(final Context context, final String email, final String senha, String nome, final Callback<Usuario> callback) {
        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("senha", senha);
        request.addProperty("nome", nome);

        asyncRequest(context, Request.Method.POST, "/cadastro", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    WebServiceConnector.this.autenticar(context, email, senha, callback);
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
            Log.d("json", json);
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