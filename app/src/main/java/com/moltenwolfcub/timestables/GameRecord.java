package com.moltenwolfcub.timestables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_history")
public class GameRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int maxTable;
    private int totalQuestions;
    private double averageSpeed;
    private long timestamp;

    public GameRecord(int maxTable, int totalQuestions, double averageSpeed, long timestamp) {
        this.maxTable = maxTable;
        this.totalQuestions = totalQuestions;
        this.averageSpeed = averageSpeed;
        this.timestamp = timestamp;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMaxTable() { return maxTable; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getAverageSpeed() { return averageSpeed; }
    public long getTimestamp() { return timestamp; }
}
