package intentservice.example.com.pruebagaribd.Bd;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.Usuario;

public class UsuariosFirestore {

    private CollectionReference usuarios;

    interface EscuchadorElemento{
        void onRespuesta(Usuario usuario);
    }

    interface EscuchadorTamanyo{
        void onRespuesta(long tamanyo);
    }

    public UsuariosFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarios = db.collection("usuarios");
    }

    public void guardarUsuario(final FirebaseUser user){
        Usuario usuario=new Usuario();
        usuario.setCorreo(user.getEmail());
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid()).set(usuario);
    }

    public void elemento(String id, final EscuchadorElemento escuchador) {
        usuarios.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Usuario usuario = task.getResult().toObject(Usuario.class);
                    escuchador.onRespuesta(usuario);
                } else {
                    escuchador.onRespuesta(null);
                }
            }
        });
    }

    public void actualiza(String id, Usuario usuario) {
        usuarios.document(id).set(usuario);
    }

    public void tamanyo(final EscuchadorTamanyo escuchador) {
        usuarios.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    escuchador.onRespuesta(task.getResult().size());
                } else {
                    escuchador.onRespuesta(-1);
                }
            }
        });
    }


}
