package intentservice.example.com.pruebagaribd.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import intentservice.example.com.pruebagaribd.Adaptadores.AdaptadorAdjuntoFirestoreUI;
import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemaFirestore;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasAsinc;
import intentservice.example.com.pruebagaribd.Bd.ProblemasFirestore;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;

public class FotoActivity extends AppCompatActivity {

    private Button sacarFoto;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Problema problema = new Problema();
    public AdjuntoProblema adjuntoProblema = new AdjuntoProblema();
    public int pos;
    long codFecha;
    String _id, _idAdjunto;
    Query query;
    String nombreAdjuntoProblema;

    static UploadTask uploadTask = null;
    StorageReference imagenRef;

    private RecyclerView recyclerView;
    public static AdaptadorAdjuntoFirestoreUI adaptador;
    public static AdjuntoProblemasAsinc adjuntoProblemas;
    public static ProblemasAsinc problemas;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        _id = RVProblemas.adaptador.getKey(pos);
        problema = RVProblemas.adaptador.getItem(pos);
        codFecha = problema.getFecha();

        problemas = new ProblemasFirestore();
        adjuntoProblemas = new AdjuntoProblemaFirestore();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        query = FirebaseFirestore.getInstance().collection("problemas").document(_id).collection("fotos").limit(50);
        FirestoreRecyclerOptions<AdjuntoProblema> opciones = new FirestoreRecyclerOptions.Builder<AdjuntoProblema>().setQuery(query, AdjuntoProblema.class).build();
        adaptador = new AdaptadorAdjuntoFirestoreUI(opciones);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Intent intentFoto = new Intent(FotoActivity.this, VerFotoActivity.class);
                intentFoto.putExtra("posicion", pos);
                startActivity(intentFoto);
            }
        });
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        sacarFoto = (Button) findViewById(R.id.sacarFoto);
        sacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText entrada = new EditText(FotoActivity.this);
                new AlertDialog.Builder(FotoActivity.this)
                        .setTitle("SELECCIÓN DE NOMBRE:")
                        .setMessage("indica el nombre del adjunto:")
                        .setView(entrada)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                nombreAdjuntoProblema = entrada.getText().toString();
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadFile(imageBitmap);
        }
    }

    private void uploadFile(Bitmap bitmap) {
        final ProgressDialog progresoSubida = new ProgressDialog(this);
        progresoSubida.setTitle("Subiendo...");
        progresoSubida.setMessage("Espere...");
        progresoSubida.setCancelable(true);
        progresoSubida.setCanceledOnTouchOutside(false);

        imagenRef = RVProblemas.getStorageReference().child(String.valueOf(codFecha) + "/images/" + adjuntoProblema.getIdTiempo() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        uploadTask = imagenRef.putBytes(data);
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
                imagenRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // getting image uri and converting into string
                        Uri downloadUrl = uri;

                        _idAdjunto = RVProblemas.adjuntoProblemas.nuevo();
                        adjuntoProblema.setUrl(downloadUrl.toString());
                        adjuntoProblema.setProblemaId(_id);
                        adjuntoProblema.setNombre(nombreAdjuntoProblema);
                        adjuntoProblema.setRefArchivo(imagenRef.getPath());
                        adjuntoProblema.setTipo("FOTO");

                        RVProblemas.adjuntoProblemas.actualiza(_idAdjunto, adjuntoProblema);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("problemas").document(_id).collection("fotos").document(_idAdjunto).set(adjuntoProblema);
                        adaptador.notifyDataSetChanged();

                        progresoSubida.dismiss();
                    }
                });
            }
        });
    }
}
