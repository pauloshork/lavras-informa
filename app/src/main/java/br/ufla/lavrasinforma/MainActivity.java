package br.ufla.lavrasinforma;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import br.ufla.lavrasinforma.model.Callback;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.WebServiceConnector;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_CADASTRO = 2;

    public static final String ACTION_LOGOUT = "logout";

    private static final String SETTINGS_TOKEN = "token";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        WebServiceConnector.requestQueue = Volley.newRequestQueue(getApplicationContext());
        prefs = getPreferences(MODE_PRIVATE);

        switch (getIntent().getAction()) {
            case ACTION_LOGOUT:
                deslogar();
                break;
        }

        if (prefs.contains(SETTINGS_TOKEN)) {
            Log.d("login", "Token encontrado nas preferências");
            Usuario u = new Usuario();
            u.setAccessToken(prefs.getString(SETTINGS_TOKEN, null));
            logar(u);
        }
    }

    private void logar(final Usuario usuario) {
        Log.d("login", "Logando no sistema");

        Intent menu = new Intent(MainActivity.this, MenuActivity.class);
        menu.putExtra(MenuActivity.EXTRA_USUARIO, usuario);
        startActivity(menu);
        finish();
    }

    private void deslogar() {
        Log.d("login", "Fazendo logout do sistema");
        LoginManager.getInstance().logOut();
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(SETTINGS_TOKEN);
        edit.apply();
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
                        Usuario u = data.getParcelableExtra(LoginActivity.EXTRA_USUARIO);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString(SETTINGS_TOKEN, u.getAccessToken());
                        edit.apply();
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
                        Usuario u = data.getParcelableExtra(LoginActivity.EXTRA_USUARIO);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString(SETTINGS_TOKEN, u.getAccessToken());
                        edit.apply();
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
