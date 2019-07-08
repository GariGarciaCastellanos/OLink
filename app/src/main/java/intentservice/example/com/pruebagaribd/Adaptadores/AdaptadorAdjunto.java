package intentservice.example.com.pruebagaribd.Adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import intentservice.example.com.pruebagaribd.AdjuntoProblema;
import intentservice.example.com.pruebagaribd.Bd.AdjuntoProblemas;
import intentservice.example.com.pruebagaribd.R;

public class AdaptadorAdjunto extends RecyclerView.Adapter<AdaptadorAdjunto.ViewHolder>  {

    protected AdjuntoProblemas adjuntoProblemas;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;

    public AdaptadorAdjunto(Context contexto, AdjuntoProblemas adjuntoProblemas){
        this.contexto=contexto;
        this.adjuntoProblemas=adjuntoProblemas;
        inflador=(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView idTextView, idAdjuntoProblema;

        public ViewHolder(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.idTextView);
            idAdjuntoProblema = (TextView) itemView.findViewById(R.id.idAdjuntoProblema);
        }
    }

    @Override
    public AdaptadorAdjunto.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflador.inflate(R.layout.elementoadjunto, viewGroup, false);
        v.setOnClickListener(onClickListener);
        return new AdaptadorAdjunto.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptadorAdjunto.ViewHolder viewHolder, int i) {
        AdjuntoProblema adjuntoProblema = adjuntoProblemas.elemento(i);
        if(adjuntoProblema.getNombre()=="") {
            viewHolder.idTextView.setText("ID Foto: ");
            viewHolder.idAdjuntoProblema.setText(adjuntoProblema.getIdTiempo().toString());
        }else{
            viewHolder.idTextView.setText("Nombre: ");
            viewHolder.idAdjuntoProblema.setText(adjuntoProblema.getNombre());
        }
    }

    @Override public int getItemCount() {
        return adjuntoProblemas.tamanyo();
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }



}
