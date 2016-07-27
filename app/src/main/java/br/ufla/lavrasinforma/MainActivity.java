package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import br.ufla.lavrasinforma.model.AccessToken;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_CADASTRO = 2;

    public static final String ACTION_LOGOUT = "logout";

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

        AccessToken accessToken = UtilSession.getAccessToken(getApplicationContext());
        if (accessToken != null) {
            Log.d("login", "Token encontrado nas preferências");
            logar();
        }
    }

    private void logar() {
        Log.d("login", "Logando no sistema");
        Intent menu = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(menu);
        finish();
    }

    private void deslogar() {
        Log.d("login", "Fazendo logout do sistema");
        UtilSession.logout(getApplicationContext());
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
                        logar();
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
                        logar();
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
