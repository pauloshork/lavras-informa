package br.ufla.lavrasinforma.model.web;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Classe que realiza requisições com parâmetros em JSON e recebe respostas em JSON.
 * Created by paulo on 18/07/16.
 */
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
