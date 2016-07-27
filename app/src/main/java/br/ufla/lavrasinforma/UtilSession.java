package br.ufla.lavrasinforma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import br.ufla.lavrasinforma.model.AccessToken;

/**
 * Classe utilitária para o aplicativo.
 * Created by paulo on 27/07/16.
 */
public class UtilSession {

    public static final String PREFERENCES_NAME = "br.ufla.lavrasinforma.settings";
    public static final String PREFERENCES_KEY_ACCESS_TOKEN = "access-token";

    protected static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static AccessToken getAccessToken(Context context) {
        SharedPreferences p = getSharedPreferences(context);
        String json = p.getString(PREFERENCES_KEY_ACCESS_TOKEN, null);
        if (json == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(json, AccessToken.class);
        }
    }

    public static void setAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences p = getSharedPreferences(context);
        SharedPreferences.Editor editor = p.edit();
        Gson gson = new Gson();
        String json = null;
        if (accessToken != null) {
            json = gson.toJson(accessToken);
        }
        editor.putString(PREFERENCES_KEY_ACCESS_TOKEN, json);
        editor.apply();
    }

    public static void logout(Context context) {
        LoginManager.getInstance().logOut();
        setAccessToken(context, null);
    }

    public static boolean relogar(Activity activity, Throwable error, int requestCode) {
        if (error.getCause() instanceof AuthFailureError || error instanceof AuthFailureError) {
            Toast.makeText(activity, "Seção expirada.", Toast.LENGTH_LONG).show();
            Intent relogar = new Intent(activity, LoginActivity.class);
            activity.startActivityForResult(relogar, requestCode);
            return true;
        } else {
            return false;
        }
    }
}
