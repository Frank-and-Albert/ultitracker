package com.example.frisbeestats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class AddStat extends Activity {
	
	public static String currOpponentName;
	public static String currHomeName;
	
	public static File currFilePath;
	
	public static int currOpponentScore;
	public static int currHomeScore;
	
	public static ArrayList<Player> playerListFromSavedGame = new ArrayList<Player>();
	
	private int currentID;
	
	private TextView f; //fantasy
	
	private TextView currSavedPath;
	
	private NumberPicker gp;
	private NumberPicker ap;
	private NumberPicker bp;
	private NumberPicker tp;
	
	private NumberPicker opponentScorePicker; //PickerOpponent
	private NumberPicker homeScorePicker; //PickerHome
	
	private Spinner nameSpinner; //spinner1
	
	private TextView opponentNameGameTab;
	private TextView homeNameGameTab;
	
	private Spinner directorySpinner;
	
	// list of directory names
	private ArrayList<String> directoryList = new ArrayList<String>();
	
	// list of the names in spinner (STATS tab)
	private ArrayList<String> rosterList;
	
	// list of player objects
	private ArrayList<Player> playerList = new ArrayList<Player>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_stat);
		
		disableSoftKeyboardForNumberPickers();
		
		//Set Home Name
		homeNameGameTab = (TextView) findViewById(R.id.HomeName);
		homeNameGameTab.setText(currHomeName);
		
		//Set Opponent Name
		opponentNameGameTab = (TextView) findViewById(R.id.OpponentName);
		opponentNameGameTab.setText(currOpponentName);
		
		updateCurrentSavedPathTextView();
		
		// If starting a new file
		if (!currFilePath.isFile()) {
			rosterList = getIntent().getStringArrayListExtra("outputRoster");
			addPlayers();
			currOpponentScore=0;
			currHomeScore=0;
		}
		else {
			rosterList = getIntent().getStringArrayListExtra("savedGameRosterFromStartScreen");
			playerList = playerListFromSavedGame;
		}
		
		tabsSetup();
		initializeNameSpinner();
		initializePickers();
		initializeDirectorySpinner();

	}

	private void disableSoftKeyboardForNumberPickers() {
		
		gp = (NumberPicker) findViewById(R.id.goalsPicker);
		ap = (NumberPicker) findViewById(R.id.assistsPicker);
		bp = (NumberPicker) findViewById(R.id.blocksPicker);
		tp = (NumberPicker) findViewById(R.id.turnsPicker);
		opponentScorePicker = (NumberPicker) findViewById(R.id.PickerOpponent);
		homeScorePicker = (NumberPicker) findViewById(R.id.PickerHome);
		
		// Disable soft-keyboard for pickers
		gp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		ap.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		bp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		tp.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		opponentScorePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		homeScorePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_stat, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		if (item.getItemId() == R.id.homeButton) {
			// ask user to save?
			AlertDialog.Builder builder = new AlertDialog.Builder(AddStat.this);
			builder.setMessage("Save Stats Before Leaving Game?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
					if (!currFilePath.isFile()) {
						dialog.cancel();
						Toast.makeText(AddStat.this, "Open INFO tab and edit file name before saving", Toast.LENGTH_SHORT).show();
					}
					else {
						writeCSV();
						Intent intent = new Intent(AddStat.this, SavedGamesActivity.class);
						startActivity(intent);
					}
					
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					
					Intent intent = new Intent(AddStat.this, SavedGamesActivity.class);
					startActivity(intent);
				}
			});
			builder.show();
			
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addPlayers() {
		
		for(String s : rosterList)
		{
			Player newPlayer = new Player();
			newPlayer.setName(s);
			playerList.add(newPlayer);
		}
		
		Collections.sort(playerList, new Comparator<Player>() {
			public int compare(Player one, Player two) {
				return one.getName().compareTo(two.getName());
			}
		});
	}
	
	private void tabsSetup() {
		TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec("Game");
		spec1.setContent(R.id.Game);
		spec1.setIndicator("Game");

		TabSpec spec2=tabHost.newTabSpec("Stats");
		spec2.setIndicator("Stats");
		spec2.setContent(R.id.Stats);

		TabSpec spec3=tabHost.newTabSpec("Info");
		spec3.setIndicator("Info");
		spec3.setContent(R.id.Info);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
	}

	private void initializeNameSpinner() {
		nameSpinner = (Spinner) findViewById(R.id.spinner1);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rosterList);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		nameSpinner.setAdapter(adapter);
		
		nameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				currentID = position;
				
				// If there is no spinner selection, position is -1
				if(position >= 0) {
					
					Player currentPlayer = (Player) playerList.get(position);
					
					gp = (NumberPicker) findViewById(R.id.goalsPicker);
					ap = (NumberPicker) findViewById(R.id.assistsPicker);
					bp = (NumberPicker) findViewById(R.id.blocksPicker);
					tp = (NumberPicker) findViewById(R.id.turnsPicker);
					
					gp.setValue(currentPlayer.getGoals());
					ap.setValue(currentPlayer.getAssists());
					bp.setValue(currentPlayer.getBlocks());
					tp.setValue(currentPlayer.getTurns());
					
					updateFantasy();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//BLAH
			}
		});
	}
	
	private void initializePickers() {
		gp = (NumberPicker) findViewById(R.id.goalsPicker);
		ap = (NumberPicker) findViewById(R.id.assistsPicker);
		bp = (NumberPicker) findViewById(R.id.blocksPicker);
		tp = (NumberPicker) findViewById(R.id.turnsPicker);
		opponentScorePicker = (NumberPicker) findViewById(R.id.PickerOpponent);
		homeScorePicker = (NumberPicker) findViewById(R.id.PickerHome);
		
		gp.setValue(0);
		gp.setMaxValue(100);
		gp.setMinValue(0);
		gp.setWrapSelectorWheel(false);
		// Value change listener to set player statistics
		gp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (playerList.size() > 0) {
					
					((Player) playerList.get(currentID)).setGoals(newVal);
					
					updateFantasy();
				}
			}
		});
		
		ap.setValue(0);
		ap.setMaxValue(100);
		ap.setMinValue(0);
		ap.setWrapSelectorWheel(false);
		// Value change listener to set player statistics
		ap.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (playerList.size() > 0) {

					((Player) playerList.get(currentID)).setAssists(newVal);
					
					updateFantasy();
				}
			}
		});
		
		bp.setValue(0);
		bp.setMaxValue(100);
		bp.setMinValue(0);
		bp.setWrapSelectorWheel(false);
		// Value change listener to set player statistics
		bp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (playerList.size() > 0) {
					
					((Player) playerList.get(currentID)).setBlocks(newVal);

					updateFantasy();
				}
			}
		});
		
		tp.setValue(0);
		tp.setMaxValue(100);
		tp.setMinValue(0);
		tp.setWrapSelectorWheel(false);
		// Value change listener to set player statistics
		tp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (playerList.size() > 0) {
					
					((Player) playerList.get(currentID)).setTurns(newVal);

					updateFantasy();
				}
			}
		});
		
		opponentScorePicker.setMaxValue(25);
		opponentScorePicker.setMinValue(0);
		opponentScorePicker.setWrapSelectorWheel(false);
		
		homeScorePicker.setMaxValue(25);
		homeScorePicker.setMinValue(0);
		homeScorePicker.setWrapSelectorWheel(false);
		
		opponentScorePicker.setValue(currOpponentScore);
		
		homeScorePicker.setValue(currHomeScore);
	}

	private void initializeDirectorySpinner() {
		
		File sdCardRoot = Environment.getExternalStorageDirectory();
		File dir = new File(sdCardRoot, "Ultimate Stats/Directory");
		
		directoryList.clear();
		
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				
				directoryList.add(f.getName());
				Collections.sort(directoryList);
			}	
		}
		
		directorySpinner = (Spinner) findViewById(R.id.directorySpinnerInfoTab);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, directoryList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		directorySpinner.setAdapter(adapter);
	}
	
	//update fantasy points each time a statistic is changed
	private void updateFantasy()
	{
		f = (TextView) findViewById(R.id.fantasy);
		Player currentPlayer = playerList.get(currentID);
		int fPoints = currentPlayer.getGoals() + currentPlayer.getAssists() + currentPlayer.getBlocks() - currentPlayer.getTurns();
		currentPlayer.setFantasy(fPoints);
		if(fPoints > 0) {
			f.setText("Fantasy: +" + fPoints);
			f.setTextColor(Color.BLUE);
		}
		else if(fPoints < 0) {
			f.setText("Fantasy: " + fPoints);
			f.setTextColor(Color.RED);
		}
		else {
			f.setText("Fantasy: 0");
			f.setTextColor(Color.BLACK);
		}
	}
	
	public void addDirectoryInfoTab(View view) {
		//newDirectoryText
		EditText newDirectoryEditText = (EditText) findViewById(R.id.newDirectoryText);
		String newDirectoryName = newDirectoryEditText.getText().toString();
		
		if (directoryList.contains(newDirectoryName)) {
			Toast.makeText(this, "Directory already exists", Toast.LENGTH_SHORT).show();
		}
		else 
		{
			directoryList.add(newDirectoryName);
			Collections.sort(directoryList);
			
			directorySpinner = (Spinner) findViewById(R.id.directorySpinnerInfoTab);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, directoryList);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			directorySpinner.setAdapter(adapter);
			directorySpinner.setSelection(directoryList.indexOf(newDirectoryName));
		}
		
		newDirectoryEditText.setText("");
	}
	
	public void createFileSaveData(View view) {
		
		
		EditText gameNameEditText = (EditText) findViewById(R.id.GameName);
		String gameName = gameNameEditText.getText().toString();
		
		if (gameName.matches("")) {
			Toast.makeText(this, "Enter a game name", Toast.LENGTH_SHORT).show();
			return;
		}
		if (directoryList.size()==0) {
			Toast.makeText(this, "Create a new folder", Toast.LENGTH_SHORT).show();
			return;
		}
		String selectedDir = directoryList.get(directorySpinner.getSelectedItemPosition());
		
		File sdCardRoot = Environment.getExternalStorageDirectory();
		File gameDir = new File(sdCardRoot, "Ultimate Stats/Directory/" + selectedDir);
		
		if (!gameDir.exists()) 
		{
			gameDir.mkdirs();
		}
		
		File gameFile = new File(gameDir, gameName + ".csv");
		
		if (gameFile.isFile()) {
			Toast.makeText(this, "File name already exists, enter different name", Toast.LENGTH_SHORT).show();
		}
		else {
			currFilePath = gameFile;
			
			writeCSV();
			
			gameNameEditText.setText("");
		}
	}
	
	public void updateData(View view) {
		// Check for whether currFilePath has been set
		if (!currFilePath.isFile()) {
			Toast.makeText(this, "Open INFO tab and edit file name before saving", Toast.LENGTH_SHORT).show();
		}
		else {
			writeCSV();
		}
		
	}
	// Writes statistics to CSV
	public void writeCSV(){
		
		try
		{
			currFilePath.createNewFile();
			CSVWriter csvWrite = new CSVWriter(new FileWriter(currFilePath));
			
			// Write Home/Opponent Scores
			String teamScoreHeader[] = {"Team", "Score"};
			String homeNameAndScore[] = {currHomeName, Integer.toString(homeScorePicker.getValue())};
			String opponentNameAndScore[] = {currOpponentName, Integer.toString(opponentScorePicker.getValue())};
			String blankLine[] = {};
			
			csvWrite.writeNext(teamScoreHeader);
			csvWrite.writeNext(homeNameAndScore);
			csvWrite.writeNext(opponentNameAndScore);
			csvWrite.writeNext(blankLine);

			// Write player statistics
			List<String[]> table = new ArrayList<String[]>();
			table.add(new String[] {"Player Name", "Goals", "Assists", "Blocks", "Turnovers", "Fantasy"});
			
			for (Player p : playerList) {
				String currLine[] = {
						p.getName(),
						Integer.toString(p.getGoals()),
						Integer.toString(p.getAssists()),
						Integer.toString(p.getBlocks()),
						Integer.toString(p.getTurns()),
						Integer.toString(p.getFantasy())
				};
				table.add(currLine);
			}
			
			csvWrite.writeAll(table);
			csvWrite.close();
			
			updateCurrentSavedPathTextView();
			
			Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}                
	}
	
	// Updates the text view in INFO tab with the file name
	private void updateCurrentSavedPathTextView() {
		currSavedPath = (TextView) findViewById(R.id.filePathTextView);
		
		if (currFilePath.isFile()) {
			String folder = currFilePath.getParentFile().getName();
					
			String text = "Saved as [" + folder + "/" + currFilePath.getName() + "]";
			
			currSavedPath.setText(text);
			currSavedPath.setTextColor(Color.BLUE);
		}
		else {
			// Make default game name Home vs Opponent
			currSavedPath.setTextColor(Color.RED);
			
			EditText defaultGameName = (EditText) findViewById(R.id.GameName); 
			defaultGameName.setText(currHomeName + " vs " + currOpponentName);
		}
		
	}
}
