package intentservice.example.com.pruebagaribd.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import intentservice.example.com.pruebagaribd.Adaptadores.AdaptadorFirestoreUI;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;
import intentservice.example.com.pruebagaribd.Usuario;

public class CreadorActivity extends AppCompatActivity {

    TextView editIdUsuario;
    TextView editNombreUsuario, editCorreo, editNTelefono, editDelegacion;
    private CollectionReference usuarios;
    Usuario usuario;
    FirebaseFirestore db;
    private RecyclerView recyclerView;
    public static AdaptadorFirestoreUI adaptador;
    Query query;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creador);

        //Ver otros problemas que ha abierto el creador, ordenador por tipo.
        //Señalar en azul para decir que se puede llamar o mandar mail.


        Bundle extras = getIntent().getExtras();
        String idCreador = extras.getString("idCreador");

        editNombreUsuario = (TextView) findViewById(R.id.editNombreUsuario);
        editCorreo = (TextView) findViewById(R.id.editCorreo);
        editNTelefono = (TextView) findViewById(R.id.editNTelefono);
        editDelegacion = (TextView) findViewById(R.id.editDelegacion);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("usuarios").document(idCreador);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Usuario usuario = task.getResult().toObject(Usuario.class);
                    editCorreo.setText(usuario.getCorreo());
                    if (usuario.getNombre() != null) editNombreUsuario.setText(usuario.getNombre());
                    editNTelefono.setText(String.valueOf(usuario.getnTelefono()));
                    if (usuario.getDelegacion() != null)
                        editDelegacion.setText(usuario.getDelegacion());
                }
            }
        });

        editCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{editCorreo.getText().toString()});
                startActivity(i);
            }
        });

        editNTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + editNTelefono.getText().toString())));
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        query = FirebaseFirestore.getInstance().collection("problemas").limit(50);
        query = query.whereEqualTo("creador", idCreador);
        FirestoreRecyclerOptions<Problema> opciones = new FirestoreRecyclerOptions.Builder<Problema>().setQuery(query, Problema.class).build();
        adaptador = new AdaptadorFirestoreUI(opciones);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Intent i = new Intent(CreadorActivity.this, VistaProblema.class);
                i.putExtra("posicion", pos);
                i.putExtra("activityCreador",true);
                startActivity(i);
            }
        });
        adaptador.startListening();
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }


}
