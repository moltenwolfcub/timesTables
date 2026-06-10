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
    private long timestamp;

    public GameRecord(String playerName, int maxTable, int totalQuestions, double averageSpeed, long timestamp) {
        this.playerName = playerName;
        this.maxTable = maxTable;
        this.totalQuestions = totalQuestions;
        this.averageSpeed = averageSpeed;
        this.timestamp = timestamp;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public int getMaxTable() { return maxTable; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getAverageSpeed() { return averageSpeed; }
    public long getTimestamp() { return timestamp; }
}
