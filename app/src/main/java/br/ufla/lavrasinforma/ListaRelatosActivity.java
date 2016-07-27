package br.ufla.lavrasinforma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import br.ufla.lavrasinforma.model.AccessToken;
import br.ufla.lavrasinforma.model.BuscaRelato;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class ListaRelatosActivity extends AppCompatActivity {

    private static final int REQUEST_BUSCA = 1;
    private static final int REQUEST_VER = 2;
    public static final String ACTION_MEUS_RELATOS = "meus-relatos";
    public static final String ACTIOIN_TODOS_RELATOS = "todos-relatos";
    private static final String EXTRA_RELATO = "relato";

    private ProblemaListAdapter relatosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_relatos);

        relatosAdapter = new ProblemaListAdapter(getApplicationContext());

        ListView lstRelatos = (ListView) findViewById(R.id.lstRelatos);
        if (lstRelatos != null) {
            lstRelatos.setAdapter(relatosAdapter);
            lstRelatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Relato relato = (Relato) adapterView.getSelectedItem();
                    verRelato(relato);
                }
            });
        }

        // Processar ações
        BuscaRelato buscaRelato;
        switch (getIntent().getAction()) {
            case ACTION_MEUS_RELATOS:
                buscaRelato = new BuscaRelato();
                buscaRelato.setMeus(true);
                buscar(buscaRelato);
                break;
            case ACTIOIN_TODOS_RELATOS:
                buscaRelato = new BuscaRelato();
                buscar(buscaRelato);
                break;
        }
    }

    public void buscar(View view) {
        buscar((BuscaRelato) null);
    }

    protected void buscar(BuscaRelato buscaRelato) {
        Intent buscar = new Intent(this, BuscaActivity.class);
        if (buscaRelato != null) {
            buscar.putExtra(BuscaActivity.EXTRA_PARAMETROS, buscaRelato);
        }
        startActivityForResult(buscar, REQUEST_BUSCA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BUSCA:
                switch (resultCode) {
                    case BuscaActivity.RESULT_SUCCESS:
                        ArrayList<Relato> relatos = data.getParcelableArrayListExtra(BuscaActivity.EXTRA_RELATOS);
                        relatosAdapter.clear();
                        relatosAdapter.addAll(relatos);
                        break;
                    case BuscaActivity.RESULT_CANCEL:
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case REQUEST_VER:
                Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);
                verRelato(relato);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void verRelato(final Relato relato) {
        final AccessToken accessToken = UtilSession.getAccessToken(ListaRelatosActivity.this);
        WebServiceConnector.getInstance().getUsuario(ListaRelatosActivity.this, accessToken, new Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                Intent verRelato = new Intent(ListaRelatosActivity.this, RelatoActivity.class);
                verRelato.putExtra(RelatoActivity.EXTRA_RELATO, relato);
                if (relato.getIdUsuario() == usuario.getId() || usuario.isAdmin()) {
                    verRelato.setAction(RelatoActivity.ACTION_EDITAVEL);
                } else {
                    verRelato.setAction(RelatoActivity.ACTION_LEITURA);
                }
                startActivity(verRelato);
            }

            @Override
            public void onError(Throwable error) {
                getIntent().putExtra(EXTRA_RELATO, relato);
                if (!UtilSession.relogar(ListaRelatosActivity.this, error, REQUEST_VER)) {
                    WebServiceConnector.mostrarDialogoErro(ListaRelatosActivity.this, error);
                }
            }
        }, true);
    }
}
