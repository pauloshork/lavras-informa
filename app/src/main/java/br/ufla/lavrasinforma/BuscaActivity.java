package br.ufla.lavrasinforma;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import br.ufla.lavrasinforma.model.BuscaRelato;

public class BuscaActivity extends AppCompatActivity {

    public static final String EXTRA_BUSCA = "busca";

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);

        if (getIntent().hasExtra(EXTRA_BUSCA)) {
            BuscaRelato buscaRelato = getIntent().getParcelableExtra(EXTRA_BUSCA);

            EditText txtTitulo = (EditText) findViewById(R.id.txtTitulo);
            EditText txtAutor = (EditText) findViewById(R.id.txtAutor);
            Spinner txtClassificacao = (Spinner) findViewById(R.id.txtClassificacao);
            Spinner txtStatus = (Spinner) findViewById(R.id.txtStatus);
            CheckBox cbxComFoto = (CheckBox) findViewById(R.id.cbxComFoto);
            CheckBox cbxSemFoto = (CheckBox) findViewById(R.id.cbxSemFoto);

            if (txtTitulo != null) {
                txtTitulo.setText(buscaRelato.getTitulo());
            }
            if (txtAutor != null) {
                txtAutor.setText(buscaRelato.getAutor());
            }

            setData(buscaRelato.getData());

            if (txtClassificacao != null) {
                txtClassificacao.setSelection(UtilConvert.positionFromClassificacao(buscaRelato.getClassificacao()));
            }
            if (txtStatus != null) {
                txtStatus.setSelection(UtilConvert.positionFromStatus(buscaRelato.getStatus()));
            }
            if (cbxComFoto != null && cbxSemFoto != null) {
                if (buscaRelato.getFoto() == null) {
                    cbxComFoto.setChecked(true);
                    cbxSemFoto.setChecked(true);
                } else {
                    cbxComFoto.setChecked(buscaRelato.getFoto());
                    cbxSemFoto.setChecked(!buscaRelato.getFoto());
                }
            }

        }
    }

    public void resetData(View view) {
        setData(null);
    }

    private void setData(Date data) {
        BuscaRelato b = getIntent().getParcelableExtra(EXTRA_BUSCA);
        b.setData(data);
        getIntent().putExtra(EXTRA_BUSCA, b);

        Button btnData = (Button) findViewById(R.id.btnData);
        if (btnData != null) {
            if (data == null) {
                btnData.setText("Data");
            } else {
                btnData.setText(DateFormat.getDateInstance().format(data));
            }
        }
    }

    public void selecionarData(View view) {
        Date data = ((BuscaRelato) getIntent().getParcelableExtra(EXTRA_BUSCA)).getData();
        int ano = 2010, mes = 0, dia = 1;
        if (data != null) {
            ano = data.getYear() + 1900;
            mes = data.getMonth();
            dia = data.getDate();
        }
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                setData(new Date(y - 1900, m, d));
            }
        }, ano, mes, dia);
        dialog.show();
    }

    public void buscar(View view) {
        EditText txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        EditText txtAutor = (EditText) findViewById(R.id.txtAutor);
        Button txtData = (Button) findViewById(R.id.btnData);
        Spinner txtClassificacao = (Spinner) findViewById(R.id.txtClassificacao);
        Spinner txtStatus = (Spinner) findViewById(R.id.txtStatus);
        CheckBox cbxComFoto = (CheckBox) findViewById(R.id.cbxComFoto);
        CheckBox cbxSemFoto = (CheckBox) findViewById(R.id.cbxSemFoto);

        BuscaRelato buscaRelato = getIntent().getParcelableExtra(EXTRA_BUSCA);
        if (buscaRelato == null) {
            buscaRelato = new BuscaRelato();
        }

        if (txtTitulo != null) {
            String titulo = txtTitulo.getText().toString();
            if (!titulo.isEmpty()) {
                buscaRelato.setTitulo(titulo);
            } else {
                buscaRelato.setTitulo(null);
            }
        }

        if (txtAutor != null) {
            String autor = txtAutor.getText().toString();
            if (!autor.isEmpty()) {
                buscaRelato.setAutor(autor);
            } else {
                buscaRelato.setAutor(null);
            }
        }

        if (txtData != null) {
            String data = txtData.getText().toString();
            if (!data.isEmpty()) {
                try {
                    buscaRelato.setData(DateFormat.getDateInstance().parse(data));
                } catch (ParseException e) {
                    Toast.makeText(this, "Data com o formato incorreto", Toast.LENGTH_LONG).show();
                }
            } else {
                buscaRelato.setData(null);
            }
        }

        if (txtClassificacao != null) {
            String classificacao = UtilConvert.classificacaoFromPosition(txtClassificacao.getSelectedItemPosition());
            buscaRelato.setClassificacao(classificacao);
        }

        if (txtStatus != null) {
            String status = UtilConvert.statusFromPosition(txtStatus.getSelectedItemPosition());
            buscaRelato.setStatus(status);
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
        getIntent().putExtra(EXTRA_BUSCA, buscaRelato);

        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_BUSCA, buscaRelato);
        setResult(RESULT_SUCCESS, resultado);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }
}
