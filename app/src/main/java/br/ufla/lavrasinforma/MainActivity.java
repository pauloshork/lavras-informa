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
import br.ufla.lavrasinforma.model.WebServiceException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_REGISTRO = 2;

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
            u.setToken(prefs.getString(SETTINGS_TOKEN, null));
            logar(u);
        }
    }

    private void logar(final Usuario usuario) {
        Log.d("login", "Logando no sistema");

        WebServiceConnector.getInstance().autorizar(this, usuario, new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    Intent menu = new Intent(MainActivity.this, MenuActivity.class);
                    menu.putExtra(MenuActivity.EXTRA_USUARIO, usuario);
                    startActivity(menu);
                    finish();
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    dialogBuilder.setMessage("Você não foi autorizado a fazer login no sistema.");
                    dialogBuilder.setPositiveButton("OK", null);
                    dialogBuilder.show();
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(Throwable error) {
                deslogar();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setMessage(error.getLocalizedMessage());
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            }
        });
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
        //Intent entrar = new Intent(this, RegistroActivity.class);
        //startActivityForResult(entrar, REQUEST_LOGIN);
        // TODO: requisitar resultado da tela de registro
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                switch (resultCode) {
                    case LoginActivity.RESULT_SUCCESS:
                        Usuario u = data.getParcelableExtra(LoginActivity.EXTRA_USUARIO);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString(SETTINGS_TOKEN, u.getToken());
                        edit.apply();
                        logar(u);
                        break;
                    case LoginActivity.RESULT_CANCEL:
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case REQUEST_REGISTRO:
                switch (resultCode) {
                    // TODO: processar resposta da tela de registro
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
