package m.i.d.mid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jooyoung on 2016-12-01.
 */

public class MySQLiteHandler {
    private MySQLiteOpenHelper helper;
    private SQLiteDatabase db;

    public MySQLiteHandler(Context context){
        helper=new MySQLiteOpenHelper(context, "Middle_DB",null,1);
    }

    //닫기
    public void close(){
        helper.close();
    }

    //저장
    public void insert(String all_location, String final_address, int peopleCount, String transport){
        db=helper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("all_location",all_location);
        values.put("final_address",final_address);
        values.put("peopleCount",peopleCount);
        values.put("transport",transport);
        db.insert("saveLocation", null, values);
    }

    public void delete(String name){
        db=helper.getWritableDatabase();
        db.execSQL("delete from saveLocation where transport='"+name+"';");
        db.close();
    }

    public Cursor select(){
        db= helper.getReadableDatabase();
        Cursor cursor=db.query("saveLocation", null, null, null, null, null, null);
        return cursor;
    }


}
