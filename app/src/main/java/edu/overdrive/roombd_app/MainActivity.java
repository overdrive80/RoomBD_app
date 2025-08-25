package edu.overdrive.roombd_app;

import static edu.overdrive.roombd_app.Constantes.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
                // Usar ESTA lista, no crear una nueva /* CUARENTENA */
                listaNotas.clear();
                listaNotas.addAll(notasFromDB);


                AdaptadorNotas.OnNotaClickListener listener = new AdaptadorNotas.OnNotaClickListener() {
                    @Override
                    public void onNotaClick(int position) {

                        MainActivity.this.posicion = position;

                        //AlertDialog
                        new AlertDialog.Builder(MainActivity.this).setItems(new String[]{getString(R.string.borrar), getString(R.string.modificar)}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Nota notaSeleccion = listaNotas.get(posicion);

                                if (i == 0) {
                                    //Borrar
                                    borrarNota(notaSeleccion);
                                } else if (i == 1) {
                                    //Actualizar
                                    actualizarNota(notaSeleccion);
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

    private void actualizarNota(Nota notaSeleccion) {
        Intent intent = new Intent(MainActivity.this, InsertarNotaActivity.class);
        intent.putExtra(NOTA, notaSeleccion);
        startActivityForResult(intent, REQUEST_CODE_INSERTAR);
    }

    private void borrarNota(Nota notaBorrar) {
        basedatosNota.notaDao().borrarTodas(notaBorrar);

        listaNotas.remove(posicion);
        adaptadorNotas.notifyItemRemoved(posicion);
        adaptadorNotas.notifyItemRangeChanged(posicion, listaNotas.size());
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
            Intent intent = new Intent(this, InsertarNotaActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INSERTAR);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode > 0) {
            Nota nota = (Nota) data.getSerializableExtra(NOTA);

            if (nota == null) {
                return;
            }

            //Nueva nota
            if (resultCode == 1) {
                listaNotas.add(nota);
                adaptadorNotas.notifyItemInserted(listaNotas.size());
                recyclerView.smoothScrollToPosition(listaNotas.size());

            } else if (resultCode == RESULT_UPDATED) {
                //Actualizar nota
                listaNotas.set(posicion, nota);
                adaptadorNotas.notifyItemChanged(posicion);
            }
        }
    }
}