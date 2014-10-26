package com.example.frisbeestats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVReader;

public class PreviewActivity extends Activity {
	
	private static File gameFile;
	private TableLayout table;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		
		String promptName = getIntent().getStringExtra("promptName");
		TextView prevTitle = (TextView) findViewById(R.id.previewTitle);
		prevTitle.setText(promptName);
		
		String filePath = getIntent().getStringExtra("game file name");
		gameFile = new File(filePath);
		
		fillTable(gameFile);
		
		
	}

	private void fillTable(File gameFile) {
		
		table = (TableLayout) findViewById(R.id.statTable);

		try {
			CSVReader reader = new CSVReader(new FileReader(gameFile));
			
			reader.readNext(); //Header
			
			TableRow homeRow = new TableRow(this);
			homeRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			String homeNameScore[] = reader.readNext();
			TextView homeName = (TextView) findViewById(R.id.homeTeamNameTextPreview);
			TextView homeScore = (TextView) findViewById(R.id.homeTeamScoreTextPreview);
			homeName.setText(homeNameScore[0]);
			homeScore.setText(homeNameScore[1]);
			
			String opponentNameScore[] = reader.readNext();
			TextView opponentName = (TextView) findViewById(R.id.opponentTeamNameTextPreview);
			TextView opponentScore = (TextView) findViewById(R.id.opponentTeamScoreTextPreview);
			opponentName.setText(opponentNameScore[0]);
			opponentScore.setText(opponentNameScore[1]);
			
			reader.readNext(); //Blank line
			reader.readNext(); //Header line
			
			String nextLine[];
			while ((nextLine = reader.readNext()) != null) {
				
				TableRow playerStatRow = new TableRow(this);
				playerStatRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				
				TextView playerName = new TextView(this);
				TextView goals = new TextView(this);
				TextView assists = new TextView(this);
				TextView blocks = new TextView(this);
				TextView turns = new TextView(this);
				TextView fantasy = new TextView(this);
				
				playerName.setText(nextLine[0]);
				goals.setText(nextLine[1]);
				assists.setText(nextLine[2]);
				blocks.setText(nextLine[3]);
				turns.setText(nextLine[4]);
				fantasy.setText(nextLine[5]);
				
				playerName.setPadding(3, 3, 3, 3);
				goals.setPadding(3, 3, 3, 3);
				assists.setPadding(3, 3, 3, 3);
				blocks.setPadding(3, 3, 3, 3);
				turns.setPadding(3, 3, 3, 3);
				fantasy.setPadding(3, 3, 3, 3);
				
				playerStatRow.addView(playerName);
				playerStatRow.addView(goals);
				playerStatRow.addView(assists);
				playerStatRow.addView(blocks);
				playerStatRow.addView(turns);
				playerStatRow.addView(fantasy);
				
				table.addView(playerStatRow);
			}
			reader.close();
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
		getMenuInflater().inflate(R.menu.activity_preview, menu);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		if (item.getItemId() == R.id.previewActivity_savedGamesButton) {
			Intent intent = new Intent(this, SavedGamesActivity.class);
			startActivity(intent);
	    }
		if (item.getItemId() == R.id.previewActivity_editGameButton) {
			editGame();
	    }
		return super.onOptionsItemSelected(item);
	}

	private void editGame() {
		
		File savedFile = gameFile;
		
		ArrayList<String> gameRoster = new ArrayList<String>();
		ArrayList<Player> playerList = new ArrayList<Player>();
		
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
		    	gameRoster.add(nextLine[0]);
		    	
		    	Player newPlayer = new Player();
		    	
		    	newPlayer.setName(nextLine[0]);
		    	newPlayer.setGoals(Integer.parseInt(nextLine[1]));
		    	newPlayer.setAssists(Integer.parseInt(nextLine[2]));
		    	newPlayer.setBlocks(Integer.parseInt(nextLine[3]));
		    	newPlayer.setTurns(Integer.parseInt(nextLine[4]));
		    	newPlayer.setFantasy(Integer.parseInt(nextLine[5]));
		    	
		    	playerList.add(newPlayer);
		    }
		    reader.close();
		    
		    Intent intent = new Intent(this, AddStat.class);
		    
		    AddStat.currFilePath = savedFile;
		    AddStat.currHomeName = savedGameHomeName;
		    AddStat.currHomeScore = savedGameHomeScore;
		    AddStat.currOpponentName = savedGameOpponentName;
		    AddStat.currOpponentScore = savedGameOpponentScore;
		    AddStat.playerListFromSavedGame = playerList;
		    
		    intent.putStringArrayListExtra("savedGameRosterFromStartScreen", gameRoster);
		    
			startActivity(intent);
		    
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
