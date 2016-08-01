package br.ufla.lavrasinforma.model.web;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Classe que realiza requisições com parâmetros em JSON e recebe respostas em JSON.
 * Created by paulo on 18/07/16.
 */
class GsonRequest extends Request<JsonElement> {

    private Map<String, String> params;
    private Response.Listener<JsonElement> listener;

    public GsonRequest(int method, String url, Map<String, String> params, Response.Listener<JsonElement> listener, Response.ErrorListener error) {
        super(method, url, error);
        this.params = params;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    protected Response<JsonElement> parseNetworkResponse(NetworkResponse response) {
        try {
            JsonElement element = parseJsonNetworkResponse(response);
            return Response.success(element, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError("Falha ao ler codificação de caracteres", e));
        } catch (JsonSyntaxException e) {
            return Response.error(new VolleyError("Falha ao decodificar JSON", e));
        }
    }

    private WebServiceException tratarErros(JsonElement element, VolleyError error) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("error")) {
                String message = object.getAsJsonPrimitive("error").toString();
                if (object.has("error_description")) {
                    message = message + ": " + object.getAsJsonPrimitive("error_description").getAsString();
                }
                return new WebServiceException(message, error);
            }
        }

        return null;
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            try {
                JsonElement json = parseJsonNetworkResponse(volleyError.networkResponse);
                VolleyError jsonError = tratarErros(json, volleyError);
                if (jsonError != null) {
                    volleyError = jsonError;
                }
            } catch (UnsupportedEncodingException | JsonSyntaxException e) {
                return super.parseNetworkError(volleyError);
            }
            return volleyError;
        } else {
            return super.parseNetworkError(volleyError);
        }
    }

    @Override
    protected void deliverResponse(JsonElement response) {
        listener.onResponse(response);
    }

    public static JsonElement parseJsonNetworkResponse(NetworkResponse networkResponse) throws UnsupportedEncodingException, JsonSyntaxException {
        String json = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        Log.d("json", json);
        JsonParser parser = new JsonParser();
        return parser.parse(json);
    }
}
