package edu.overdrive.roombd_app;

import static edu.overdrive.roombd_app.Constantes.*;

import android.app.ComponentCaller;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.overdrive.roombd_app.adaptadores.AdaptadorNotas;
import edu.overdrive.roombd_app.basedatos.BasedatosNota;
import edu.overdrive.roombd_app.entidades.Nota;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvTitulo;
    private RecyclerView recyclerView;
    private AdaptadorNotas adaptadorNotas;
    private BasedatosNota basedatosNota;
    private List<Nota> listaNotas = new ArrayList<>();
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVistas();
        configurarLista();
    }

    private void configurarLista() {

        basedatosNota = BasedatosNota.getInstance(this);

        //Poblar de datos la lista
        BasedatosNota.servicioExecutor.execute(() -> {
            List<Nota> notasFromDB = basedatosNota.notaDao().getTodas();

            // Ahora regresamos al hilo principal para tocar la UI
            runOnUiThread(() -> {
                // Usar ESTA lista, no crear una nueva
                listaNotas.clear();
                listaNotas.addAll(notasFromDB);


                AdaptadorNotas.OnNotaClickListener listener = new AdaptadorNotas.OnNotaClickListener() {
                    @Override
                    public void onNotaClick(int position) {

                        MainActivity.this.posicion = position;

                        //AlertDialog
                        new AlertDialog.Builder(MainActivity.this)
                                .setItems(new String[]{"Borrar", "Modificar"}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            //Borrar
                                            Nota notaBorrar = listaNotas.get(posicion);
                                            basedatosNota.notaDao().borrarTodas(notaBorrar);

                                            listaNotas.remove(posicion);
                                            adaptadorNotas.notifyItemRemoved(posicion);
                                            adaptadorNotas.notifyItemRangeChanged(posicion, listaNotas.size());

                                        } else if (i == 1){
                                            //Actualizar
                                            Intent intent = new Intent(MainActivity.this, InsertarNotaActivity.class);
                                            intent.putExtra(NOTA, listaNotas.get(posicion));
                                            startActivityForResult(intent, REQUEST_CODE_INSERTAR);
                                        }
                                    }
                                }).show();

                    }
                };

                adaptadorNotas = new AdaptadorNotas(listaNotas, this, listener);
                adaptadorNotas.notifyDataSetChanged();
                recyclerView.setAdapter(adaptadorNotas);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            });
        });
    }

    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.tv_titulo);
        recyclerView = findViewById(R.id.recyclerView);

        //Barra de navegación
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Boton flotante
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Toast.makeText(this, "Boton flotante pulsado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, InsertarNotaActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INSERTAR);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode > 0){
            Nota nota = (Nota) data.getSerializableExtra(NOTA);

            if (nota == null){return;}

            //Nueva nota
            if (resultCode == 1){
                listaNotas.add(nota);
                adaptadorNotas.notifyItemInserted(listaNotas.size());
                recyclerView.smoothScrollToPosition(listaNotas.size());

            } else if (resultCode == RESULT_UPDATED){
                //Actualizar nota
                listaNotas.set(posicion, nota);
                adaptadorNotas.notifyItemChanged(posicion);

            }

        }

    }
}