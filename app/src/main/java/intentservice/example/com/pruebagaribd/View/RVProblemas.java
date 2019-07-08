package intentservice.example.com.pruebagaribd.View;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import intentservice.example.com.pruebagaribd.Adaptadores.AdaptadorFirestoreUI;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemaFirestore;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.Bd.ProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasFirestore;
import intentservice.example.com.pruebagaribd.R;

public class RVProblemas extends AppCompatActivity implements LocationListener {

    private RecyclerView recyclerView;
    public static AdaptadorFirestoreUI adaptador;
    public static ProblemasAsinc problemas;
    public Problema problema;
    public static AdjuntoProblemaFirestore adjuntoProblemas;
    private RecyclerView.LayoutManager layoutManager;

    private LocationManager manejador;
    private Location mejorLocaliz;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 4;
    private double latitud, latitudReal;
    private double longitud, longitudReal;

    boolean gps_enabled;
    boolean network_enabled;
    Intent intentAnyadir;
    boolean problemaNuevo = false;
    Query query;

    static FirebaseStorage storage;
    static StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://problemas-bf6a2.appspot.com");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anyadir();
            }
        });

        problemas = new ProblemasFirestore();
        adjuntoProblemas = new AdjuntoProblemaFirestore();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        query = FirebaseFirestore.getInstance().collection("problemas").limit(50);
        FirestoreRecyclerOptions<Problema> opciones = new FirestoreRecyclerOptions.Builder<Problema>().setQuery(query, Problema.class).build();
        adaptador = new AdaptadorFirestoreUI(opciones);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Intent i = new Intent(RVProblemas.this, VistaProblema.class);
                i.putExtra("posicion", pos);
                startActivity(i);
            }
        });
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkLocationOnOff();
        ultimaLocalizacion();
        activarProveedores();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_problemas, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                adaptadorFiltroBusqueda(query);
                adaptador.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filtroTodos:
                query = FirebaseFirestore.getInstance().collection("problemas").limit(50);
                FirestoreRecyclerOptions<Problema> opciones = new FirestoreRecyclerOptions.Builder<Problema>().setQuery(query, Problema.class).build();
                adaptador = new AdaptadorFirestoreUI(opciones);
                adaptador.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = recyclerView.getChildAdapterPosition(v);
                        Intent i = new Intent(RVProblemas.this, VistaProblema.class);
                        i.putExtra("posicion", pos);
                        startActivity(i);
                    }
                });
                adaptador.startListening();
                recyclerView.setAdapter(adaptador);
                return true;
            case R.id.filtroTipoNoise:
                adaptadorFiltroTipo("NOISE");
                return true;
            case R.id.filtroTipoManufacture:
                adaptadorFiltroTipo("MANUFACTURE");
                return true;
            case R.id.filtroTipoDelivered:
                adaptadorFiltroTipo("DELIVERED");
                return true;
            case R.id.filtroTipoConstruction:
                adaptadorFiltroTipo("CONSTRUCTION");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void adaptadorFiltroTipo(String filtro) {
        query = FirebaseFirestore.getInstance().collection("problemas").limit(50);
        query = query.whereEqualTo("tipo", filtro);
        FirestoreRecyclerOptions<Problema> opciones = new FirestoreRecyclerOptions.Builder<Problema>().setQuery(query, Problema.class).build();
        adaptador = new AdaptadorFirestoreUI(opciones);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Intent i = new Intent(RVProblemas.this, VistaProblema.class);
                i.putExtra("posicion", pos);
                startActivity(i);
            }
        });
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
    }

    private void adaptadorFiltroBusqueda(String busqueda) {
        query = FirebaseFirestore.getInstance().collection("problemas").limit(50);
        query = query.whereEqualTo("nombre", busqueda);
        FirestoreRecyclerOptions<Problema> opciones = new FirestoreRecyclerOptions.Builder<Problema>().setQuery(query, Problema.class).build();
        adaptador = new AdaptadorFirestoreUI(opciones);
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
    }

    void anyadir() {
        problemaNuevo = true;
        ultimaLocalizacion();
        intentAnyadir = new Intent(RVProblemas.this, EditarProblemaActivity.class);
        intentAnyadir.putExtra("problemaNuevo", problemaNuevo);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Se encuentra en el sitio del problema?");
        dialog.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                intentAnyadir.putExtra("latitud", latitud);
                intentAnyadir.putExtra("longitud", longitud);
                startActivity(intentAnyadir);
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                intentAnyadir.putExtra("latitud", 0);
                intentAnyadir.putExtra("longitud", 0);
                startActivity(intentAnyadir);
            }
        });
        dialog.show();
    }

    void ultimaLocalizacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        }
    }

    private void activarProveedores() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, (LocationListener) this);
            }
            if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 10, (LocationListener) this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String provider) {
        activarProveedores();
    }

    @Override
    public void onProviderDisabled(String provider) {
        activarProveedores();
    }

    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (mejorLocaliz == null
                || localiz.getAccuracy() < 2 * mejorLocaliz.getAccuracy()
                || localiz.getTime() - mejorLocaliz.getTime() > 2 * 60 * 1000)) {
            mejorLocaliz = localiz;
            latitud = localiz.getLatitude();
            longitud = localiz.getLongitude();
        }
    }

    private void checkLocationOnOff() {
        try {
            gps_enabled = manejador.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Desea activar la Localización?");
            dialog.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    ultimaLocalizacion();
                    activarProveedores();
                }
            });
            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    public static StorageReference getStorageReference() {
        return storageRef;
    }

}

