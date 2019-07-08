package intentservice.example.com.pruebagaribd.View;

import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
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

public class VideoActivity extends AppCompatActivity {

    //implements SurfaceHolder.Callback, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Button videoPlay, videoPausa, videoStop, videoRecord, videoRecordStop;

    ContextWrapper wrapper;
    private static String fichero;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    //private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    public Problema problema = new Problema();
    public AdjuntoProblema adjuntoProblema = new AdjuntoProblema();
    Integer pos;
    String _id, nombreAdjuntoProblema, _idAdjunto;
    Long codFecha;
    private ProgressDialog progresoSubida;
    Boolean subiendoDatos = false;

    private SurfaceView videoSurface;
    private SurfaceHolder surfaceHolder;
    private VideoView videoView;

    private String fileName;
    private String path;
    private String uriVideo;
    Bitmap videoBitmap;
    StorageReference videoRef;
    static UploadTask uploadTask = null;

    private RecyclerView recyclerView;
    public static AdaptadorAdjuntoFirestoreUI adaptador;
    public static AdjuntoProblemasAsinc adjuntoProblemas;
    public static ProblemasAsinc problemas;
    private RecyclerView.LayoutManager layoutManager;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        videoView = (VideoView) findViewById(R.id.videoView);
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");

        _id = RVProblemas.adaptador.getKey(pos);
        problema = RVProblemas.adaptador.getItem(pos);
        codFecha = problema.getFecha();

        problemas = new ProblemasFirestore();
        adjuntoProblemas = new AdjuntoProblemaFirestore();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        query = FirebaseFirestore.getInstance().collection("problemas").document(_id).collection("videos").limit(50);
        FirestoreRecyclerOptions<AdjuntoProblema> opciones = new FirestoreRecyclerOptions.Builder<AdjuntoProblema>().setQuery(query, AdjuntoProblema.class).build();
        adaptador = new AdaptadorAdjuntoFirestoreUI(opciones);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Intent intentVideo = new Intent(VideoActivity.this, VerVideoActivity.class);
                intentVideo.putExtra("posicion", pos);
                startActivity(intentVideo);
            }
        });
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Botones de grabación.
        videoRecord = (Button) findViewById(R.id.videoRecord);
        videoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreAdjuntoProblema = null;
                final EditText entrada = new EditText(VideoActivity.this);
                new AlertDialog.Builder(VideoActivity.this)
                        .setTitle("SELECCIÓN DE NOMBRE:")
                        .setMessage("indica el nombre del adjunto:")
                        .setView(entrada)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                nombreAdjuntoProblema = entrada.getText().toString();
                                grabar2();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

    }

    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void grabar2() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            final ProgressDialog progresoSubida = new ProgressDialog(this);
            progresoSubida.setTitle("Subiendo...");
            progresoSubida.setMessage("Espere...");
            progresoSubida.setCancelable(true);
            progresoSubida.setCanceledOnTouchOutside(false);

            videoRef = RVProblemas.getStorageReference().child(String.valueOf(codFecha) + "/videos/" + System.currentTimeMillis() + ".mp4");

            uploadTask = videoRef.putFile(videoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progresoSubida.show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // get the image Url of the file uploaded
                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // getting image uri and converting into string
                            Uri downloadUrl = uri;
                            RVProblemas.problemas.actualiza(_id, problema);
                            RVProblemas.adaptador.notifyDataSetChanged();

                            _idAdjunto = RVProblemas.adjuntoProblemas.nuevo();
                            adjuntoProblema.setUrl(downloadUrl.toString());
                            adjuntoProblema.setProblemaId(_id);
                            adjuntoProblema.setNombre(nombreAdjuntoProblema);
                            adjuntoProblema.setRefArchivo(videoRef.getPath());
                            adjuntoProblema.setTipo("VIDEO");

                            RVProblemas.adjuntoProblemas.actualiza(_idAdjunto, adjuntoProblema);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("problemas").document(_id).collection("videos").document(_idAdjunto).set(adjuntoProblema);
                            adaptador.notifyDataSetChanged();

                            progresoSubida.dismiss();

                        }
                    });
                }
            });
        }
    }
}
