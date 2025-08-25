package edu.overdrive.roombd_app.dao;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.MapInfo;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

import edu.overdrive.roombd_app.Constantes;
import edu.overdrive.roombd_app.entidades.Nota;

@Dao
public interface NotaDAO {

    //Consultas
    @Query("SELECT * FROM " + Constantes.TABLA_NOTAS + " ORDER BY titulo ASC")
    public List<Nota> getTodas();

    @Query("SELECT * FROM " + Constantes.TABLA_NOTAS + " WHERE id_nota = :id_solicitado")
    public Nota getNota(int id_solicitado);

    //Insercción
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertarTodas(Nota ... notas);

    //Actualización
    @Update
    int actualizarTodas(Nota ... notas);

    //Borrado
    @Delete
    int borrarTodas(Nota ... notas);

}
