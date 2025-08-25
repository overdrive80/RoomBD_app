package edu.overdrive.roombd_app.conversores;

import androidx.room.TypeConverter;

import java.util.Date;

public class ConversorRoom {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);  //Primitivo a complejo
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();            //Complejo a primitivo
    }
}
