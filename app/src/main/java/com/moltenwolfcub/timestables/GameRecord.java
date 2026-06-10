package com.moltenwolfcub.timestables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_history")
public class GameRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String playerName;
    private int maxTable;
    private int totalQuestions;
    private double averageSpeed;
    private double standardDeviation;
    private long timestamp;

    public GameRecord(String playerName, int maxTable, int totalQuestions, double averageSpeed, double standardDeviation, long timestamp) {
        this.playerName = playerName;
        this.maxTable = maxTable;
        this.totalQuestions = totalQuestions;
        this.averageSpeed = averageSpeed;
        this.standardDeviation = standardDeviation;
        this.timestamp = timestamp;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public int getMaxTable() { return maxTable; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getAverageSpeed() { return averageSpeed; }
    public double getStandardDeviation() { return standardDeviation; }
    public long getTimestamp() { return timestamp; }
}
