package com.poh.jon_app;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.com.poh.pecs_plus.R;
import com.poh.database.SQLFolder;
import com.poh.database.SQLFolderDataSource;
import com.poh.database.SQLItem;
import com.poh.database.SQLItemDataSource;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;

public class MainActivity extends Activity   implements
TextToSpeech.OnInitListener{
  private static TextToSpeech tts;
  ListView list;
  Context context;
  String path = "";
  List<String> web=new ArrayList<String>();
  List<String> filename=new ArrayList<String>(); 
  List<String> textToSpeak = new ArrayList<String>();
  int current_id = 0;
  int current_parent_id = 0;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    context = this.getApplicationContext();

    
 // create a File object for the parent directory
    File yourFile = new File(Environment.getExternalStorageDirectory(), "/poh_pecs");
    // have the object build the directory structure, if needed.
    yourFile.mkdirs();
    
    readFromJSONFile("poh_pecs/poh_pecs.json");
    
	SQLFolderDataSource sqlFolder = new SQLFolderDataSource(this.getApplicationContext());
	sqlFolder.open();    
    
	List<SQLFolder> starting_items = sqlFolder.getAllParentTopTierFolders();
    sqlFolder.close();
    filename.clear();
    if(starting_items.size() > 0)
    {
    	for(int i = 0; i < starting_items.size(); i++)
    	{
    		SQLFolder fol = starting_items.get(i);
    		web.add(new String(fol.getTitle()));
    		filename.add(new String(fol.getLogo()));
    		textToSpeak.add(new String(fol.getTitle()));
    	}
    }
    current_id = -1;
    current_parent_id = -1;

    CustomList adapter = new CustomList(MainActivity.this, web.toArray(new String[web.size()]), filename.toArray(new String[filename.size()]));
    tts = new TextToSpeech(this, this);
    list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                	SQLFolderDataSource sqlFolder1 = new SQLFolderDataSource(context);
                	SQLItemDataSource sqlItem = new SQLItemDataSource(context);
                	sqlItem.open();
                	sqlFolder1.open();
                	List<SQLFolder> next_tier;
                	speakOut(position);      
                	if(current_parent_id != -1)
                		return;
                	if(current_id == -1)
                	{
                		next_tier = sqlFolder1.getAllParentTopTierFolders();
                		for(int i = 0; i < next_tier.size(); i++)
                		{
                			String t1 = next_tier.get(i).getTitle();
                			String t2 = web.get(position);
                			if(t1.equals( t2 ))
                			{
                				current_id = next_tier.get(i).getRecid();
                				
                			}
                		}
                	}
                	
                	next_tier = sqlFolder1.getAllFolderByParentID(current_id);
                	
                	if(next_tier.size() == 0) {
                		// Item Level
                		List<SQLItem> items = sqlItem.getAllItemsByParentID(current_id);
    					if(items.size() > 0)
    					{
    						web.clear();
    						textToSpeak.clear();
    						current_parent_id = items.get(0).getParentId();
    						current_id = -1;
    						filename.clear();
    						for(int j = 0; j < items.size(); j++)
    						{
    							filename.add(items.get(j).getImageFile());
    							web.add("");
    							textToSpeak.add(new String(items.get(j).getTite()));
    						}
        			    	CustomList adapter1 = new CustomList(MainActivity.this,web.toArray(new String[web.size()]), filename.toArray(new String[filename.size()]));
        			    	list.setAdapter(adapter1);  
        			    	adapter1.notifyDataSetChanged();
    					}
                	}
                	else
                	{
    					web.clear();
    					textToSpeak.clear();
    					filename.clear();
    			    	for(int j = 0; j < next_tier.size(); j++)
    			    	{
    			    		SQLFolder fol = next_tier.get(j);
    			    		current_parent_id = fol.getParent();
    			    		web.add(new String(fol.getTitle()));
    			    		textToSpeak.add(new String(fol.getTitle()));
    			    	}
    			    	CustomList adapter1 = new CustomList(MainActivity.this, web.toArray(new String[web.size()]), filename.toArray(new String[filename.size()]));
    			    	list.setAdapter(adapter1);
    			    	adapter1.notifyDataSetChanged();
                	}
                    sqlFolder1.close();
                    sqlItem.close();                	
                }
            });
  }

@Override
public void onInit(int status) {
    if (status == TextToSpeech.SUCCESS) 
    {
 	   
         int result = tts.setLanguage(Locale.US);
         

         if (result == TextToSpeech.LANG_MISSING_DATA
                 || result == TextToSpeech.LANG_NOT_SUPPORTED) {
             Log.e("TTS", "This Language is not supported");
         } else {
         }

     } else {
         Log.e("TTS", "Initilization Failed!");
     }
  }

