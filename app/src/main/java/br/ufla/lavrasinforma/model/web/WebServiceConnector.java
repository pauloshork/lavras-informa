package br.ufla.lavrasinforma.model.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.ufla.lavrasinforma.R;
import br.ufla.lavrasinforma.model.AccessToken;
import br.ufla.lavrasinforma.model.BuscaComentario;
import br.ufla.lavrasinforma.model.BuscaRelato;
import br.ufla.lavrasinforma.model.Comentario;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Usuario;

/**
 * Classe que auxilia na comunicação com o webservice do aplicativo.
 * Created by paulo on 11/07/16.
 */
public class WebServiceConnector {

    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private static ImageLoader.ImageCache imageCache;
    private static WebServiceConnector instance = new WebServiceConnector();
    public static WebServiceConnector getInstance() {
        return instance;
    }

    public static RequestQueue getQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }

    public static ImageLoader.ImageCache getImageCache() {
        if (imageCache == null) {
            final int maxSize = (int) Runtime.getRuntime().maxMemory() / 1024 / 8;
            imageCache = new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> lru = new LruCache<String, Bitmap>(maxSize) {
                    @Override
                    protected int sizeOf(String key, Bitmap value) {
                        return value.getByteCount() / 1024;
                    }
                };
                @Override
                public Bitmap getBitmap(String url) {
                    return lru.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    lru.put(url, bitmap);
                }
            };
        }
        return imageCache;
    }

    public static ImageLoader getImageLoader(Context context) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getQueue(context), getImageCache());
        }

        return imageLoader;
    }

    private Gson gson;

    private WebServiceConnector() {
        this.gson = new Gson();
    }

    private void asyncRequest(final Context context, int method, String path, Map<String, String> request, final Callback<JsonElement> callback, boolean modal) {
        final Object tag = new Object();

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Conectando ao servidor...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                getQueue(context).cancelAll(tag);
                callback.onCancel();
            }
        });

        Response.Listener<JsonElement> listener = new Response.Listener<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                dialog.dismiss();

                try {
                    callback.onSuccess(response);
                } catch (Throwable e) {
                    callback.onError(e);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

                WebServiceException wsError;
                if (error instanceof WebServiceException) {
                    wsError = (WebServiceException) error;
                } else if (error instanceof AuthFailureError) {
                    wsError = new WebServiceException("Falha de autenticação:" + error.getLocalizedMessage(), error);
                } else if (error instanceof ServerError) {
                    wsError = new WebServiceException("Falha interna do servidor", error);
                } else if (error instanceof NetworkError) {
                    wsError = new WebServiceException("Falha de rede: " + error.getLocalizedMessage(), error);
                } else if (error instanceof ParseError) {
                    wsError = new WebServiceException("Resposta não pôde ser lida: " + error.getLocalizedMessage(), error);
                } else if (error instanceof NoConnectionError) {
                    wsError = new WebServiceException("Falha ao se conectar com o servidor: " + error.getLocalizedMessage(), error);
                } else if (error instanceof TimeoutError) {
                    wsError = new WebServiceException("O servidor demorou muito para responder", error);
                } else {
                    wsError = new WebServiceException("Falha ao realizar requisição: " + error.getClass() + ": " + error.getLocalizedMessage(), error);
                }

                callback.onError(wsError);
            }
        };

        String webservice = context.getResources().getString(R.string.lavras_informa_webservice);
        GsonRequest r = new GsonRequest(method, webservice + path, request, listener, errorListener);
        r.setTag(tag);

        getQueue(context).add(r);

        if (modal) {
            dialog.show();
        }
    }

    public static void mostrarDialogoErro(Context context, Throwable error) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(error.getLocalizedMessage());
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    private static Map<String, String> criarUserCredentialsRequest(Context context, String username, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", context.getResources().getString(R.string.lavras_informa_app_id));
        request.put("client_secret", context.getResources().getString(R.string.lavras_informa_app_secret));
        request.put("grant_type", "password");
        request.put("username", username);
        request.put("password", password);
        return request;
    }

    private static Map<String, String> criarAccessTokenRequest(AccessToken accessToken, Map<String, String> request) {
        if (request == null) {
            request = new HashMap<>();
        }
        Log.d("login", accessToken.getAccessToken());
        request.put("access_token", accessToken.getAccessToken());
        return request;
    }

    /**
     * Requisição para /login do webservice.
     * @param context Contexto da aplicação
     * @param email Endereço de email do usuário
     * @param senha Senha do usuário
     * @param callback Função ativada ao fim da requisição
     */
    public void autenticar(Context context, String email, String senha, final Callback<AccessToken> callback, boolean modal) {
        Map<String, String> request = criarUserCredentialsRequest(context, email, senha);

        asyncRequest(context, Request.Method.POST, "/login", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                AccessToken accessToken = gson.fromJson(jsonElement, AccessToken.class);
                callback.onSuccess(accessToken);
            }
        }, modal);
    }

    /**
     * Requisição para /loginFacebook do webservice
     * @param context Contexto da aplicação
     * @param userId UserId fornecido pela API do facebook
     * @param token AccessToken fornecido pela API do facebook
     * @param callback Função ativada ao fim da requisição
     */
    public void autenticarFacebook(Context context, String userId, String token, final Callback<AccessToken> callback, boolean modal) {
        Map<String, String> request = criarUserCredentialsRequest(context, userId, token);

        asyncRequest(context, Request.Method.POST, "/loginFacebook", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                AccessToken accessToken = gson.fromJson(jsonElement, AccessToken.class);
                callback.onSuccess(accessToken);
            }
        }, modal);
    }

    /**
     * Requisição para /cadastro e /login do webservice.
     * @param context Contexto da aplicação
     * @param email Endereço de email do usuário
     * @param senha Senha do usuário
     * @param nome Nome do usuário
     * @param callback Função ativada ao fim da requisição
     */
    public void cadastrar(final Context context, final String email, final String senha, String nome, final Callback<AccessToken> callback, final boolean modal) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("senha", senha);
        request.put("nome", nome);

        asyncRequest(context, Request.Method.POST, "/cadastro", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                WebServiceConnector.this.autenticar(context, email, senha, callback, modal);
            }
        }, modal);
    }

    /**
     * Requisição para /usuario do webservice.
     * @param context Contexto da aplicação
     * @param accessToken Token de acesso da seção atual
     * @param callback Função ativada ao fim da requisição
     */
    public void getUsuario(Context context, AccessToken accessToken, final Callback<Usuario> callback, boolean modal) {
        Map<String, String> request = criarAccessTokenRequest(accessToken, null);

        asyncRequest(context, Request.Method.POST, "/usuario", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                Usuario usuario = gson.fromJson(jsonElement, Usuario.class);
                callback.onSuccess(usuario);
            }
        }, modal);
    }

    /**
     * Requisição para /relatos do webservice.
     * @param context Contexto da aplicação
     * @param accessToken Token de acesso da seção atual
     * @param buscaRelato Parâmetros de busca
     * @param callback Função ativada ao fim da requisição
     */
    public void buscarRelatos(Context context, AccessToken accessToken, BuscaRelato buscaRelato, final Callback<ArrayList<Relato>> callback, boolean modal) {
        Map<String, String> request = buscaRelato.toParams(null);
        request = criarAccessTokenRequest(accessToken, request);

        asyncRequest(context, Request.Method.POST, "/relatos", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                Type listType = new TypeToken<ArrayList<Relato>>() {}.getType();
                ArrayList<Relato> list = gson.fromJson(jsonElement, listType);
                callback.onSuccess(list);
            }
        }, modal);
    }

    /**
     * Requisição para /relatos/set do webservice.
     * @param context Contexto da aplicação
     * @param accessToken Token de acesso da seção atual
     * @param relato Relato a ser enviado
     * @param callback Função ativada ao fim da requisição
     */
    public void enviarRelato(Context context, AccessToken accessToken, Relato relato, final Callback<Void> callback, boolean modal) {
        Map<String, String> request = relato.toParams(null);
        request = criarAccessTokenRequest(accessToken, request);

        asyncRequest(context, Request.Method.POST, "/relatos/set", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.onSuccess(null);
            }
        }, modal);
    }

    public void buscarComentarios(Context context, AccessToken accessToken, final BuscaComentario buscaComentario, final Callback<ArrayList<Comentario>> callback, boolean modal) {
        Map<String, String> request = buscaComentario.toParams(null);
        request = criarAccessTokenRequest(accessToken, request);

        asyncRequest(context, Request.Method.POST, "/comentarios", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                Type listType = new TypeToken<ArrayList<Comentario>>() {}.getType();
                ArrayList<Comentario> list = gson.fromJson(jsonElement, listType);
                callback.onSuccess(list);
            }
        }, modal);
    }

    public String buscarImagem(Context context, AccessToken accessToken, Relato relato) {
        String webservice = context.getResources().getString(R.string.lavras_informa_webservice);
        String path = "/foto?access_token=" + accessToken.getAccessToken() + "&id=" + relato.getId();
        return webservice + path;
    }

    public void enviarComentario(Context context, AccessToken accessToken, Comentario comentario, final Callback<Void> callback, boolean modal) {
        Map<String, String> request = comentario.toParams(null);
        request = criarAccessTokenRequest(accessToken, request);

        asyncRequest(context, Request.Method.POST, "/comentarios/set", request, new Callback<JsonElement>(callback) {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.onSuccess(null);
            }
        }, modal);
    }
}
