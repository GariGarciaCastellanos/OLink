package intentservice.example.com.pruebagaribd.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import intentservice.example.com.pruebagaribd.Adaptadores.AdaptadorAdjuntoFirestoreUI;
import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemaFirestore;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasFirestore;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.Bd.ProblemasBD;
import intentservice.example.com.pruebagaribd.R;

public class VibracionesActivity extends AppCompatActivity implements SensorEventListener {

    private Button empezarGrabacion, pararGrabacion, verGrabacion;
    private TextView textVibraciones;
    public boolean estadoGrabacion = false;
    public Problema problema = new Problema();
    public AdjuntoProblema adjuntoProblema = new AdjuntoProblema();
    Integer pos;
    float valor;
    SensorManager sensorManager;
    Sensor acelerometerSensor;
    public String FICHERO;
    public Context context;
    long codFecha;
    private int uTiempo = 1;
    public FileOutputStream f;
    public String texto, _id, _idAdjunto, nombreAdjuntoProblema;
    /// Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    /// Cuando se realizó el último proceso
    private long ultimoProceso = 0;
    double factorTiempo;
    ThreadGrabando threadGrabando;

    static UploadTask uploadTask = null;
    StorageReference imagenRef;

    private RecyclerView recyclerView;
    public static AdaptadorAdjuntoFirestoreUI adaptador;
    public static AdjuntoProblemasAsinc adjuntoProblemas;
    public static ProblemasAsinc problemas;
    private RecyclerView.LayoutManager layoutManager;
    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibraciones);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        threadGrabando = new ThreadGrabando();

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        _id = RVProblemas.adaptador.getKey(pos);
        problema = RVProblemas.adaptador.getItem(pos);
        codFecha = problema.getFecha();

        problemas = new ProblemasFirestore();
        adjuntoProblemas = new AdjuntoProblemaFirestore();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        query = FirebaseFirestore.getInstance().collection("problemas").document(_id).collection("vibraciones").limit(50);
        FirestoreRecyclerOptions<AdjuntoProblema> opciones = new FirestoreRecyclerOptions.Builder<AdjuntoProblema>().setQuery(query, AdjuntoProblema.class).build();
        adaptador = new AdaptadorAdjuntoFirestoreUI(opciones);
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        empezarGrabacion = (Button) findViewById(R.id.empezarGrabacion);
        empezarGrabacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText entrada = new EditText(VibracionesActivity.this);
                new AlertDialog.Builder(VibracionesActivity.this)
                        .setTitle("SELECCIÓN DE NOMBRE:")
                        .setMessage("indica el nombre del adjunto:")
                        .setView(entrada)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                nombreAdjuntoProblema = entrada.getText().toString();
                                activarSensores();
                                estadoGrabacion = true;
                                threadGrabando.start();
                            }})
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        pararGrabacion = (Button) findViewById(R.id.pararGrabacion);
        pararGrabacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoGrabacion = false;
                try {
                    f.write(texto.getBytes());
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagenRef = RVProblemas.getStorageReference().child(String.valueOf(codFecha) + "/vibraciones/" + System.currentTimeMillis() + ".txt");
                Uri uri = Uri.fromFile(getFileStreamPath(FICHERO));

                final ProgressDialog progresoSubida = new ProgressDialog(VibracionesActivity.this);
                progresoSubida.setTitle("Subiendo...");
                progresoSubida.setMessage("Espere...");
                progresoSubida.setCancelable(true);
                progresoSubida.setCanceledOnTouchOutside(false);

                uploadTask = imagenRef.putFile(uri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progresoSubida.show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        desactivarSensores();

                        _idAdjunto = RVProblemas.adjuntoProblemas.nuevo();
                        adjuntoProblema.setProblemaId(problema.getFecha().toString());
                        adjuntoProblema.setNombre(nombreAdjuntoProblema);
                        adjuntoProblema.setTipo("VIBRACIONES");

                        RVProblemas.adjuntoProblemas.actualiza(_idAdjunto, adjuntoProblema);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> datos = new HashMap<>();
                        datos.put("idAdjunto", adjuntoProblema.getIdTiempo());
                        datos.put("nombre", nombreAdjuntoProblema);
                        db.collection("problemas").document(_id).collection("vibraciones").document(_idAdjunto).set(datos);
                        adaptador.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void grabar() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        factorTiempo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora;  // Para la próxima vez

        FICHERO = "FicheroVibraciones_" + System.currentTimeMillis() + ".txt";
        try {
            f = openFileOutput(FICHERO, Context.MODE_APPEND);
            texto = factorTiempo + " " + valor + "\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        valor = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void activarSensores() {
        acelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (acelerometerSensor != null) {
            sensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void desactivarSensores() {
        sensorManager.unregisterListener(this);
    }

//    public List<String> verVibraciones() {
//        List<String> result = new ArrayList<String>();
//        String file = problema.getGrabacionAceleracion();
//        try {
//            FileInputStream f_entrada = openFileInput(file);
//            BufferedReader entrada = new BufferedReader(new InputStreamReader(f_entrada));
//            int n = 0;
//            String linea;
//            do {
//                linea = entrada.readLine();
//                if (linea != null) {
//                    result.add(linea);
//                    n++;
//                }
//            } while (linea != null);
//            f_entrada.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    class ThreadGrabando extends Thread {

        public ThreadGrabando() {
        }

        @Override
        public void run() {
            while (estadoGrabacion = true) {
                grabar();
            }
        }
    }

}
