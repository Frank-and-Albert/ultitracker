package com.example.frisbeestats;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import au.com.bytecode.opencsv.CSVReader;

public class SavedGamesActivity extends ListActivity {
	
	// List to be shown in listView
	private ArrayList<String> savedGamesList = new ArrayList<String>();
	
	// List of paths to games in sdcard
	private ArrayList<File> savedGamesFilePathList = new ArrayList<File>();
	
	// Roster of selected saved game
	private ArrayList<String> savedGamesRoster = new ArrayList<String>();
	
	// Players of selected saved game
	private ArrayList<Player> savedGamesPlayerList = new ArrayList<Player>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_games);
		
		getSavedGames();
		initializeListView();
	}

	private void getSavedGames() {
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			
			savedGamesFilePathList.clear();
			savedGamesList.clear();
			
			File sdCardRoot = Environment.getExternalStorageDirectory();
			File dir = new File(sdCardRoot, "Ultimate Stats/Directory");        
			
			if (!dir.exists()) 
			{
				dir.mkdirs();
			}
			
			for (File f : dir.listFiles()) {	
				for (File g : f.listFiles()) {
					if (g.isFile()) {
						String folderName = f.getName();
						String fileName = g.getName();
						
						savedGamesFilePathList.add(g);
						
						int lastPeriodPos = fileName.lastIndexOf('.');
		                File renamed = new File(g.getParent(), fileName.substring(0, lastPeriodPos));
						
		                savedGamesList.add("(" + folderName + ") " + renamed.getName());
					}
				}
			}
		}
		else {
			Log.d("Test", "sdcard state: " + state);
		}
	}

	private void initializeListView() {
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, android.R.id.text1, savedGamesList);
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		final int pos = position;
		AlertDialog.Builder builder = new AlertDialog.Builder(SavedGamesActivity.this);
		String options[] = {"Edit/Resume","Preview","Delete"};
		
		builder.setTitle("Select Option");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				switch (which) {
				case 0: 
					goToSavedGame(pos);
					break;
				case 1: 
					goToPreview(pos);
					break;
				case 2: 
					deleteDialog(pos);
					break;
				default: 
					break;
				
				}
				
			}

		});
		builder.show();
		
	}
	

	private void goToPreview(int pos) {
		String promptName = savedGamesList.get(pos);
		
		Intent intent = new Intent(this,PreviewActivity.class);
		
		intent.putExtra("game file name", savedGamesFilePathList.get(pos).getPath());
		intent.putExtra("promptName", promptName);
		
		startActivity(intent);
	}

	private void deleteDialog(int position) {
		final int pos = position;
		
		String promptName = savedGamesList.get(pos);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(SavedGamesActivity.this);
    	builder.setTitle("Delete File: " + promptName + "?");
    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int id) {
    			
    			File fileToBeDeleted = savedGamesFilePathList.get(pos);
    			//deleteFile(position);
    			System.out.println(fileToBeDeleted.delete());
    			
    			savedGamesList.remove(pos);
    			
    			initializeListView();
    		}
    	});
    	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int id) {
    			
    			dialog.cancel();
    		}
    	});
    	builder.show();
	}

	private void goToSavedGame(int pos) {
		
		File savedFile = savedGamesFilePathList.get(pos);
		
		savedGamesRoster.clear();
		savedGamesPlayerList.clear();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(savedFile));
			
			reader.readNext();
			String homeNameScore[] = reader.readNext();
			String opponentNameScore[] = reader.readNext();
			
			String savedGameHomeName = homeNameScore[0];
			int savedGameHomeScore = Integer.parseInt(homeNameScore[1]);
			
			String savedGameOpponentName = opponentNameScore[0];
			int savedGameOpponentScore = Integer.parseInt(opponentNameScore[1]);
			
			reader.readNext(); //Blank line
			reader.readNext(); //Player stat Header
			
			String nextLine[];
		    while ((nextLine = reader.readNext()) != null) {
		    	
		    	// add player names to roster
		    	savedGamesRoster.add(nextLine[0]);
		    	
		    	Player newPlayer = new Player();
		    	
		    	newPlayer.setName(nextLine[0]);
		    	newPlayer.setGoals(Integer.parseInt(nextLine[1]));
		    	newPlayer.setAssists(Integer.parseInt(nextLine[2]));
		    	newPlayer.setBlocks(Integer.parseInt(nextLine[3]));
		    	newPlayer.setTurns(Integer.parseInt(nextLine[4]));
		    	newPlayer.setFantasy(Integer.parseInt(nextLine[5]));
		    	
		    	savedGamesPlayerList.add(newPlayer);
		    }
		    reader.close();
		    
		    Intent intent = new Intent(this, AddStat.class);
		    
		    AddStat.currFilePath = savedFile;
		    AddStat.currHomeName = savedGameHomeName;
		    AddStat.currHomeScore = savedGameHomeScore;
		    AddStat.currOpponentName = savedGameOpponentName;
		    AddStat.currOpponentScore = savedGameOpponentScore;
		    AddStat.playerListFromSavedGame = savedGamesPlayerList;
		    
		    intent.putStringArrayListExtra("savedGameRosterFromStartScreen", savedGamesRoster);
		    
			startActivity(intent);
		    
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.saved_games_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		if (item.getItemId() == R.id.new_game) {
			Intent intent = new Intent(this, RosterActivity.class);
			startActivity(intent);
	    }
		return super.onOptionsItemSelected(item);
	}
}