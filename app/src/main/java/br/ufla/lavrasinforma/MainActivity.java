package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_REGISTRO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        LoginManager manager = LoginManager.getInstance();
        manager.logOut();
    }

    public void entrar(View view) {
        Intent entrar = new Intent(this, LoginActivity.class);
        startActivityForResult(entrar, REQUEST_LOGIN);
    }

    public void cadastro(View view) {
//        Intent entrar = new Intent(this, RegistroActivity.class);
//        startActivityForResult(entrar, REQUEST_LOGIN);
        // TODO requisitar resultado da tela de registro
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                switch (resultCode) {
                    case LoginActivity.RESULT_SUCCESS:
                        Intent menu = new Intent(this, MenuActivity.class);
                        startActivity(menu);
                        finish();
                        break;
                    case LoginActivity.RESULT_CANCEL:
                        break;
                    case LoginActivity.RESULT_ERROR:
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case REQUEST_REGISTRO:
                switch (resultCode) {
                    // TODO processar resposta da tela de registro
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
