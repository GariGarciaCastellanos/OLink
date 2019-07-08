package intentservice.example.com.pruebagaribd.View;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.R;

public class VerVideoActivity extends AppCompatActivity {

    VideoView videoView;
    private String uriVideo;
    public AdjuntoProblema adjuntoProblema = new AdjuntoProblema();
    public int pos;
    long codFecha;
    String _id;
    private Button videoPlay, videoPausa, videoStop;
    StorageReference imagenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vervideo);
        videoView = (VideoView) findViewById(R.id.videoView);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        _id = VideoActivity.adaptador.getKey(pos);
        adjuntoProblema = VideoActivity.adaptador.getItem(pos);
        codFecha = adjuntoProblema.getIdTiempo();


        uriVideo = adjuntoProblema.getUrl();
        if (!uriVideo.isEmpty()) {
            Uri uri = Uri.parse(uriVideo);
            videoView.setVideoURI(uri);
        } else {

        }

                //Botones de reproducción.
        videoPlay = (Button) findViewById(R.id.videoPlay);
        videoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reproducir();
                videoView.setMediaController(new MediaController(VerVideoActivity.this));
                videoView.start();
                videoView.requestFocus();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_verfoto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editar:

                return true;
            case R.id.borrar:
                new AlertDialog.Builder(VerVideoActivity.this)
                        .setTitle("¿Estás seguro que quieres eliminar este problema?")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("problemas").document(adjuntoProblema.getProblemaId()).collection("videos").document(_id).delete();

                                String _id = VideoActivity.adaptador.getKey(pos);
                                RVProblemas.adjuntoProblemas.borrar(_id);

                                imagenRef = RVProblemas.getStorageReference().child(adjuntoProblema.getProblemaId() + "/videos/" + adjuntoProblema.getIdTiempo() + ".mp4");

                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(adjuntoProblema.getUrl());
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(VerVideoActivity.this, "Video borrado satisfactoriamente.", Toast.LENGTH_SHORT).show();
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                });
                                VideoActivity.adaptador.notifyDataSetChanged();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
