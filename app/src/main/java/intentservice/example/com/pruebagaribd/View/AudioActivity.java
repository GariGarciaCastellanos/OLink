package intentservice.example.com.pruebagaribd.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import intentservice.example.com.pruebagaribd.Adaptadores.AdaptadorAdjuntoFirestoreUI;
import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemaFirestore;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasFirestore;
import intentservice.example.com.pruebagaribd.PermisosUtilidades;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.Bd.ProblemasBD;
import intentservice.example.com.pruebagaribd.R;

public class AudioActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Button audioPlay, audioRecord, audioRecordStop;
    private ProgressBar progressBar;

    ContextWrapper wrapper;
    private static String fichero;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    Uri uriAudio;
    public Problema problema = new Problema();
    public AdjuntoProblema adjuntoProblema = new AdjuntoProblema();
    Integer pos;
    String _id,nombreAdjuntoProblema,_idAdjunto;
    Long codFecha;
    StorageReference audioRef;
    static UploadTask uploadTask = null;
    File file;

    private RecyclerView recyclerView;
    public static AdaptadorAdjuntoFirestoreUI adaptador;
    public static AdjuntoProblemasAsinc adjuntoProblemas;
    public static ProblemasAsinc problemas;
    private RecyclerView.LayoutManager layoutManager;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        _id = RVProblemas.adaptador.getKey(pos);
        problema = RVProblemas.adaptador.getItem(pos);
        codFecha = problema.getFecha();

        //Botones de grabación.
        audioRecord = (Button) findViewById(R.id.audioRecord);
        audioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText entrada = new EditText(AudioActivity.this);
                new AlertDialog.Builder(AudioActivity.this)
                        .setTitle("SELECCIÓN DE NOMBRE:")
                        .setMessage("indica el nombre del adjunto:")
                        .setView(entrada)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                nombreAdjuntoProblema = entrada.getText().toString();
                                grabar();
                            }})
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        audioRecordStop = (Button) findViewById(R.id.audioRecordStop);
        audioRecordStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerGrabacion();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
        } else {
            PermisosUtilidades.solicitarPermiso(Manifest.permission.RECORD_AUDIO,
                    "Sin el permiso de grabación de audio no se puede grabar ningún audio ",
                    REQUEST_RECORD_AUDIO_PERMISSION, this);
        }

        problemas = new ProblemasFirestore();
        adjuntoProblemas = new AdjuntoProblemaFirestore();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        query = FirebaseFirestore.getInstance().collection("problemas").document(_id).collection("audios").limit(50);
        FirestoreRecyclerOptions<AdjuntoProblema> opciones = new FirestoreRecyclerOptions.Builder<AdjuntoProblema>().setQuery(query, AdjuntoProblema.class).build();
        adaptador = new AdaptadorAdjuntoFirestoreUI(opciones);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                adjuntoProblema = adaptador.getItem(pos);
                reproducir(adjuntoProblema);
            }
        });
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void grabar() {
        mediaRecorder = new MediaRecorder();
        fichero = "audio_" + (System.currentTimeMillis() / 1000) + ".3gp";

        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        file = wrapper.getDir("Audios", MODE_PRIVATE);
        file = new File(file, fichero);
        uriAudio = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(file);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
        }
        mediaRecorder.start();
    }

    public void detenerGrabacion() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        audioRef = RVProblemas.getStorageReference().child(String.valueOf(codFecha) + "/audios/" + System.currentTimeMillis() + ".3gp");
        String ruta = audioRef.getPath();

        uploadTask = audioRef.putFile(uriAudio);
        RVProblemas.problemas.actualiza(_id, problema);
        RVProblemas.adaptador.notifyDataSetChanged();

        final ProgressDialog progresoSubida = new ProgressDialog(this);
        progresoSubida.setTitle("Subiendo...");
        progresoSubida.setMessage("Espere...");
        progresoSubida.setCancelable(true);
        progresoSubida.setCanceledOnTouchOutside(false);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progresoSubida.show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        RVProblemas.problemas.actualiza(_id, problema);
                        RVProblemas.adaptador.notifyDataSetChanged();

                        _idAdjunto = RVProblemas.adjuntoProblemas.nuevo();
                        adjuntoProblema.setUrl(downloadUrl.toString());
                        adjuntoProblema.setProblemaId(_id);
                        adjuntoProblema.setNombre(nombreAdjuntoProblema);
                        adjuntoProblema.setRefArchivo(audioRef.getPath());
                        adjuntoProblema.setTipo("AUDIO");

                        RVProblemas.adjuntoProblemas.actualiza(_idAdjunto, adjuntoProblema);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("problemas").document(_id).collection("audios").document(_idAdjunto).set(adjuntoProblema);
                        adaptador.notifyDataSetChanged();

                    }
                });
            }
        });

    }

    public void reproducir(AdjuntoProblema adjuntoProblema) {
        mediaPlayer = new MediaPlayer();
//        mediaController=new MediaController(this);
        //ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        //File f = wrapper.getDir("audios", MODE_PRIVATE);
        String f = adjuntoProblema.getUrl();

        try {
            mediaPlayer.setDataSource(f);
            mediaPlayer.prepare();
            mediaPlayer.start();
//            mediaController.setMediaPlayer(this);
//            mediaController.setAnchorView(findViewById(R.id.layout_audio));
//            mediaController.setEnabled(true);
//            mediaController.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            progressBar.setMin(0);
        }
        progressBar.setMax(100);


        //Introducir un Thread
        MiThread thread = new MiThread();
        thread.start();
        //progressBar.setVisibility(View.GONE);
    }

    public void pausa() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            detenerGrabacion();
        }
        if (mediaPlayer != null) {
            stop();
        }
        if (mediaController != null) {
            mediaController.hide();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    class MiThread extends Thread {
        private int progressStatus = 0;

        public MiThread() {
        }

        @Override
        public void run() {
            while (progressStatus < 100) {

//                progressStatus = ((mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
//                progressBar.setProgress(progressStatus);
            }

        }
    }

}
