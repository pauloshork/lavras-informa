package br.ufla.lavrasinforma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
    public static final String EXTRA_BUSCA = "busca";
    private static final String EXTRA_RELATO = "relato";

    private RelatoListAdapter relatosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_relatos);

        relatosAdapter = new RelatoListAdapter(getApplicationContext());

        ListView lstRelatos = (ListView) findViewById(R.id.lstRelatos);
        if (lstRelatos != null) {
            lstRelatos.setAdapter(relatosAdapter);
            lstRelatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Relato relato = (Relato) adapterView.getItemAtPosition(i);
                    verRelato(relato);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BuscaRelato buscaRelato = getIntent().getParcelableExtra(EXTRA_BUSCA);
        buscar(buscaRelato);
    }

    public void buscar(View view) {
        BuscaRelato buscaRelato = getIntent().getParcelableExtra(EXTRA_BUSCA);
        Intent buscar = new Intent(this, BuscaActivity.class);
        buscar.putExtra(BuscaActivity.EXTRA_BUSCA, buscaRelato);
        startActivityForResult(buscar, REQUEST_BUSCA);
    }

    protected void buscar(BuscaRelato buscaRelato) {
        getIntent().putExtra(EXTRA_BUSCA, buscaRelato);

        WebServiceConnector.getInstance().buscarRelatos(this, UtilSession.getAccessToken(this), buscaRelato, new Callback<ArrayList<Relato>>() {
            @Override
            public void onSuccess(ArrayList<Relato> relatos) {
                relatosAdapter.clear();
                relatosAdapter.addAll(relatos);
            }

            @Override
            public void onCancel() {
                Toast.makeText(ListaRelatosActivity.this, "Busca cancelada.", Toast.LENGTH_LONG);
            }

            @Override
            public void onError(Throwable error) {
                if (!UtilSession.relogar(ListaRelatosActivity.this, error, REQUEST_BUSCA)) {
                    WebServiceConnector.mostrarDialogoErro(ListaRelatosActivity.this, error);
                }
            }
        }, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BUSCA:
                switch (resultCode) {
                    case BuscaActivity.RESULT_SUCCESS:
                        BuscaRelato buscaRelato = data.getParcelableExtra(BuscaActivity.EXTRA_BUSCA);
                        getIntent().putExtra(EXTRA_BUSCA, buscaRelato);
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
        AccessToken accessToken = UtilSession.getAccessToken(ListaRelatosActivity.this);
        WebServiceConnector.getInstance().getUsuario(ListaRelatosActivity.this, accessToken, new Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                Intent verRelato = new Intent(ListaRelatosActivity.this, RelatoActivity.class);
                verRelato.putExtra(RelatoActivity.EXTRA_RELATO, relato);
                if (relato.getIdUsuario().equals(usuario.getId()) || usuario.isAdmin()) {
                    verRelato.setAction(RelatoActivity.ACTION_LEITURA_EDITAVEL);
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
