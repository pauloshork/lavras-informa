package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.ufla.lavrasinforma.model.BuscaRelato;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_DADOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WebServiceConnector.getInstance().getUsuario(this, UtilSession.getAccessToken(this), new Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
            }

            @Override
            public void onError(Throwable error) {
                if (!UtilSession.relogar(MenuActivity.this, error, REQUEST_DADOS)) {
                    WebServiceConnector.mostrarDialogoErro(MenuActivity.this, error);
                }
            }
        }, true);
    }

    public void relatarProblema(View view) {
        Intent relatar = new Intent(this, RelatoActivity.class);
        relatar.setAction(RelatoActivity.ACTION_EDITAVEL);
        startActivity(relatar);
    }

    public void meusRelatos(View view) {
        Intent lista = new Intent(this, ListaRelatosActivity.class);
        BuscaRelato buscaRelato = new BuscaRelato();
        buscaRelato.setMeus(true);
        lista.putExtra(ListaRelatosActivity.EXTRA_BUSCA, buscaRelato);
        startActivity(lista);
    }

    public void todosRelator(View view) {
        Intent lista = new Intent(this, ListaRelatosActivity.class);
        lista.putExtra(ListaRelatosActivity.EXTRA_BUSCA, new BuscaRelato());
        startActivity(lista);
    }

    public void mapaRelatos(View view) {
        Toast.makeText(this, "TODO Criar tela de mapa de relatos", Toast.LENGTH_LONG).show();
        // TODO Criar tela de mapa de relatos
    }

    public void logout(View view) {
        Intent logout = new Intent(this, MainActivity.class);
        logout.setAction(MainActivity.ACTION_LOGOUT);
        startActivity(logout);
        finish();
    }
}
