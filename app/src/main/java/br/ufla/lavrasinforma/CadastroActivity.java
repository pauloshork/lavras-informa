package br.ufla.lavrasinforma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import br.ufla.lavrasinforma.model.Callback;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.WebServiceConnector;

public class CadastroActivity extends AppCompatActivity {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCEL = 2;
    public static final String EXTRA_USUARIO = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
    }

    public void finalizar(View view) {
        EditText txtNome = (EditText) findViewById(R.id.txtNome);
        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
        EditText txtSenha = (EditText) findViewById(R.id.txtSenha);

        String email = txtEmail.getText().toString().trim();
        String senha = txtSenha.getText().toString();
        String nome = txtNome.getText().toString().trim();

        WebServiceConnector.getInstance().cadastrar(this, email, senha, nome, new Callback<Usuario>() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                builder.setMessage(error.getLocalizedMessage());
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }
}
