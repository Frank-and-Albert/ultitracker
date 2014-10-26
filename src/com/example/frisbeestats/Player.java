package com.example.frisbeestats;

public class Player {
	
	private int goals;
	private int assists;
	private int blocks;
	private int turns;
	private int number;
	private int fantasy;
	private String name;
	
	// Constructor
	public Player() {
		number = 0;
		goals = 0;
		assists = 0;
		blocks = 0;
		turns = 0;
		fantasy = 0;
		name = "New Player";
	}
	
	// Setter methods
	public void setGoals(int g) {
		goals = g;
	}
	
	public void setAssists(int a) {
		assists = a;
	}
	
	public void setBlocks(int d) {
		blocks = d;
	}
	
	public void setTurns(int t) {
		turns = t;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setNumber(int n) {
		number = n;
	}
	
	public void setFantasy(int f) {
		fantasy = f;
	}
	
	// Getter methods
	public int getGoals() {
		return goals;
	}
	
	public int getAssists() {
		return assists;
	}
	
	public int getBlocks() {
		return blocks;
	}
	
	public int getTurns() {
		return turns;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumber() {
		return number;
	}

	public int getFantasy() {
		return fantasy;
	}

	
	
}
