package intentservice.example.com.pruebagaribd.Adaptadores;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;

public class AdaptadorFirestoreUI extends FirestoreRecyclerAdapter<Problema,Adaptador.ViewHolder> {

    protected View.OnClickListener onClickListener;

    public AdaptadorFirestoreUI(@NonNull FirestoreRecyclerOptions<Problema> options){
        super(options);
    }

    @Override
    public Adaptador.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista,parent,false);
        return new Adaptador.ViewHolder(view);
    }





    @Override protected void onBindViewHolder(@NonNull Adaptador.ViewHolder viewHolder, int position, @NonNull Problema problema){
        viewHolder.tipo.setText(problema.getTipo().getTexto());
        viewHolder.nombre.setText(problema.getNombre());
        viewHolder.descripcion.setText(problema.getDescripcion());
        viewHolder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick){
        onClickListener=onClick;
    }

    public String getKey(int pos){
        return super.getSnapshots().getSnapshot(pos).getId();
    }

}
