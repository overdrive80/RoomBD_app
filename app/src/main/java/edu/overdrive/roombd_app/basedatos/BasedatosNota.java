package edu.overdrive.roombd_app.basedatos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.overdrive.roombd_app.Constantes;
import edu.overdrive.roombd_app.conversores.ConversorRoom;
import edu.overdrive.roombd_app.dao.NotaDAO;
import edu.overdrive.roombd_app.entidades.Nota;

@Database(
        entities = {Nota.class},
        version = 1,
        exportSchema = false)
@TypeConverters({ConversorRoom.class})
public abstract class BasedatosNota extends RoomDatabase {

    public abstract NotaDAO notaDao();

    //Implementar un patrón Singleton
    private static volatile BasedatosNota INSTANCIA;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService servicioExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Podemos añadir synchronized para que solo se pueda crear una instancia de la BD
    public static BasedatosNota getInstance(final Context context) {

        if (INSTANCIA == null) {
            synchronized (BasedatosNota.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = crearInstancia(context);
                }
            }
        }

        return INSTANCIA;

    }

    private static BasedatosNota crearInstancia(Context context) {
        return Room.databaseBuilder(context, BasedatosNota.class, Constantes.BBDD).
                allowMainThreadQueries().build();
    }

    public static void anularInstancia() {
        INSTANCIA = null;

    }
}
