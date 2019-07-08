package intentservice.example.com.pruebagaribd.Adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import intentservice.example.com.pruebagaribd.Bd.Problemas;
import intentservice.example.com.pruebagaribd.Bd.ProblemasBD;
import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;

public class MiAdaptador extends RecyclerView.Adapter<MiAdaptador.ViewHolder> {

    private LayoutInflater inflador;
    protected Cursor cursor;
    protected View.OnClickListener onClickListener;

    public MiAdaptador(Context contexto, Problemas problemas, Cursor cursor) {
        super();
        this.cursor = cursor;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MiAdaptador.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflador.inflate(R.layout.elemento_lista, viewGroup, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public Problema problemaPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        return ProblemasBD.extraeProblema(cursor);
    }

    public int idPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        return cursor.getInt(0);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Problema problema = problemaPosicion(i);
        viewHolder.tipo.setText(problema.getTipo().getTexto());
        viewHolder.nombre.setText(problema.getNombre());
        viewHolder.descripcion.setText(problema.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tipo, nombre, descripcion;

        public ViewHolder(View itemView) {
            super(itemView);
            tipo = (TextView) itemView.findViewById(R.id.tipo);
            nombre = (TextView) itemView.findViewById(R.id.nombre);
            descripcion = (TextView) itemView.findViewById(R.id.descripcion);
        }
    }


}
