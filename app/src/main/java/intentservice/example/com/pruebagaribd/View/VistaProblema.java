package intentservice.example.com.pruebagaribd.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import intentservice.example.com.pruebagaribd.Problema;
import intentservice.example.com.pruebagaribd.R;
import intentservice.example.com.pruebagaribd.Usuario;

public class VistaProblema extends AppCompatActivity {

    TextView editIdProblema, editTipo, editNombre, editDescripcion, editCodigoAscensor,
            editLatitud, editLongitud, editImportancia, editFoto, editGrabacionAceleracion, editAudio, editVideo, editCreador;
    public Problema problema = new Problema();
    Integer pos;
    Boolean activityCreador=false;
    Button verActivityFoto, verActivityAudio, verActivityVideo, verActivityVibraciones;

    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_FOTO = 2;
    final static int RESULTADO_AUDIO = 3;
    final static int RESULTADO_VIDEO = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_problema);
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("posicion");
        activityCreador=extras.getBoolean("activityCreador",false);


        verActivityFoto = (Button) findViewById(R.id.verActivityFoto);
        verActivityAudio = (Button) findViewById(R.id.verActivityAudio);
        verActivityVideo = (Button) findViewById(R.id.verActivityVideo);
        verActivityVibraciones = (Button) findViewById(R.id.verActivityVibraciones);

        verActivityFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaProblema.this, FotoActivity.class);
                i.putExtra("posicion", pos);
                startActivity(i);
            }
        });
        verActivityAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaProblema.this, AudioActivity.class);
                i.putExtra("posicion", pos);
                startActivity(i);
            }
        });
        verActivityVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaProblema.this, VideoActivity.class);
                i.putExtra("posicion", pos);
                startActivity(i);
            }
        });
        verActivityVibraciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaProblema.this, VibracionesActivity.class);
                i.putExtra("posicion", pos);
                startActivity(i);
            }
        });

        actualizarVistas(pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_problema, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editar:
                Intent i = new Intent(VistaProblema.this, EditarProblemaActivity.class);
                i.putExtra("posicion", pos);
                startActivityForResult(i, RESULTADO_EDITAR);
                return true;
            case R.id.borrar:
                new AlertDialog.Builder(VistaProblema.this)
                        .setTitle("¿Estás seguro que quieres eliminar este problema?")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String _id = RVProblemas.adaptador.getKey(pos);
                                RVProblemas.problemas.borrar(_id);
                                RVProblemas.adaptador.notifyDataSetChanged();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            case R.id.buscar:
                return true;
            case R.id.enviar:
                Intent i1 = new Intent(Intent.ACTION_SEND);
                i1.setType("message/rfc822");
                i1.putExtra(Intent.EXTRA_EMAIL, new String[]{"technicalsupport@prueba.com"});
                i1.putExtra(Intent.EXTRA_SUBJECT, "Problema:" + problema.getNombre());
                i1.putExtra(Intent.EXTRA_TEXT, problema.toString());
                startActivity(i1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas(pos);
        }
        if (requestCode == RESULTADO_FOTO) {
            actualizarVistas(pos);
        }
        if (requestCode == RESULTADO_AUDIO) {
            actualizarVistas(pos);
        }
        if (requestCode == RESULTADO_VIDEO) {
            actualizarVistas(pos);
        }
    }

    public void actualizarVistas(final int pos) {
        this.pos = pos;
        if(activityCreador==true){
            problema=CreadorActivity.adaptador.getItem(pos);
        }else {
            problema = RVProblemas.adaptador.getItem(pos);
        }
        if (problema != null) {
            editIdProblema = (TextView) findViewById(R.id.editIdProblema);
            editTipo = (TextView) findViewById(R.id.editTipo);
            editNombre = (TextView) findViewById(R.id.editNombre);
            editDescripcion = (TextView) findViewById(R.id.editDescripcion);
            editCreador = (TextView) findViewById(R.id.editCreador);
            editCodigoAscensor = (TextView) findViewById(R.id.editCodigoAscensor);
            editLatitud = (TextView) findViewById(R.id.editLatitud);
            editLongitud = (TextView) findViewById(R.id.editLongitud);
            editImportancia = (TextView) findViewById(R.id.editImportancia);
            editFoto = (TextView) findViewById(R.id.editFoto);
            editGrabacionAceleracion = (TextView) findViewById(R.id.editGrabacionAceleracion);
            editAudio = (TextView) findViewById(R.id.editAudio);
            editVideo = (TextView) findViewById(R.id.editVideo);


            editTipo.setText(problema.getTipo().getTexto());
            editNombre.setText(problema.getNombre());
            editDescripcion.setText(problema.getDescripcion());
            editCodigoAscensor.setText(problema.getCodigoAscensor());
            editLatitud.setText(String.valueOf(problema.getLatitud()));
            editLongitud.setText(String.valueOf(problema.getLongitud()));
            editImportancia.setText(String.valueOf(problema.getImportancia()));

            //Coger POJO de Firestore. Me serviría para ver la información del creador
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (problema.getCreador()!=null) {
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
                editCreador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(VistaProblema.this, CreadorActivity.class);
                        i.putExtra("idCreador",problema.getCreador());
                        startActivity(i);
                    }
                });
            }
            String _id = RVProblemas.adaptador.getKey(pos);


            DocumentReference DocCollectionFotos = db.collection("problemas").document(_id);
            //Poner el total de adjuntos fotos
            DocCollectionFotos.collection("fotos").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                Integer countFotos = task.getResult().size();
                                editFoto.setText(countFotos.toString());
                            } else {
                                editFoto.setText("0");
                            }
                        }
                    });


            //Poner el total de adjuntos videos
            DocCollectionFotos.collection("videos").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Integer countVideos = task.getResult().size();
                                editVideo.setText(countVideos.toString());
                            } else {
                                editVideo.setText("0");
                            }
                        }
                    });


            //Poner el total de adjuntos audios
            DocCollectionFotos.collection("audios").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Integer countAudios = task.getResult().size();
                                editAudio.setText(countAudios.toString());
                            } else {
                                editAudio.setText("0");
                            }
                        }
                    });

            //Poner el total de adjuntos vibraciones
            DocCollectionFotos.collection("grabaciones").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Integer countGrabacion = task.getResult().size();
                                editGrabacionAceleracion.setText(countGrabacion.toString());
                            } else {
                                editGrabacionAceleracion.setText("0");
                            }
                        }
                    });


        }
    }
}
