package intentservice.example.com.pruebagaribd.Bd;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.Problema;

public class AdjuntoProblemaFirestore implements AdjuntoProblemasAsinc {

    private CollectionReference adjuntoProblemas;

    public AdjuntoProblemaFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        adjuntoProblemas = db.collection("adjuntoProblemas");
    }

    @Override
    public void elemento(String id, final ProblemasAsinc.EscuchadorElemento escuchador) {
        adjuntoProblemas.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
    public void anyade(AdjuntoProblema adjuntoProblema) {
        adjuntoProblemas.add(adjuntoProblema);
    }

    @Override
    public String nuevo() {
        return adjuntoProblemas.document().getId();
    }

    @Override
    public void borrar(String id) {
        adjuntoProblemas.document(id).delete();
    }

    @Override
    public void actualiza(String id, AdjuntoProblema adjuntoProblema) {
        adjuntoProblemas.document(id).set(adjuntoProblema);
    }

    @Override
    public void tamanyo(final ProblemasAsinc.EscuchadorTamanyo escuchador) {
        adjuntoProblemas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
