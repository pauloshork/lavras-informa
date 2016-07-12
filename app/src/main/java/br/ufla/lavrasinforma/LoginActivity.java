package br.ufla.lavrasinforma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import br.ufla.lavrasinforma.model.Callback;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.WebServiceConnector;

public class LoginActivity extends AppCompatActivity {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCEL = 2;
    public static final String EXTRA_USUARIO = "usuario";

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

        WebServiceConnector.getInstance().autenticar(this, emailValor, senhaValor, new Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                Intent resultado = new Intent();
                resultado.putExtra(EXTRA_USUARIO, usuario);
                setResult(RESULT_SUCCESS, resultado);
                finish();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(Throwable error) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                dialogBuilder.setMessage(error.getLocalizedMessage());
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            }
        });
    }

    public void loginFacebook(View view) {
        LoginManager manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Usuario usuario = new Usuario();
                usuario.setToken(loginResult.getAccessToken().getToken());

                Intent resultado = new Intent();
                resultado.putExtra(EXTRA_USUARIO, usuario);
                setResult(RESULT_SUCCESS, resultado);
                finish();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                dialogBuilder.setMessage(error.getLocalizedMessage());
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            }
        });
        manager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }
}
