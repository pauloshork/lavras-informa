package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;

import br.ufla.lavrasinforma.model.BuscaRelato;
import br.ufla.lavrasinforma.model.Classificacao;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Status;
import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class BuscaActivity extends AppCompatActivity {

    private static final int REQUEST_BUSCA = 1;

    public static final String EXTRA_PARAMETROS = "parametros";
    public static final String EXTRA_RELATOS = "relatos";

    protected static final String EXTRA_BUSCA = "busca";

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);

        BuscaRelato buscaRelato = null;

        if (getIntent().hasExtra(EXTRA_PARAMETROS)) {
            buscaRelato = getIntent().getParcelableExtra(EXTRA_PARAMETROS);
        }

        if (buscaRelato != null) {
            buscar(buscaRelato);
        }
    }

    public void buscar(View view) {
        EditText txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        EditText txtAutor = (EditText) findViewById(R.id.txtAutor);
        EditText txtData = (EditText) findViewById(R.id.txtData);
        Spinner txtClassificacao = (Spinner) findViewById(R.id.txtClassificacao);
        Spinner txtStatus = (Spinner) findViewById(R.id.txtStatus);
        CheckBox cbxComFoto = (CheckBox) findViewById(R.id.cbxComFoto);
        CheckBox cbxSemFoto = (CheckBox) findViewById(R.id.cbxSemFoto);

        BuscaRelato buscaRelato = new BuscaRelato();

        if (txtTitulo != null) {
            String titulo = txtTitulo.getText().toString();
            if (!titulo.isEmpty()) {
                buscaRelato.setTitulo(titulo);
            }
        }

        if (txtAutor != null) {
            String autor = txtAutor.getText().toString();
            if (!autor.isEmpty()) {
                buscaRelato.setAutor(autor);
            }
        }

        if (txtData != null) {
            String data = txtData.toString();
            if (!data.isEmpty()) {
                try {
                    buscaRelato.setData(DateFormat.getDateInstance().parse(data));
                } catch (ParseException e) {
                    // vazio
                }
            }
        }

        if (txtClassificacao != null) {
            Classificacao classificacao = Classificacao.fromValor((byte) txtClassificacao.getSelectedItemPosition());
            if (classificacao != Classificacao.Desconhecido) {
                buscaRelato.setClassificacao(classificacao);
            }
        }

        if (txtStatus != null) {
            Status status = Status.fromValor((byte) txtStatus.getSelectedItemPosition());
            if (status != Status.Desconhecido) {
                buscaRelato.setStatus(status);
            }
        }

        if (cbxComFoto != null && cbxSemFoto != null) {
            boolean comFoto = cbxComFoto.isChecked();
            boolean semFoto = cbxSemFoto.isChecked();
            Boolean foto;
            if ((comFoto && semFoto) || (!comFoto && !semFoto)) {
                foto = null;
            } else {
                foto = comFoto;
            }
            buscaRelato.setFoto(foto);
        }

        buscar(buscaRelato);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }

    protected void buscar(final BuscaRelato buscaRelato) {
        WebServiceConnector.getInstance().buscarRelatos(this, UtilSession.getAccessToken(this), buscaRelato, new Callback<ArrayList<Relato>>() {
            @Override
            public void onSuccess(ArrayList<Relato> relatos) {
                Intent resultado = new Intent();
                resultado.putParcelableArrayListExtra(EXTRA_RELATOS, relatos);
                setResult(RESULT_SUCCESS, resultado);
                finish();
            }

            @Override
            public void onCancel() {
                onBackPressed();
            }

            @Override
            public void onError(Throwable error) {
                getIntent().putExtra(EXTRA_BUSCA, buscaRelato);
                if (!UtilSession.relogar(BuscaActivity.this, error, REQUEST_BUSCA)) {
                    WebServiceConnector.mostrarDialogoErro(BuscaActivity.this, error);
                }
            }
        }, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BUSCA:
                BuscaRelato buscaRelato = getIntent().getParcelableExtra(EXTRA_BUSCA);
                buscar(buscaRelato);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
