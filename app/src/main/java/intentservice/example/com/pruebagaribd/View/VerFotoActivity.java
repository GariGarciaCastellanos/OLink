package intentservice.example.com.pruebagaribd.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.R;

public class VerFotoActivity extends AppCompatActivity {

    ImageView imagenFoto;
    private String uriFoto;
    public AdjuntoProblema adjuntoProblema = new AdjuntoProblema();
    public int pos;
    long codFecha;
    String _id;
    StorageReference imagenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfoto);
        imagenFoto = (ImageView) findViewById(R.id.idImagenFoto);

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        _id = FotoActivity.adaptador.getKey(pos);
        adjuntoProblema = FotoActivity.adaptador.getItem(pos);
        codFecha = adjuntoProblema.getIdTiempo();

        uriFoto = adjuntoProblema.getUrl();
        if (!uriFoto.isEmpty()) {
            Glide.with(this)
                    .load(uriFoto)
                    .into(imagenFoto);
        } else {
            imagenFoto.setImageBitmap(null);
        }
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
                new AlertDialog.Builder(VerFotoActivity.this)
                        .setTitle("¿Estás seguro que quieres eliminar este problema?")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String ProblemaId = adjuntoProblema.getProblemaId();
                                db.collection("problemas").document(adjuntoProblema.getProblemaId()).collection("fotos").document(_id).delete();

                                String _id = FotoActivity.adaptador.getKey(pos);
                                RVProblemas.adjuntoProblemas.borrar(_id);

                                imagenRef = RVProblemas.getStorageReference().child(adjuntoProblema.getProblemaId() + "/images/" + adjuntoProblema.getIdTiempo() + ".jpg");

                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(adjuntoProblema.getUrl());
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(VerFotoActivity.this, "Foto borrada satisfactoriamente.", Toast.LENGTH_SHORT).show();
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                });
                                FotoActivity.adaptador.notifyDataSetChanged();
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
