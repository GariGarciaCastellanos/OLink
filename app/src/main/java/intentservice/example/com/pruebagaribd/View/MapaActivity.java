package intentservice.example.com.pruebagaribd.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.Bd.ProblemasBD;
import intentservice.example.com.pruebagaribd.R;
import intentservice.example.com.pruebagaribd.TipoProblema;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    double latitud;
    double longitud;
    private ProblemasBD problemas;
    private Problema problema;
    Spinner spinnerTipo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        problemas = new ProblemasBD(this);
        spinnerTipo = (Spinner) findViewById(R.id.spinnerTipo);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TipoProblema.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adaptador);
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MapaActivity.this, "Los problemas de " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
                if(parent.getItemAtPosition(position).toString().contains("Noise")) verProblemasTipo("NOISE");
                if(parent.getItemAtPosition(position).toString().contains("Manufacture")) verProblemasTipo("MANUFACTURE");
                if(parent.getItemAtPosition(position).toString().contains("delivered")) verProblemasTipo("DELIVERED");
                if(parent.getItemAtPosition(position).toString().contains("construction")) verProblemasTipo("CONSTRUCTION");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }

    public void verProblemasTodos(View view) {
        Toast.makeText(MapaActivity.this, "Todos los problemas", Toast.LENGTH_LONG).show();
        FirebaseFirestore.getInstance().collection("problemas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                problema = document.toObject(Problema.class);
                                latitud = problema.getLatitud();
                                longitud = problema.getLongitud();
                                if (problema != null && latitud != 0 && longitud != 0) {
                                    mapa.addMarker(new MarkerOptions()
                                            .position(new LatLng(latitud, longitud))
                                            .title(problema.getTipo().getTexto()));
                                }
                            }

                        }
                    }
                });
    }

    public void verProblemasTipo(String filtro) {
        mapa.clear();
        FirebaseFirestore.getInstance().collection("problemas")
                .whereEqualTo("tipo",filtro)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                problema = document.toObject(Problema.class);
                                latitud = problema.getLatitud();
                                longitud = problema.getLongitud();
                                if (problema != null && latitud != 0 && longitud != 0) {
                                    mapa.addMarker(new MarkerOptions()
                                            .position(new LatLng(latitud, longitud))
                                            .title(problema.getTipo().getTexto()));
                                }
                            }

                        }
                    }
                });
    }

}
