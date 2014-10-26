package com.example.frisbeestats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

public class RosterActivity extends Activity {

	private EditText rosterName;
	
	private EditText homeNameText;
	private EditText opponentNameText;
	TextView selectedRoster;
	
	private ArrayList<String> rosterSpinnerList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	
	private Spinner rosterSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);
		
		initializeRosterSpinner();
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_roster, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		if (item.getItemId() == R.id.saved_games) {
			Intent intent = new Intent(this, SavedGamesActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initializeRosterSpinner() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			
			File sdCardRoot = Environment.getExternalStorageDirectory();
			File rosterDir = new File(sdCardRoot, "Ultimate Stats/Roster");        
			
			if (!rosterDir.exists()) 
			{
				rosterDir.mkdirs();
			}
			
			updateRosterSpinner(rosterDir);
		}
		else {
			Log.d("Test", "sdcard state: " + state);
		}
	}
	
	private void updateRosterSpinner(File rosterDirectory) {
		
		rosterSpinnerList.clear();
		
		for (File f : rosterDirectory.listFiles()) {
	        if (f.isFile()) {
	        	String name = f.getName();
	        	
	        	int lastPeriodPos = name.lastIndexOf('.');
                File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
                
	        	rosterSpinnerList.add(renamed.getName());
		        Collections.sort(rosterSpinnerList);
	        }	
		}
		rosterSpinner = (Spinner) findViewById(R.id.rosterSpinner);

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rosterSpinnerList);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		rosterSpinner.setAdapter(adapter);
	}
	

	
	public void createRoster(View view) {
		
		rosterName = (EditText) findViewById(R.id.rosterNameText);
		String roster = rosterName.getText().toString();
		
		if (roster.length()==0) {
			Toast.makeText(this, "Enter a Roster Name", Toast.LENGTH_SHORT).show();
		}
		else {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				Log.d("Test", "sdcard mounted and writable");
				
				saveRoster(roster);
				
				Intent intent = new Intent(this, EditRoster.class);
				ArrayList<String> newRoster = new ArrayList<String>();
				intent.putStringArrayListExtra("outputRoster", newRoster);
				intent.putExtra("HomeRosterName", roster);
				startActivity(intent);
				
			}
			else {
				Log.d("Test", "sdcard state: " + state);
				Toast.makeText(this, "SDCard is not writeable", Toast.LENGTH_SHORT).show();
			}
		}

		rosterName.setText("");
		
	}

	private void saveRoster(String rosterFileName) {
		
		File sdCardRoot = Environment.getExternalStorageDirectory();     
		File rosterDir = new File(sdCardRoot, "Ultimate Stats/Roster");
		
		File file = new File(rosterDir, rosterFileName + ".csv");
		
		try 
		{	
			file.createNewFile();
		
			updateRosterSpinner(rosterDir);
			
		}
		catch(Exception e)
		{
			Log.e("SaveRoster", e.getMessage(), e);
		}
		
	}
	
	public void startTracking(View view) {
		
		if (rosterSpinnerList.isEmpty()) {
			Toast.makeText(this, "Create Home Team Roster", Toast.LENGTH_SHORT).show();
			return;
		}
		
		opponentNameText = (EditText) findViewById(R.id.opponentNameRosterActivity);
		homeNameText = (EditText) findViewById(R.id.homeNameRosterActivity);
		
		if (opponentNameText.getText().toString().isEmpty() || homeNameText.getText().toString().isEmpty()) {
			Toast.makeText(this, "Enter Names for Home and Opponent Teams", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ArrayList<String> outputRoster = readCSV();
		
		if (outputRoster.isEmpty()) {
			Toast.makeText(this, "Selected Roster is Empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//Set game tab textView Opponent and Home names
		
		AddStat.currOpponentName = opponentNameText.getText().toString();
		opponentNameText.setText("");
		
		
		AddStat.currHomeName = homeNameText.getText().toString();
		homeNameText.setText("");
		
		AddStat.currFilePath = new File(Environment.getExternalStorageDirectory(), "UltimateStats/Directory");
		
		Intent intent = new Intent(this, AddStat.class);
		
		intent.putStringArrayListExtra("outputRoster", outputRoster);
		startActivity(intent);
	}
	
	public ArrayList<String> readCSV()
	{
		ArrayList<String> selectedRosterPlayers = new ArrayList<String>();
		String selectedRosterFileName = rosterSpinner.getSelectedItem().toString();
		
		File sdCardRoot = Environment.getExternalStorageDirectory();
		File rosterFile = new File(sdCardRoot, "Ultimate Stats/Roster/" + selectedRosterFileName + ".csv");
		
		try 
		{
			CSVReader reader = new CSVReader(new FileReader(rosterFile));
			String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		        selectedRosterPlayers.add(nextLine[0]);
		    }
		    reader.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return selectedRosterPlayers;
	}
	
	public void editRoster(View view)
	{
		Intent intent = new Intent(this, EditRoster.class);
		ArrayList<String> outputRoster = readCSV();
		intent.putStringArrayListExtra("outputRoster", outputRoster);
		intent.putExtra("HomeRosterName", rosterSpinner.getSelectedItem().toString());
		startActivity(intent);
	}

	public void deleteRoster(View view)
	{
		System.out.println("Delete attempt");
		String selectedRosterFileName = rosterSpinner.getSelectedItem().toString();
		rosterSpinnerList.remove(selectedRosterFileName);
		adapter.remove(selectedRosterFileName);
		rosterSpinner.setAdapter(adapter);
		File sdCardRoot = Environment.getExternalStorageDirectory();     
		File rosterDir = new File(sdCardRoot, "Ultimate Stats/Roster");
		
		File file = new File(rosterDir, selectedRosterFileName + ".csv");
		
		try 
		{	
			file.delete();
		
//			updateRosterSpinner(rosterDir);
			
		}
		catch(Exception e)
		{
			Log.e("DeleteRoster", e.getMessage(), e);
		}
		System.out.println("Delete Successful");
	}
}
