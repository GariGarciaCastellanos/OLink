package intentservice.example.com.pruebagaribd.Bd;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import intentservice.example.com.pruebagaribd.Problema;

public class ProblemasFirestore implements ProblemasAsinc {
    private CollectionReference problemas;

    public ProblemasFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        problemas = db.collection("problemas");
    }

    @Override
    public void elemento(String id, final EscuchadorElemento escuchador) {
        problemas.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Problema problema = task.getResult().toObject(Problema.class);
                    escuchador.onRespuesta(problema);
                } else {
                    escuchador.onRespuesta(null);
                }
            }
        });
    }

    @Override
    public void anyade(Problema problema) {
        problemas.add(problema);
    }

    @Override
    public String nuevo() {
        return problemas.document().getId();
    }

    @Override
    public void borrar(String id) {
        problemas.document(id).delete();
    }

    @Override
    public void actualiza(String id, Problema problema) {
        problemas.document(id).set(problema);
    }

    @Override
    public void tamanyo(final EscuchadorTamanyo escuchador) {
        problemas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
