package com.moltenwolfcub.timestables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_history")
public class GameRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String playerName;
    private final int maxTable;
    private final int totalQuestions;
    private final double averageSpeed;
    private final double standardDeviation;
    private final long timestamp;

    private final int rosette1;
    private final int rosette2;
    private final int rosette3;

    public GameRecord(String playerName, int maxTable, int totalQuestions, double averageSpeed, double standardDeviation, long timestamp, int rosette1, int rosette2, int rosette3) {
        this.playerName = playerName;
        this.maxTable = maxTable;
        this.totalQuestions = totalQuestions;
        this.averageSpeed = averageSpeed;
        this.standardDeviation = standardDeviation;
        this.timestamp = timestamp;
        this.rosette1 = rosette1;
        this.rosette2 = rosette2;
        this.rosette3 = rosette3;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public int getMaxTable() { return maxTable; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getAverageSpeed() { return averageSpeed; }
    public double getStandardDeviation() { return standardDeviation; }
    public long getTimestamp() { return timestamp; }
    public int getRosette1() { return rosette1; }
    public int getRosette2() { return rosette2; }
    public int getRosette3() { return rosette3; }
}
