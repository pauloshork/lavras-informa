package br.ufla.lavrasinforma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import br.ufla.lavrasinforma.model.AccessToken;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_CADASTRO = 2;
    public static final String PREFERENCES_NAME = "br.ufla.lavrasinforma.settings";
    public static final String PREFERENCES_KEY_ACCESS_TOKEN = "access-token";
    public static final String ACTION_LOGOUT = "logout";

    protected static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        switch (getIntent().getAction()) {
            case ACTION_LOGOUT:
                deslogar();
                break;
        }

        AccessToken accessToken = getAccessToken(getApplicationContext());
        if (accessToken != null) {
            Log.d("login", "Token encontrado nas preferências");
            logar(accessToken);
        }
    }

    private void logar(AccessToken accessToken) {
        Log.d("login", "Logando no sistema");

        setAccessToken(getApplicationContext(), accessToken);

        Intent menu = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(menu);
        finish();
    }

    private void deslogar() {
        Log.d("login", "Fazendo logout do sistema");

        LoginManager.getInstance().logOut();
        setAccessToken(getApplicationContext(), null);
    }

    public void entrar(View view) {
        Intent entrar = new Intent(this, LoginActivity.class);
        startActivityForResult(entrar, REQUEST_LOGIN);
    }

    public void cadastro(View view) {
        Intent entrar = new Intent(this, CadastroActivity.class);
        startActivityForResult(entrar, REQUEST_CADASTRO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                switch (resultCode) {
                    case LoginActivity.RESULT_SUCCESS:
                        AccessToken u = data.getParcelableExtra(LoginActivity.EXTRA_USUARIO);
                        logar(u);
                        break;
                    case LoginActivity.RESULT_CANCEL:
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case REQUEST_CADASTRO:
                switch (resultCode) {
                    case CadastroActivity.RESULT_SUCCESS:
                        AccessToken u = data.getParcelableExtra(LoginActivity.EXTRA_USUARIO);
                        logar(u);
                    case CadastroActivity.RESULT_CANCEL:
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
