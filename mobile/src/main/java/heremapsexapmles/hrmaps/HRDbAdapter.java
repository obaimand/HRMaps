package heremapsexapmles.hrmaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sachin on 2018-02-07.
 */

public class HRDbAdapter {

    private static final String DATABASE_NAME="HRdatabase.db";
    private static final int DATABASE_VERSION=4;

    public static final String TABLE="trip_details";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_TITLE="title";
    public static final String COLUMN_LANES="lanes";
    public static final String COLUMN_TIME="time";
    public static final String COLUMN_STARTPOINT="startpoint";
    public static final String COLUMN_ENDPOINT="endpoint";

    private String[] allColumns={COLUMN_ID,COLUMN_TITLE, COLUMN_LANES , COLUMN_TIME  };


    public static final String CREATE_TABLE ="create table "+ TABLE+ " ( "+
            COLUMN_ID+ " integer primary key autoincrement, "+
            COLUMN_TITLE + " text not null, " +
            COLUMN_LANES + " text, " +
            COLUMN_TIME +  " text, "+
            COLUMN_STARTPOINT + " text, "+
            COLUMN_ENDPOINT + " text ) ; ";




    private SQLiteDatabase sqlDB;
    private Context context;

    private HRDbHelper hrDbHelper;

    public HRDbAdapter(Context ctx)
    {
        context=ctx;

    }
    public HRDbAdapter open() throws android.database.SQLException{

        hrDbHelper=new HRDbHelper(context);
        sqlDB=hrDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){hrDbHelper.close();}


    public long InsertinDatabase(DataHolder dataHolder){
        ContentValues values= new ContentValues();
        values.put(COLUMN_TITLE, dataHolder.getTitle());
        values.put(COLUMN_LANES, dataHolder.getLanes());
        values.put(COLUMN_TIME, dataHolder.getTime());

        long inserId= sqlDB.insert(TABLE, null,values);

        return inserId;
    }

    public ArrayList<DataHolder> getAllNotes(){
        ArrayList<DataHolder> myNotes = new ArrayList<DataHolder>();

        //getting the information from the database for the myNotes in it
        Cursor cursor=sqlDB.query(TABLE, allColumns, null, null, null, null, null);

        //Looping through all the objects of the database and adding them to the list
        for(cursor.moveToLast();!cursor.isBeforeFirst();cursor.moveToPrevious()){
          //  String title =cursor.getString(2);

            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String time= cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
            String lanes= cursor.getString(cursor.getColumnIndex(COLUMN_LANES));
            myNotes.add(new DataHolder(title, lanes, time));
        }
        cursor.close();
        return myNotes;

    }

    private class HRDbHelper extends SQLiteOpenHelper{

        HRDbHelper(Context ctx){
            super(ctx, DATABASE_NAME , null , DATABASE_VERSION );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Create note table
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("Upgrading DataBase", "Upgrading Database");
            Log.w(HRDbHelper.class.getName(),
                    "Upgrading database from Version"+ oldVersion+" to "+newVersion);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE);
            onCreate(db);
        }
    }


}
