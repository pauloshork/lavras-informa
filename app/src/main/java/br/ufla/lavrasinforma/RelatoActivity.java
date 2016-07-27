package br.ufla.lavrasinforma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import br.ufla.lavrasinforma.model.BuscaComentario;
import br.ufla.lavrasinforma.model.Classificacao;
import br.ufla.lavrasinforma.model.Comentario;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Status;
import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class RelatoActivity extends AppCompatActivity {

    private static final int REQUEST_COMENTARIO = 1;
    private static final int REQUEST_RELATAR = 2;
    private static final int REQUEST_FOTO = 3;
    private static final int REQUEST_GPS = 4;
    private static final int REQUEST_COMENTAR = 5;

    public static final String ACTION_EDITAVEL = "visualizar-editavel";
    public static final String ACTION_LEITURA = "visualizar-leitura";
    public static final String EXTRA_RELATO = "relato";
    private static final String EXTRA_COMENTARIO = "comentario";

    private boolean editavel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getIntent().getAction()) {
            case ACTION_EDITAVEL:
                stateEditavel();
                break;
            case ACTION_LEITURA:
            default:
                stateLeitura();
        }
    }

    protected void stateEditavel() {
        setContentView(R.layout.activity_relato_editavel);
        editavel = true;

        NetworkImageView foto = (NetworkImageView) findViewById(R.id.foto);
        EditText txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        TextView txtAutor = (TextView) findViewById(R.id.txtAutor);
        TextView txtData = (TextView) findViewById(R.id.txtData);
        Spinner txtClassificacao = (Spinner) findViewById(R.id.txtClassificacao);
        Spinner txtStatus = (Spinner) findViewById(R.id.txtStatus);
        EditText txtDescricao = (EditText) findViewById(R.id.txtDescricao);

        Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);

        if (relato != null) {
            if (relato.getId() != null) {
                foto.setImageUrl(WebServiceConnector.getInstance().buscarImagem(this, UtilSession.getAccessToken(this), relato), WebServiceConnector.getImageLoader(this));
            } else {
                foto.setImageUrl(null, WebServiceConnector.getImageLoader(this));
            }

            txtTitulo.setText(relato.getTitulo());
            if (relato.getNomeUsuario() != null) {
                txtAutor.setText(relato.getNomeUsuario());
            } else {
                txtAutor.setVisibility(View.GONE);
            }
            if (relato.getData() != null) {
                txtData.setText(relato.getData().toString());
            } else {
                txtData.setVisibility(View.GONE);
            }
            txtClassificacao.setSelection(relato.getClassificacao().getValor());
            txtStatus.setSelection(relato.getStatus().getValor());
            txtDescricao.setText(relato.getDescricao());
        } else {
            txtAutor.setVisibility(View.GONE);
            txtData.setVisibility(View.GONE);
        }
    }

    protected void stateLeitura() {
        setContentView(R.layout.activity_relato_leitura);
        editavel = false;

        NetworkImageView foto = (NetworkImageView) findViewById(R.id.foto);
        TextView txtTitulo = (TextView) findViewById(R.id.txtTitulo);
        TextView txtAutor = (TextView) findViewById(R.id.txtAutor);
        TextView txtData = (TextView) findViewById(R.id.txtData);
        TextView txtClassificacao = (TextView) findViewById(R.id.txtClassificacao);
        TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
        TextView txtDescricao = (TextView) findViewById(R.id.txtDescricao);

        Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);

        if (relato != null) {
            foto.setImageUrl(WebServiceConnector.getInstance().buscarImagem(this, UtilSession.getAccessToken(this), relato), WebServiceConnector.getImageLoader(this));
            txtTitulo.setText(relato.getTitulo());
            txtAutor.setText(relato.getNomeUsuario());
            txtData.setText(relato.getData().toString());
            txtClassificacao.setText(relato.getClassificacao().toString());
            txtStatus.setText(relato.getStatus().toString());
            txtDescricao.setText(relato.getDescricao());
        }

        carregarComentarios();
    }

    protected void carregarComentarios() {
        if (!editavel) {
            final TextView semComentarios = (TextView) findViewById(R.id.lblSemComentarios);
            final LinearLayout list = (LinearLayout) findViewById(R.id.lstComentarios);
            final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);

            Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);

            list.removeAllViews();
            if (relato == null) {
                progress.setVisibility(View.GONE);
                semComentarios.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.VISIBLE);
                semComentarios.setVisibility(View.GONE);

                BuscaComentario buscaComentario = new BuscaComentario();
                buscaComentario.setIdRelato(relato.getId());
                WebServiceConnector.getInstance().buscarComentarios(this, UtilSession.getAccessToken(this), buscaComentario, new Callback<ArrayList<Comentario>>() {
                    @Override
                    public void onSuccess(ArrayList<Comentario> comentarios) {
                        for (Comentario c : comentarios) {
                            View v = criarViewComentario(c);
                            list.addView(v);
                        }
                    }

                    @Override
                    public void onCancel() {
                        progress.setVisibility(View.GONE);
                        semComentarios.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable error) {
                        progress.setVisibility(View.GONE);
                        semComentarios.setVisibility(View.VISIBLE);
                        if (!UtilSession.relogar(RelatoActivity.this, error, REQUEST_COMENTARIO)) {
                            WebServiceConnector.mostrarDialogoErro(RelatoActivity.this, error);
                        }
                    }
                }, false);
            }
        }
    }

    protected View criarViewComentario(Comentario comentario) {
        View v = getLayoutInflater().inflate(R.layout.item_comentario, null);

        TextView txtAutor = (TextView) v.findViewById(R.id.txtAutor);
        TextView txtData = (TextView) v.findViewById(R.id.txtData);
        TextView txtComentario = (TextView) v.findViewById(R.id.txtComentario);

        txtAutor.setText(comentario.getNomeUsuario());
        txtData.setText(comentario.getData().toString());
        txtComentario.setText(comentario.getTexto());

        return v;
    }

    public void comentar(View view) {
        EditText txtComentar = (EditText) findViewById(R.id.txtComentar);


        Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);
        String texto = txtComentar.getText().toString();
        if (!texto.isEmpty()) {
            Comentario comentario = new Comentario();
            comentario.setIdRelato(relato.getId());
            comentario.setTexto(txtComentar.getText().toString());

            comentar(comentario);
        }
    }

    private void comentar(Comentario comentario) {
        getIntent().putExtra(EXTRA_COMENTARIO, comentario);
        WebServiceConnector.getInstance().enviarComentario(this, UtilSession.getAccessToken(this), comentario, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RelatoActivity.this, "Comentário enviado com sucesso.", Toast.LENGTH_LONG).show();
                carregarComentarios();
            }

            @Override
            public void onCancel() {
                Toast.makeText(RelatoActivity.this, "Comentário não enviado.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error) {
                if (!UtilSession.relogar(RelatoActivity.this, error, REQUEST_COMENTAR)) {
                    WebServiceConnector.mostrarDialogoErro(RelatoActivity.this, error);
                }
            }
        }, true);
    }

    public void relatar(View view) {
        final Relato relato;
        if (getIntent().hasExtra(EXTRA_RELATO)) {
            relato = getIntent().getParcelableExtra(EXTRA_RELATO);
        } else {
            relato = new Relato();
        }

        NetworkImageView foto = (NetworkImageView) findViewById(R.id.foto);
        EditText txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        Spinner txtClassificacao = (Spinner) findViewById(R.id.txtClassificacao);
        Spinner txtStatus = (Spinner) findViewById(R.id.txtStatus);
        EditText txtDescricao = (EditText) findViewById(R.id.txtDescricao);

        if (foto.getBackground() instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) foto.getBackground()).getBitmap();
            relato.setFoto(bmp);
        }
        relato.setTitulo(txtTitulo.getText().toString());
        relato.setClassificacao(Classificacao.fromValor((byte) txtClassificacao.getSelectedItemPosition()));
        relato.setStatus(Status.fromValor((byte) txtStatus.getSelectedItemPosition()));
        relato.setDescricao(txtDescricao.getText().toString());

        getIntent().putExtra(EXTRA_RELATO, relato);
        relatarGps();
    }

    private void ativarGps() {
        Intent gpsOptionsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(gpsOptionsIntent, REQUEST_GPS);
    }

    public void tirarFoto(View view) {
        Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (foto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(foto, REQUEST_FOTO);
        }
    }

    private void setFoto(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        NetworkImageView foto = (NetworkImageView) findViewById(R.id.foto);
        foto.setBackground(drawable);
    }

    private void relatarGps() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ativarGps();
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Buscando localização...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("gps", location.toString());
                    if (location.getAccuracy() < 25) {
                        dialog.dismiss();
                        Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);
                        relato.setLatitude(location.getLatitude());
                        relato.setLongitude(location.getLongitude());
                        getIntent().putExtra(EXTRA_RELATO, relato);
                        locationManager.removeUpdates(this);
                        relatarWeb();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {
                    dialog.dismiss();
                    locationManager.removeUpdates(this);
                    ativarGps();
                }
            });
        }
    }

    private void relatarWeb() {
        Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);
        WebServiceConnector.getInstance().enviarRelato(this, UtilSession.getAccessToken(this), relato, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RelatoActivity.this, "Relato enviado com sucesso.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(RelatoActivity.this, "Relato não enviado.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable error) {
                if (!UtilSession.relogar(RelatoActivity.this, error, REQUEST_RELATAR)) {
                    WebServiceConnector.mostrarDialogoErro(RelatoActivity.this, error);
                }
            }
        }, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_RELATAR:
                relatar(findViewById(R.id.btnRelatar));
                break;
            case REQUEST_COMENTARIO:
                carregarComentarios();
                break;
            case REQUEST_GPS:
                relatarGps();
                break;
            case REQUEST_FOTO:
                switch (resultCode) {
                    case RESULT_OK:
                        setFoto((Bitmap) data.getExtras().get("data"));
                        break;
                }
                break;
            case REQUEST_COMENTAR:
                Comentario comentario = getIntent().getParcelableExtra(EXTRA_COMENTARIO);
                comentar(comentario);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
