package com.poh.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLItemDataSource {
	private static final String DEBUG_TAG = SQLItemDataSource.class.getSimpleName();
	
    private final SQLItemHelperClass sqlHelper;
    private SQLiteDatabase database;	
    
    private String[] allSiteColumns =
    {
    		SQLItemHelperClass.COL_RECID,
    		SQLItemHelperClass.COL_PARENT,
    		SQLItemHelperClass.COL_TEXT
    };
    
    public SQLItemDataSource(Context context)
    {
        sqlHelper = new SQLItemHelperClass(context);
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
            "SELECT COUNT(*) FROM " +  SQLItemHelperClass.TABLE_NAME +
            " WHERE " + SQLItemHelperClass.COL_RECID + " = ? ;";
    

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
    
    public long createItem(int rec_id, long fol_id, String filename, String text)
    {
    	ContentValues values = new ContentValues();
    	
    	if(rec_id != -1)
    		values.put(SQLItemHelperClass.COL_RECID, rec_id);
    	
        values.put(SQLItemHelperClass.COL_PARENT,  fol_id);
        values.put(SQLItemHelperClass.COL_IMG_FILE, filename);
        values.put(SQLItemHelperClass.COL_TEXT, text);
        
        long insertId = 0;
        long table_rows = 0;
        
        if(rec_id != -1)
        {
        	table_rows = fetchTableCount(rec_id);
        }    
        if(table_rows > 0)
        {
            String where = SQLItemHelperClass.COL_RECID + " = ?";
            String[] whereArgs = {""+rec_id};
            database.update(SQLItemHelperClass.TABLE_NAME, values,
                    where, whereArgs);
        }
        else
        {
            insertId = database.insert(SQLItemHelperClass.TABLE_NAME, null,
                    values);
        }
        // To show how to query
        Cursor cursor = database.query(SQLItemHelperClass.TABLE_NAME,
                allSiteColumns,
                SQLItemHelperClass.COL_RECID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        return insertId;        
    }
    
    public List<SQLItem> getAllItemsByParentID(int parent_id)
    {
        List<SQLItem> items = new ArrayList<SQLItem>();
        final String sqlQuery = "SELECT * FROM " + SQLItemHelperClass.TABLE_NAME +
        		" WHERE " + SQLItemHelperClass.COL_PARENT + " =\'" + parent_id + "\'";

        final Cursor cursor = database.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
        	SQLItem folder = cursorToItem(cursor);
        	items.add(folder);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return items;
    }       
    
    public List<SQLItem> getAllParentTopTierFolders()
    {
        List<SQLItem> folders = new ArrayList<SQLItem>();
        final String sqlQuery = "SELECT * FROM " + SQLItemHelperClass.TABLE_NAME + 
        		" WHERE " + SQLItemHelperClass.COL_PARENT + " =\'" + 0 + "\'";

        final Cursor cursor = database.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
        	SQLItem folder = cursorToItem(cursor);
        	folders.add(folder);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return folders;
    }       
        
    
    private static final String SQL_FIND_BUILD_ITEM =
            "SELECT * FROM " + SQLItemHelperClass.TABLE_NAME +
            " WHERE " +
            SQLItemHelperClass.COL_RECID    + " = ?";        
    
    public SQLItem findFolderById(int recid)
    {
    	SQLItem bld;
        Cursor cursor = database.rawQuery(SQL_FIND_BUILD_ITEM,
                new String[] { "" + recid });
        cursor.moveToFirst();
        while ( !cursor.isAfterLast() )
        {
        	SQLItem ann = cursorToItem(cursor);
        	cursor.close();
        	return ann;
        }

        cursor.close();
        return null;
    }  

    public long ItemExists(String title, long fol_id)
    {
    	final String sqlQuery = "SELECT * FROM " + SQLItemHelperClass.TABLE_NAME + " WHERE " + SQLItemHelperClass.COL_TEXT + " = \'" + title + "\'";
    	
    	final Cursor cursor = database.rawQuery(sqlQuery, null);
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast())
    	{
    		SQLItem itm = cursorToItem(cursor);
    		if(itm.getParentId() == fol_id)
    		{
    			cursor.close();
    			return itm.getRecid();
    		}
    		cursor.moveToNext();
    		
    	}
    	cursor.close();
    	return -1;
    }    
    
    public void sqlDeleteItem(int id)
    {
        database.delete(SQLItemHelperClass.TABLE_NAME,
        		SQLItemHelperClass.COL_RECID +
                "=?", new String[] { id + "" });
    }    
    
    private static SQLItem cursorToItem(Cursor cursor)
    {
    	SQLItem site = new SQLItem();
        site.setRecid(cursor.getInt(0));
        site.setParentId(cursor.getInt(1));
        site.setImageFile(cursor.getString(2));
        site.setTite(cursor.getString(3));
        return site;
    }  
}
