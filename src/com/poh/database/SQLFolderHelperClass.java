package com.poh.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLFolderHelperClass  extends SQLiteOpenHelper {
	
	
    private static final String DB_NAME    = "FOLDERS.db";
    private static final int    DB_VERSION = 1;
    
    public static final String TABLE_NAME = "FOLDER";
    
    public static final String COL_RECID 	 = "recid";
    public static final String COL_PARENT 	 = "parent";
    public static final String COL_TEXT 	 = "text";
    public static final String COL_LOGO      = "logo";
    
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
            "( " +
            COL_RECID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_PARENT  + " INTEGER, " +
            COL_TEXT +    " TEXT NOT NULL, " +
            COL_LOGO + " TEXT NOT NULL " + 
            ")";    
    
    private static final String SQL_CREATE_INDEX =
            "CREATE UNIQUE INDEX " + TABLE_NAME + "_IDX1 ON " +
            TABLE_NAME + "(" + COL_RECID + ")";
    
    public SQLFolderHelperClass(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_INDEX);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLFolderHelperClass.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
		
	}	

}
