package edu.overdrive.roombd_app;

import static edu.overdrive.roombd_app.Constantes.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.overdrive.roombd_app.basedatos.BasedatosNota;
import edu.overdrive.roombd_app.entidades.Nota;

public class InsertarNotaActivity extends AppCompatActivity {

    private TextView et_titulo, et_contenido;
    private Button btn_Guardar;
    private BasedatosNota basedatosNota;
    private Nota nota;
    private boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_nota);

        et_titulo = findViewById(R.id.et_titulo);
        et_contenido = findViewById(R.id.et_contenido);
        btn_Guardar = findViewById(R.id.btn_guardar);

        basedatosNota = BasedatosNota.getInstance(this);

        nota = (Nota) getIntent().getSerializableExtra(NOTA);

        if (nota != null) {
            //getSupportActionBar().setTitle("Editar Nota");
            update = true;
            btn_Guardar.setText("Actualizar");

            et_titulo.setText(nota.getTitulo());
            et_contenido.setText(nota.getContenido());
        }

        btn_Guardar.setOnClickListener(view -> {
            String titulo = et_titulo.getText().toString();
            String contenido = et_contenido.getText().toString();

            if (update) {
                nota.setTitulo(titulo);
                nota.setContenido(contenido);
                BasedatosNota.servicioExecutor.execute(() -> {
                    basedatosNota.notaDao().actualizarTodas(nota);

                    //Volver a la actividad anterior
                    runOnUiThread(() -> {
                        setResult(nota, RESULT_UPDATED);
                    });
                });

            } else {
                nota = new Nota(titulo, contenido);
                BasedatosNota.servicioExecutor.execute(() -> {
                    long[] ids = basedatosNota.notaDao().insertarTodas(nota);

                    //Volver a la actividad anterior
                    runOnUiThread(() -> {
                        setResult(nota, RESULT_CREATED);
                    });
                });
            }
        });
    }

    private void setResult(Nota nota, int flag) {
        setResult(flag, new Intent().putExtra(NOTA, nota));
        finish();


    }
}