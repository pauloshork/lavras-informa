package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCEL = 2;
    public static final int RESULT_ERROR = 3;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }

    public void login(View view) {
        // TODO: Pedir autorização ao webservice
    }

    public void loginFacebook(View view) {
        LoginManager manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setResult(RESULT_SUCCESS);
                // TODO: Enviar dados para o webservice
                finish();
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCEL);
            }

            @Override
            public void onError(FacebookException error) {
                setResult(RESULT_ERROR);
                error.printStackTrace();
                // TODO: Mostrar mensagem de erro para o usuário
            }
        });
        manager.logInWithReadPermissions(this, null);
    }
}
