package br.ufla.lavrasinforma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import br.ufla.lavrasinforma.model.AccessToken;
import br.ufla.lavrasinforma.model.Busca;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class ListaRelatosActivity extends AppCompatActivity {

    private static final int REQUEST_BUSCA = 1;
    public static final String ACTION_MEUS_RELATOS = "meus-relatos";
    public static final String ACTIOIN_TODOS_RELATOS = "todos-relatos";

    private ProblemaListAdapter relatosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_relatos);

        relatosAdapter = new ProblemaListAdapter(getApplicationContext());

        ListView lstRelatos = (ListView) findViewById(R.id.lstRelatos);
        lstRelatos.setAdapter(relatosAdapter);

        lstRelatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Relato relato = (Relato) adapterView.getSelectedItem();
                final AccessToken accessToken = MainActivity.getAccessToken(getApplicationContext());
                WebServiceConnector.getInstance().getUsuario(ListaRelatosActivity.this, accessToken, new Callback<Usuario>() {
                    @Override
                    public void onSuccess(Usuario usuario) {
                        // TODO Criar tela de visualização/edição de relatos
                        Toast.makeText(ListaRelatosActivity.this, "TODO Criar tela vizualização/edição de relatos", Toast.LENGTH_LONG).show();
                        /*Intent relato = new Intent(ListaRelatosActivity.this, RelatoActivity.class);
                        if (relato.getIdUsuario() == usuario.getId() || usuario.isAdmin()) {
                            relato.setAction(RelatoActivity.ACTION_VISUALIZAR_EDITAVEL);
                        } else {
                            relato.setAction(RelatoActivity.ACTION_VISUALIZAR_LEITURA);
                        }
                        startActivity(relato);*/
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(Throwable error) {
                        WebServiceConnector.mostrarDialogoErro(ListaRelatosActivity.this, error);
                    }
                });
            }
        });

        // Processar ações
        final AccessToken accessToken = MainActivity.getAccessToken(getApplicationContext());
        switch (getIntent().getAction()) {
            case ACTION_MEUS_RELATOS:
                WebServiceConnector.getInstance().getUsuario(this, accessToken, new Callback<Usuario>() {
                    @Override
                    public void onSuccess(Usuario usuario) {
                        Busca busca = new Busca();
                        busca.setAutor(usuario.getNome());
                        buscar(busca);
                    }

                    @Override
                    public void onCancel() {
                        onBackPressed();
                    }

                    @Override
                    public void onError(Throwable error) {
                        WebServiceConnector.mostrarDialogoErro(ListaRelatosActivity.this, error);
                    }
                });
                break;
            case ACTIOIN_TODOS_RELATOS:
                WebServiceConnector.getInstance().getUsuario(this, accessToken, new Callback<Usuario>() {
                    @Override
                    public void onSuccess(Usuario usuario) {
                        Busca busca = new Busca();
                        buscar(busca);
                    }

                    @Override
                    public void onCancel() {
                        onBackPressed();
                    }

                    @Override
                    public void onError(Throwable error) {
                        WebServiceConnector.mostrarDialogoErro(ListaRelatosActivity.this, error);
                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void buscar(View view) {
        buscar((Busca) null);
    }

    protected void buscar(Busca busca) {
        Toast.makeText(this, "TODO Criar tela de busca", Toast.LENGTH_LONG).show();
        // TODO Criar tela de busca
        //Intent buscar = new Intent(this, BuscaActivity.class);
        //buscar.putExtra(BuscaActivity.EXTRA_PARAMETROS);
        //startActivityForResult(buscar, REQUEST_BUSCA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BUSCA:
                Toast.makeText(this, "TODO Criar tela de busca", Toast.LENGTH_LONG).show();
                // TODO Criar tela de busca
                //Collection<Relato> problemas = data.getParcelableExtra(BuscaActivity.EXTRA_PROBLEMAS);
                //relatosAdapter.clear();
                //relatosAdapter.addAll(problemas);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