@Override
public void onBackPressed() {
	SQLFolderDataSource sqlFolder = new SQLFolderDataSource(context);
	sqlFolder.open();
    if(this.current_parent_id != -1) // At the item level.
    {
    	List<SQLFolder> next_tier = sqlFolder.getAllFolderByID(this.current_parent_id);
		web.clear();
		textToSpeak.clear();
		filename.clear();
    	if(next_tier != null && next_tier.size() > 0 && next_tier.get(0).getParent() == -1)
    	{
    		// Top level
    		next_tier= sqlFolder.getAllParentTopTierFolders();
    	    sqlFolder.close();
    	    filename.clear();
    	    if(next_tier.size() > 0)
    	    {
    	    	for(int i = 0; i < next_tier.size(); i++)
    	    	{
    	    		SQLFolder fol = next_tier.get(i);
    	    		web.add(new String(fol.getTitle()));
    	    		filename.add(new String(fol.getLogo()));
    	    		textToSpeak.add(new String(fol.getTitle()));
    	    	}
    	    }    
    	    current_parent_id = -1;
    	}
    	else
    	{
    		for(int j = 0; (next_tier != null) && j < next_tier.size(); j++)
    		{
    			SQLFolder fol = next_tier.get(j);
    			web.add(new String(fol.getTitle()));
    			textToSpeak.add(new String(fol.getTitle()));
    			current_parent_id = fol.getParent();
    			current_id = fol.getRecid();
    		}
    	}
    	CustomList adapter = new CustomList(MainActivity.this, web.toArray(new String[web.size()]), filename.toArray(new String[filename.size()]));
    	list.setAdapter(adapter);
    	adapter.notifyDataSetChanged();
    }
    else
    {
        sqlFolder.close();    	
    	finish();
    }
    sqlFolder.close();
}

void speakOut(int position) 
{
	tts.speak(textToSpeak.get(position), TextToSpeech.QUEUE_FLUSH, null);
 }

@Override
protected void onSaveInstanceState(Bundle icicle)
{
	super.onSaveInstanceState(icicle);
	icicle.putInt("current_id", this.current_id);
	icicle.putInt("parent_id", this.current_parent_id);
	icicle.putStringArrayList("files", (ArrayList<String>) filename);
	icicle.putStringArrayList("web", (ArrayList<String>) web);
	icicle.putStringArrayList("textToSpeak", (ArrayList<String>) textToSpeak);
}

@Override
protected void onRestoreInstanceState(Bundle icicle)
{
	super.onRestoreInstanceState(icicle);
	this.current_id = icicle.getInt("current_id");
	this.current_parent_id = icicle.getInt("parent_id");
	filename = icicle.getStringArrayList("files");
	web = icicle.getStringArrayList("web");
	textToSpeak = icicle.getStringArrayList("textToSpeak");
	SQLFolderDataSource sqlFolder = new SQLFolderDataSource(context);
	SQLItemDataSource sqlItem = new SQLItemDataSource(context);
	sqlItem.open();
	sqlFolder.open();
	if(this.current_parent_id >= 0)
	{
		// Item level.
		List<SQLItem> items = sqlItem.getAllItemsByParentID(current_parent_id);
		if(items.size() > 0)
		{
			web.clear();
			textToSpeak.clear();
			current_id = -1;
			filename.clear();
			for(int j = 0; j < items.size(); j++)
			{
				filename.add(items.get(j).getImageFile());
				web.add("");
				textToSpeak.add(new String(items.get(j).getTite()));
			}
	    	CustomList adapter = new CustomList(MainActivity.this,web.toArray(new String[web.size()]), filename.toArray(new String[filename.size()]));
	    	list.setAdapter(adapter);  
	    	adapter.notifyDataSetChanged();
		}
	}
	sqlItem.close();
	sqlFolder.close();
}

private void readFromJSONFile(String file_name) {
	// TODO, check if needs to be updated.
	SQLFolderDataSource sqlFolder = new SQLFolderDataSource(this.getApplicationContext());
	SQLItemDataSource sqlItem = new SQLItemDataSource(this.getApplicationContext());
	sqlItem.open();
	sqlFolder.open();	
	try {

		File menuJson = new File(Environment.getExternalStorageDirectory(), file_name);
        FileInputStream stream = new FileInputStream(menuJson);
        String jsonStr = null;
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            jsonStr = Charset.defaultCharset().decode(bb).toString();
          }
          finally {
            stream.close();
          }
        JSONObject jsonObj = new JSONObject(jsonStr);

        // Getting data JSON Array nodes
	    JSONArray data  = jsonObj.getJSONArray("mainfolder");

	    // looping through All nodes
	    for (int i = 0; i < data.length(); i++) {
	    	JSONObject c = data.getJSONObject(i);

	    	String name = c.getString("name");
	    	String logo = c.getString("logo");
	    	long parent_id = -1;
	    	parent_id = sqlFolder.folderExists(name);
            // McDonalds
	    	// Add Main folder here.
	    	if(parent_id == -1) 
	    	{
	    		parent_id = sqlFolder.createFolder(-1, -1, name, logo);
	    	}
	    	JSONArray items  = c.getJSONArray("items");
	    		for(int s = 0; s < items.length(); s++)
	    		{
	    			JSONObject itm = items.getJSONObject(s);
	    			String item_title = itm.getString("title");
	    			String item_filename = itm.getString("filename");
	    			
	    			long itm_id = sqlItem.ItemExists(item_title, parent_id);
	    			if(itm_id == -1)
	    			{
	    				itm_id = sqlItem.createItem(-1, parent_id, item_filename, item_title);
	    			}
	    		}
                // do what do you want on your interface
	    }
       } catch (Exception e) {
    	   Toast.makeText(getApplicationContext(), e.getMessage(), 
    			   Toast.LENGTH_LONG).show();
    	   e.printStackTrace();
    	   
      }
	sqlItem.close();
	sqlFolder.close();	
   }
}
