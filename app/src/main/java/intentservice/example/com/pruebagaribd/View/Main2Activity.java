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
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Main2Activity extends AppCompatActivity{ //implements LocationListener {

//    private RecyclerView recyclerView;
//    public static MiAdaptador adaptador;
//    public static ProblemasBD problemas;
//    public Problema problema;
//    private RecyclerView.LayoutManager layoutManager;
//
//    private LocationManager manejador;
//    private Location mejorLocaliz;
//    private static final int SOLICITUD_PERMISO_LOCALIZACION = 4;
//    private double latitud,latitudReal;
//    private double longitud,longitudReal;
//
//    boolean gps_enabled;
//    boolean network_enabled;
//    Intent intentAnyadir;
//    boolean problemaNuevo=false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        problemas = new ProblemasBD(this);
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        adaptador = new MiAdaptador(this, problemas, problemas.extraeCursor());
//        adaptador.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int pos = recyclerView.getChildAdapterPosition(v);
//                problema = adaptador.problemaPosicion((pos));
//                Intent i = new Intent(Main2Activity.this, VistaProblema.class);
//                i.putExtra("posicion", pos);
//                startActivity(i);
//            }
//        });
//        recyclerView.setAdapter(adaptador);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
//        checkLocationOnOff();
//        ultimaLocalizacion();
//        activarProveedores();
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_problemas, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.anadir:
//                problemaNuevo=true;
//                ultimaLocalizacion();
//                intentAnyadir = new Intent(Main2Activity.this, EditarProblemaActivity.class);
//                intentAnyadir.putExtra("problemaNuevo",problemaNuevo);
//                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                dialog.setMessage("Se encuentra en el sitio del problema?");
//                dialog.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        intentAnyadir.putExtra("latitud", latitud);
//                        intentAnyadir.putExtra("longitud", longitud);
//                        startActivity(intentAnyadir);
//                    }
//                });
//                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        intentAnyadir.putExtra("latitud", 0);
//                        intentAnyadir.putExtra("longitud", 0);
//                        startActivity(intentAnyadir);
//                    }
//                });
//                dialog.show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    void ultimaLocalizacion() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
//                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//            }
//            if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
//            }
//        }
//    }
//
//    private void activarProveedores() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
//                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, (LocationListener) this);
//            }
//            if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 10, (LocationListener) this);
//            }
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        activarProveedores();
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        activarProveedores();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        activarProveedores();
//    }
//
//    private void actualizaMejorLocaliz(Location localiz) {
//        if (localiz != null && (mejorLocaliz == null
//                || localiz.getAccuracy() < 2 * mejorLocaliz.getAccuracy()
//                || localiz.getTime() - mejorLocaliz.getTime() > 2 * 60 * 1000)) {
//            mejorLocaliz = localiz;
//            latitud = localiz.getLatitude();
//            longitud = localiz.getLongitude();
//        }
//    }
//
//    private void checkLocationOnOff(){
//        try {
//            gps_enabled = manejador.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        }catch (Exception ex){}
//        try{
//            network_enabled = manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        }catch (Exception ex){}
//        if(!gps_enabled && !network_enabled){
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setMessage("Desea activar la Localización?");
//            dialog.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(myIntent);
//                }
//            });
//            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
//            dialog.show();
//        }
//    }
//
//

}
