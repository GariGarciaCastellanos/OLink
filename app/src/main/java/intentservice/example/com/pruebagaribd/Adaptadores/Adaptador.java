package intentservice.example.com.pruebagaribd.Adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import intentservice.example.com.pruebagaribd.Bd.Problemas;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder> {

    protected Problemas problemas;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;

    public Adaptador(Context contexto, Problemas problemas){
        this.contexto=contexto;
        this.problemas=problemas;
        inflador=(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tipo, nombre, descripcion;

        public ViewHolder(View itemView) {
            super(itemView);
            tipo = (TextView) itemView.findViewById(R.id.tipo);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            descripcion = (TextView) itemView.findViewById(R.id.descripcion);
        }
    }

    @Override
    public Adaptador.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflador.inflate(R.layout.elemento_lista, viewGroup, false);
        v.setOnClickListener(onClickListener);
        return new Adaptador.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Adaptador.ViewHolder viewHolder, int i) {
        Problema problema = problemas.elemento(i);
        viewHolder.tipo.setText(problema.getTipo().getTexto());
        viewHolder.nombre.setText(problema.getNombre());
        viewHolder.descripcion.setText(problema.getDescripcion());
    }

    @Override public int getItemCount() {
        return problemas.tamanyo();
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
