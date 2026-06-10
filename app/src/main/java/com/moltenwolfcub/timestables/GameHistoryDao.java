package com.moltenwolfcub.timestables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameHistoryDao {

    @Insert
    void insertGame(GameRecord record);

    @Query("SELECT * FROM game_history" +
            " WHERE timestamp >= :cuttoffTime" +
            " AND (:maxTable = 0 OR maxTable=:maxTable)" +
            " AND (:playerName = '' OR playerName LIKE :playerName || '%')" +
            " ORDER BY timestamp DESC")
    List<GameRecord> getGamesFiltered(long cuttoffTime, int maxTable, String playerName);

    @Query("SELECT COUNT(*) FROM game_history" +
            " WHERE timestamp >= :cutoffTime" +
            " AND (:maxTable = 0 OR maxTable=:maxTable)" +
            " AND (:playerName = '' OR playerName LIKE :playerName || '%')")
    int getGameCount(long cutoffTime, int maxTable, String playerName);

    @Query("SELECT AVG(averageSpeed) FROM game_history" +
            " WHERE timestamp >= :cutoffTime" +
            " AND (:maxTable = 0 OR maxTable=:maxTable)" +
            " AND (:playerName = '' OR playerName LIKE :playerName || '%')")
    double getAverageSpeed(long cutoffTime, int maxTable, String playerName);

    @Query("SELECT AVG(standardDeviation) FROM game_history" +
            " WHERE timestamp >= :cutoffTime" +
            " AND (:maxTable = 0 OR maxTable=:maxTable)" +
            " AND (:playerName = '' OR playerName LIKE :playerName || '%')")
    double getAverageSD(long cutoffTime, int maxTable, String playerName);

    @Query("SELECT MIN(averageSpeed) FROM game_history" +
            " WHERE timestamp >= :cutoffTime" +
            " AND (:maxTable = 0 OR maxTable = :maxTable)" +
            " AND (:playerName = '' OR playerName LIKE :playerName || '%')")
    double getBestMatchSpeed(long cutoffTime, int maxTable, String playerName);
}
