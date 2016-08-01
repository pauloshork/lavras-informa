package br.ufla.lavrasinforma;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import br.ufla.lavrasinforma.model.AccessToken;
import br.ufla.lavrasinforma.model.BuscaRelato;
import br.ufla.lavrasinforma.model.Relato;
import br.ufla.lavrasinforma.model.Usuario;
import br.ufla.lavrasinforma.model.web.Callback;
import br.ufla.lavrasinforma.model.web.WebServiceConnector;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<Marker, Relato> relatos;

    private static final String EXTRA_RELATO = "relato";
    private static final int REQUEST_VER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        relatos = new HashMap<>();
        mMap = null;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {
            buscarRelatos();
        }
    }

    private void buscarRelatos() {
        mMap.clear();
        relatos.clear();

        BuscaRelato busca = new BuscaRelato();
        WebServiceConnector.getInstance().buscarRelatos(this, UtilSession.getAccessToken(this), busca, new Callback<ArrayList<Relato>>() {
            @Override
            public void onSuccess(ArrayList<Relato> relatos) {
                for (Relato r : relatos) {
                    LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
                    float cor;
                    switch (UtilConvert.positionFromStatus(r.getStatus())) {
                        case 1:
                            cor = BitmapDescriptorFactory.HUE_RED;
                            break;
                        case 2:
                            cor = BitmapDescriptorFactory.HUE_YELLOW;
                            break;
                        case 3:
                            cor = BitmapDescriptorFactory.HUE_GREEN;
                            break;
                        default:
                            cor = BitmapDescriptorFactory.HUE_MAGENTA;
                    }
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(pos)
                            .title(r.getTitulo())
                            .icon(BitmapDescriptorFactory.defaultMarker(cor))
                            .snippet(r.getClassificacao());
                    MapaActivity.this.relatos.put(mMap.addMarker(markerOptions), r);
                }
            }
        }, true);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lavras = new LatLng(-21.248086, -45.001045);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lavras, 10));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Relato relato = relatos.get(marker);
                getIntent().putExtra(EXTRA_RELATO, relato);
                return false;
            }
        });

        buscarRelatos();
    }

    public void verRelato(View view) {
        final Relato relato = getIntent().getParcelableExtra(EXTRA_RELATO);

        if (relato == null) {
            Toast.makeText(this, "Nenhum relato selecionado.", Toast.LENGTH_LONG).show();
        } else {
            AccessToken accessToken = UtilSession.getAccessToken(MapaActivity.this);
            WebServiceConnector.getInstance().getUsuario(MapaActivity.this, accessToken, new Callback<Usuario>() {
                @Override
                public void onSuccess(Usuario usuario) {
                    Intent verRelato = new Intent(MapaActivity.this, RelatoActivity.class);
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
                    if (!UtilSession.relogar(MapaActivity.this, error, REQUEST_VER)) {
                        WebServiceConnector.mostrarDialogoErro(MapaActivity.this, error);
                    }
                }
            }, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_VER:
                verRelato(findViewById(R.id.btnVerRelato));
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
