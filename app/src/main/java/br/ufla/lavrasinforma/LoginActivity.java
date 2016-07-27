package br.ufla.lavrasinforma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.AccessToken;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class LoginActivity extends AppCompatActivity {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCEL = 2;
    public static final String EXTRA_TOKEN = "token";

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
        EditText email = (EditText) findViewById(R.id.txtEmail);
        EditText senha = (EditText) findViewById(R.id.txtSenha);

        String emailValor = email.getText().toString();
        String senhaValor = senha.getText().toString();

        WebServiceConnector.getInstance().autenticar(this, emailValor, senhaValor, new Callback<AccessToken>() {
            @Override
            public void onSuccess(AccessToken accessToken) {
                Log.d("login", "onSuccess()");
                UtilSession.setAccessToken(LoginActivity.this, accessToken);
                Intent resultado = new Intent();
                resultado.putExtra(EXTRA_TOKEN, accessToken);
                setResult(RESULT_SUCCESS, resultado);
                finish();
            }

            @Override
            public void onCancel() {
                Log.d("login", "onCancel()");
            }

            @Override
            public void onError(Throwable error) {
                Log.d("login", "onError()", error);
                WebServiceConnector.mostrarDialogoErro(LoginActivity.this, error);
            }
        }, true);
    }

    public void loginFacebook(View view) {
        LoginManager manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d("facebook", "onSuccess()");
                String userId = loginResult.getAccessToken().getUserId();
                String accessToken = loginResult.getAccessToken().getToken();

                WebServiceConnector.getInstance().autenticarFacebook(LoginActivity.this, userId, accessToken, new Callback<AccessToken>() {
                    @Override
                    public void onSuccess(AccessToken accessToken) {
                        Log.d("facebook/req", "onSuccess()");
                        UtilSession.setAccessToken(LoginActivity.this, accessToken);
                        Intent resultado = new Intent();
                        resultado.putExtra(EXTRA_TOKEN, accessToken);
                        setResult(RESULT_SUCCESS, resultado);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("facebook/req", "onCancel()");
                        Toast.makeText(LoginActivity.this, "Login cancelado.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.d("facebook/req", "onError()", error);
                        WebServiceConnector.mostrarDialogoErro(LoginActivity.this, error);
                    }
                }, true);
            }

            @Override
            public void onCancel() {
                Log.d("facebook", "onCancel()");
                Toast.makeText(LoginActivity.this, "Login cancelado.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebook", "onError()", error);
                WebServiceConnector.mostrarDialogoErro(LoginActivity.this, error);
            }
        });
        manager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }
}
