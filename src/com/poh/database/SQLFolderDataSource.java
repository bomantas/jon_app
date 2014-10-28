package com.poh.database;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class SQLFolderDataSource {
	private static final String DEBUG_TAG = SQLFolderDataSource.class.getSimpleName();
	
    private final SQLFolderHelperClass sqlHelper;
    private SQLiteDatabase database;	
    
    private String[] allSiteColumns =
    {
    		SQLFolderHelperClass.COL_RECID,
    		SQLFolderHelperClass.COL_PARENT,
    		SQLFolderHelperClass.COL_TEXT,
    		SQLFolderHelperClass.COL_LOGO,
    };
    
    public SQLFolderDataSource(Context context)
    {
        sqlHelper = new SQLFolderHelperClass(context);
    }    
    
    public void open() throws SQLException
    {
        database = sqlHelper.getWritableDatabase();
    }

    public void close()
    {
        sqlHelper.close();
    }
    
    private static final String SQL_COUNT_FOR_NAME =
            "SELECT COUNT(*) FROM " +  SQLFolderHelperClass.TABLE_NAME +
            " WHERE " + SQLFolderHelperClass.COL_RECID + " = ? ;";
    

    private long fetchTableCount(int id)
    {
    	if(id == -1)
    		return 0;
    	final SQLiteDatabase db = sqlHelper.getReadableDatabase();

    	final Cursor cursor = db.rawQuery(SQL_COUNT_FOR_NAME, new String[] { ""+id, ""});

    	cursor.moveToFirst();

    	final long count = cursor.getLong(0);

    	cursor.close();

    	return count;
	}    
    
    public long createFolder(int rec_id, int parentID, String text, String logo)
    {
    	ContentValues values = new ContentValues();
    	
    	if(rec_id != -1)
    		values.put(SQLFolderHelperClass.COL_RECID, rec_id);
    	
        values.put(SQLFolderHelperClass.COL_PARENT,  parentID);
        values.put(SQLFolderHelperClass.COL_TEXT, text);
        values.put(SQLFolderHelperClass.COL_LOGO, logo);
        
        long insertId = 0;
        long table_rows = 0;
        
        if(rec_id != -1)
        {
        	table_rows = fetchTableCount(rec_id);
        }    
        if(table_rows > 0)
        {
            String where = SQLFolderHelperClass.COL_RECID + " = ?";
            String[] whereArgs = {""+rec_id};
            database.update(SQLFolderHelperClass.TABLE_NAME, values,
                    where, whereArgs);
        }
        else
        {
            insertId = database.insert(SQLFolderHelperClass.TABLE_NAME, null,
                    values);
        }
        // To show how to query
        Cursor cursor = database.query(SQLFolderHelperClass.TABLE_NAME,
                allSiteColumns,
                SQLFolderHelperClass.COL_RECID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        return insertId;        
    }
    
    public List<SQLFolder> getAllFolderByParentID(int parent_id)
    {
        List<SQLFolder> folders = new ArrayList<SQLFolder>();
        final String sqlQuery = "SELECT * FROM " + SQLFolderHelperClass.TABLE_NAME +
        		" WHERE " + SQLFolderHelperClass.COL_PARENT + " =\'" + parent_id + "\'";

        final Cursor cursor = database.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
        	SQLFolder folder = cursorToFolder(cursor);
        	folders.add(folder);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return folders;
    }  
    
    public List<SQLFolder> getAllFolderByID(int id)
    {
        List<SQLFolder> folders = new ArrayList<SQLFolder>();
        final String sqlQuery = "SELECT * FROM " + SQLFolderHelperClass.TABLE_NAME +
        		" WHERE " + SQLFolderHelperClass.COL_RECID + " =\'" + id + "\'";

        final Cursor cursor = database.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
        	SQLFolder folder = cursorToFolder(cursor);
        	folders.add(folder);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return folders;
    }     
    
    public long folderExists(String fol_name)
    {
    	final String sqlQuery = "SELECT * FROM " + SQLFolderHelperClass.TABLE_NAME + " WHERE " + SQLFolderHelperClass.COL_TEXT + " = \'" + fol_name + "\'";
    	
    	final Cursor cursor = database.rawQuery(sqlQuery, null);
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast())
    	{
    		SQLFolder folder = cursorToFolder(cursor);
    		cursor.close();
    		return folder.getRecid();
    		
    	}
    	return -1;
    }
    
    public List<SQLFolder> getAllParentTopTierFolders()
    {
        List<SQLFolder> folders = new ArrayList<SQLFolder>();
        final String sqlQuery = "SELECT * FROM " + SQLFolderHelperClass.TABLE_NAME + 
        		" WHERE " + SQLFolderHelperClass.COL_PARENT + " =\'" + -1 + "\'";

        final Cursor cursor = database.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
        	SQLFolder folder = cursorToFolder(cursor);
        	folders.add(folder);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return folders;
    }       
        
    
    private static final String SQL_FIND_BUILD_ITEM =
            "SELECT * FROM " + SQLFolderHelperClass.TABLE_NAME +
            " WHERE " +
            SQLFolderHelperClass.COL_RECID    + " = ?";        
    
    public SQLFolder findFolderById(int recid)
    {
    	SQLFolder bld;
        Cursor cursor = database.rawQuery(SQL_FIND_BUILD_ITEM,
                new String[] { "" + recid });
        cursor.moveToFirst();
        while ( !cursor.isAfterLast() )
        {
        	SQLFolder ann = cursorToFolder(cursor);
        	cursor.close();
        	return ann;
        }

        cursor.close();
        return null;
    }  
    
    public void sqlDeleteItem(int id)
    {
        database.delete(SQLFolderHelperClass.TABLE_NAME,
        		SQLFolderHelperClass.COL_RECID +
                "=?", new String[] { id + "" });
    }    
    
    private static SQLFolder cursorToFolder(Cursor cursor)
    {
    	SQLFolder site = new SQLFolder();
        site.setRecid(cursor.getInt(0));
        site.setParent(cursor.getInt(1));
        site.setTitle(cursor.getString(2));
        site.setLogo(cursor.getString(3));
        return site;
    }    
}
