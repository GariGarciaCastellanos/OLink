package intentservice.example.com.pruebagaribd.View;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;
import intentservice.example.com.pruebagaribd.TipoProblema;
import intentservice.example.com.pruebagaribd.Usuario;

public class EditarProblemaActivity extends AppCompatActivity {

    TextView editIdProblema, editLatitud, editLongitud, editFoto, editGrabacionAceleracion, editAudio, editVideo, editCreador;
    EditText editNombre, editDescripcion, editCodigoAscensor, editImportancia;
    Spinner spinnerTipo;
    Button fotoVer, activityAudio, activityVideo;
    public Problema problema = new Problema();
    public int pos;
    public String id, _id;

    private double latitud;
    private double longitud;
    boolean problemaNuevo;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_problema);
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        problemaNuevo = extras.getBoolean("problemaNuevo", false);


        editIdProblema = (TextView) findViewById(R.id.editIdProblema);
        spinnerTipo = (Spinner) findViewById(R.id.spinnerTipo);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TipoProblema.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adaptador);
        editNombre = (EditText) findViewById(R.id.editNombre);
        editDescripcion = (EditText) findViewById(R.id.editDescripcion);
        editCreador = (TextView) findViewById(R.id.editCreador);
        editCodigoAscensor = (EditText) findViewById(R.id.editCodigoAscensor);
        editLatitud = (TextView) findViewById(R.id.editLatitud);
        editLongitud = (TextView) findViewById(R.id.editLongitud);
        editImportancia = (EditText) findViewById(R.id.editImportancia);
        editFoto = (TextView) findViewById(R.id.editFoto);
        editGrabacionAceleracion = (TextView) findViewById(R.id.editGrabacionAceleracion);
        editAudio = (TextView) findViewById(R.id.editAudio);
        editVideo = (TextView) findViewById(R.id.editVideo);

        if (problemaNuevo) {
            latitud = extras.getDouble("latitud");
            longitud = extras.getDouble("longitud");
            problema.setLatitud(latitud);
            problema.setLongitud(longitud);
            editLatitud.setText(String.valueOf(problema.getLatitud()));
            editLongitud.setText(String.valueOf(problema.getLongitud()));
            editImportancia.setText(String.valueOf(1));

        } else {
            problema = RVProblemas.adaptador.getItem(pos);
            _id = RVProblemas.adaptador.getKey(pos);
            spinnerTipo.setSelection(problema.getTipo().ordinal());
            editNombre.setText(problema.getNombre());
            editDescripcion.setText(problema.getDescripcion());
            editCodigoAscensor.setText(problema.getCodigoAscensor());
            editLatitud.setText(String.valueOf(problema.getLatitud()));
            editLongitud.setText(String.valueOf(problema.getLongitud()));
            editImportancia.setText(String.valueOf(problema.getImportancia()));
        }

        //Coger POJO de Firestore. Me serviría para ver la información del creador
        db = FirebaseFirestore.getInstance();
        if (problema.getCreador() != null) {
            DocumentReference docRef = db.collection("usuarios").document(problema.getCreador());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Usuario usuario = task.getResult().toObject(Usuario.class);
                        editCreador.setText(usuario.getNombre());
                    }
                }
            });
        }


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
                if (problemaNuevo) {
                    _id = RVProblemas.problemas.nuevo();
                    problema.setCreador(MainActivity._idUsuario);


                } else {
                    _id = RVProblemas.adaptador.getKey(pos);
                }
                problema.setTipo(TipoProblema.values()[spinnerTipo.getSelectedItemPosition()]);
                problema.setNombre(editNombre.getText().toString());
                problema.setDescripcion(editDescripcion.getText().toString());
                problema.setCodigoAscensor(editCodigoAscensor.getText().toString());
                problema.setImportancia(Float.valueOf(editImportancia.getText().toString()));
                problema.setLatitud(Double.valueOf(editLatitud.getText().toString()));
                problema.setLongitud(Double.valueOf(editLongitud.getText().toString()));

                RVProblemas.problemas.actualiza(_id, problema);
                RVProblemas.adaptador.notifyDataSetChanged();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
