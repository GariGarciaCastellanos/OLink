package intentservice.example.com.pruebagaribd.Adaptadores;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.R;


public class AdaptadorAdjuntoFirestoreUI extends FirestoreRecyclerAdapter<AdjuntoProblema,AdaptadorAdjunto.ViewHolder> {

    protected View.OnClickListener onClickListener;

    public AdaptadorAdjuntoFirestoreUI(@NonNull FirestoreRecyclerOptions<AdjuntoProblema> options){
        super(options);
    }

    @Override
    public AdaptadorAdjunto.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elementoadjunto,parent,false);
        return new AdaptadorAdjunto.ViewHolder(view);
    }

    @Override protected void onBindViewHolder(@NonNull AdaptadorAdjunto.ViewHolder viewHolder, int position, @NonNull AdjuntoProblema adjuntoProblema){
        if(adjuntoProblema.getNombre()==null) {
            viewHolder.idTextView.setText("ID Foto: ");
            viewHolder.idAdjuntoProblema.setText(adjuntoProblema.getIdTiempo().toString());
        }else{
            viewHolder.idTextView.setText("Nombre: ");
            viewHolder.idAdjuntoProblema.setText(adjuntoProblema.getNombre());
        }

        viewHolder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick){
        onClickListener=onClick;
    }

    public String getKey(int pos){
        return super.getSnapshots().getSnapshot(pos).getId();
    }



}
