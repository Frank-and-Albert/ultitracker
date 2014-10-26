package com.example.frisbeestats;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;


public class EditRoster extends ListActivity
{
		
	private EditText addNewName;
	private TextView selectedPlayer;
	
	private ArrayList<String> roster = new ArrayList<String>();
	
	private ArrayAdapter<String> adapter;
	
	private String rosterName;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_roster);
		
		roster = getIntent().getStringArrayListExtra("outputRoster");
		rosterName = getIntent().getStringExtra("HomeRosterName");
		
		initializeListView();
		
		TextView tv = (TextView) findViewById(R.id.rosterNameInEditRoster);
		tv.setText(rosterName);
		
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
//			dialogName = (EditText)findViewById(R.id.editPlayer);
//        	dialogName.setText("");
    	selectedPlayer = (TextView) view;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(EditRoster.this);
	    // Get the layout inflater
	    LayoutInflater inflater = EditRoster.this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View v = inflater.inflate(R.layout.dialog_edit_player, null);
	    builder.setView(v);
	    builder.setTitle("Edit or Delete Player");
	    
	    final EditText newName = (EditText) v.findViewById(R.id.editPlayer);
	    newName.setText(roster.get(position));
	    newName.setSelection(newName.getText().length());
	    
	    // Add action buttons
	    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
	    	
	    	public void onClick(DialogInterface dialog, int id) {
	    		String name = newName.getText().toString();
            	String removePlayer = selectedPlayer.getText().toString();
            	if(roster.contains(name) || name.matches(""))
    			{
    				return;
    			}
    			else
    			{
    				roster.set(roster.indexOf(removePlayer), name);
    			}
            	try 
            	{
            		initializeListView();
            		updateRoster();
            	}
            	catch(Exception e)
            	{
            		Log.e("EditRoster", e.getMessage(), e);
            	}
            	
	    	}
	    });
	    
	    builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
	    	
		    public void onClick(DialogInterface dialog, int which)
		    {
			   	String removePlayer = selectedPlayer.getText().toString();
				roster.remove(removePlayer);
				try 
				{
					initializeListView();
					updateRoster();
				}
				catch(Exception e)
				{
					Log.e("EditRoster", e.getMessage(), e);
				}
		    }
	    });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
	    builder.show();
	}
	
	private void initializeListView() {
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, android.R.id.text1, roster);
		setListAdapter(adapter); 
		Collections.sort (roster, new Comparator<String>() {
	        @Override
	        public int compare(String s1, String s2) {
	            return s1.compareToIgnoreCase(s2);
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_roster_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		if (item.getItemId() == R.id.edit_done) {
			Intent intent = new Intent(this, RosterActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void addNewPlayer(View view)
	{
		addNewName = (EditText) findViewById(R.id.addNewPlayer);
		String name = addNewName.getText().toString();
		if(roster.contains(name))
		{
			Toast.makeText(this, "Roster contains " + name, Toast.LENGTH_SHORT).show();
		}
		else if(name.matches(""))
		{
			Toast.makeText(this, "Enter player name", Toast.LENGTH_SHORT).show();
		}
		else
		{
			roster.add(name);
			try 
			{
				initializeListView();
				updateRoster();
			}
			catch(Exception e)
			{
				Log.e("EditRoster", e.getMessage(), e);
			}
			addNewName.setText("");
		}
	}
	
	public void home(View view)
	{
		Intent intent = new Intent(this, RosterActivity.class);
		startActivity(intent);
	}
	
	private void updateRoster()
	{
		File sdCardRoot = Environment.getExternalStorageDirectory();     
		File rosterDir = new File(sdCardRoot, "Ultimate Stats/Roster");
		
		File file = new File(rosterDir, rosterName + ".csv");
		
		try 
		{	
			file.createNewFile();                
			
			CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
			
			for (int i = 0; i < roster.size(); i++) {
				String arrStr[] = {roster.get(i)};
				csvWrite.writeNext(arrStr);
			}
			csvWrite.close();
			Toast.makeText(this, "Roster file updated", Toast.LENGTH_SHORT).show();
			
		}
		catch(Exception e)
		{
			Log.e("SaveRoster", e.getMessage(), e);
		}
		
	}
}