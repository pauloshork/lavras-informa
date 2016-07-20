package br.ufla.lavrasinforma.model.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;

import br.ufla.lavrasinforma.R;
import br.ufla.lavrasinforma.model.AccessToken;
import br.ufla.lavrasinforma.model.Busca;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Usuario;

/**
 * Classe que auxilia na comunicação com o webservice do aplicativo.
 * Created by paulo on 11/07/16.
 */
public class WebServiceConnector {

    private static RequestQueue requestQueue;
    private static WebServiceConnector instance = new WebServiceConnector();
    public static WebServiceConnector getInstance() {
        return instance;
    }

    private Gson gson;

    private WebServiceConnector() {
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
                callback.onError(new WebServiceException("Falha ao realizar requisição: " + error.getLocalizedMessage(), error));
                dialog.dismiss();
            }
        };

        String webservice = context.getResources().getString(R.string.lavras_informa_webservice);
        GsonRequest r = new GsonRequest(gson, method, webservice + path, request, listener, errorListener);
        r.setTag(tag);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        requestQueue.add(r);

        dialog.show();
    }

    private void tratarErros(JsonElement element) throws WebServiceException {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("error")) {
                String message = object.getAsJsonObject("error").getAsJsonPrimitive("message").getAsString();
                Log.d("json", object.toString());
                throw new WebServiceException(message);
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
    public void autenticar(Context context, String email, String senha, final Callback<AccessToken> callback) {
        JsonObject request = criarUserCredentialsRequest(context, email, senha);

        asyncRequest(context, Request.Method.POST, "/login", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    AccessToken accessToken = gson.fromJson(jsonElement, AccessToken.class);
                    callback.onSuccess(accessToken);
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
    public void autenticarFacebook(Context context, String userId, String token, final Callback<AccessToken> callback) {
        JsonObject request = criarUserCredentialsRequest(context, userId, token);

        asyncRequest(context, Request.Method.POST, "/loginFacebook", request, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                try {
                    tratarErros(jsonElement);
                    AccessToken accessToken = gson.fromJson(jsonElement, AccessToken.class);
                    callback.onSuccess(accessToken);
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
    public void cadastrar(final Context context, final String email, final String senha, String nome, final Callback<AccessToken> callback) {
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

    /**
     * Requisição para /usuario do webservice.
     * @param context Contexto da aplicação
     * @param accessToken Token de acesso da seção atual
     * @param callback Função ativada ao fim da requisição
     */
    public void getUsuario(final Context context, final AccessToken accessToken, final Callback<Usuario> callback) {
        JsonElement request = gson.toJsonTree(accessToken);

        asyncRequest(context, Request.Method.POST, "/usuario", request, new Callback<JsonElement>() {
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

    public void buscarRelatos(final Context context, final AccessToken accessToken, final Busca busca, final Callback<Collection<Relato>> callback) {
        throw new RuntimeException("Não implementado");
    }

    public void enviarRelato(final Context context, final AccessToken accessToken, final Relato relato, final Callback<Void> callback) {
        throw new RuntimeException("Não implementado");
    }
}
