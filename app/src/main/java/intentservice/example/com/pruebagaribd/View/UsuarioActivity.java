package intentservice.example.com.pruebagaribd.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;
import intentservice.example.com.pruebagaribd.TipoProblema;
import intentservice.example.com.pruebagaribd.Usuario;

public class UsuarioActivity extends AppCompatActivity {

    TextView editIdUsuario;
    EditText editNombreUsuario, editCorreo, editNTelefono, editDelegacion;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        editIdUsuario = (TextView) findViewById(R.id.editIdUsuario);
        editNombreUsuario = (EditText) findViewById(R.id.editNombreUsuario);
        editNTelefono = (EditText) findViewById(R.id.editNTelefono);
        editDelegacion = (EditText) findViewById(R.id.editDelegacion);


        editIdUsuario.setText(MainActivity._idUsuario);
        //Coger POJO de Firestore. Me serviría para ver la información del creador
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("usuarios").document(MainActivity._idUsuario);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Usuario usuario = task.getResult().toObject(Usuario.class);
                    if (usuario.getNombre() != null) editNombreUsuario.setText(usuario.getNombre());
                    editNTelefono.setText(String.valueOf(usuario.getnTelefono()));
                    if (usuario.getDelegacion() != null)
                        editDelegacion.setText(usuario.getDelegacion());
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_problema, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guardar:
                FirebaseUser usuarioAuth = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                        .setDisplayName(editNombreUsuario.getText().toString())
                        .build();
                usuarioAuth.updateProfile(perfil);

                //Coger POJO de Firestore. Me serviría para ver la información del creador
                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("usuarios").document(MainActivity._idUsuario);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            Usuario usuario = task.getResult().toObject(Usuario.class);
                            usuario.setNombre(editNombreUsuario.getText().toString());
                            usuario.setnTelefono(Long.parseLong(editNTelefono.getText().toString()));
                            usuario.setDelegacion(editDelegacion.getText().toString());
                            db.collection("usuarios").document(MainActivity._idUsuario).set(usuario);
                        }
                    }
                });

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
