package m.i.d.mid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jooyoung on 2016-12-01.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper{

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //테이블 생성 작업
        String sql="create table saveLocation(_id integer primary key autoincrement, all_location text, final_address text, peopleCount integer, transport text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //테이블 삭제
        db.execSQL("drop table if exists saveLocation");
        onCreate(db);
    }
}
