package com.moltenwolfcub.timestables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameHistoryDao {

    @Insert
    void insertGame(GameRecord record);

    @Query("SELECT * FROM game_history WHERE timestamp >= :cuttoffTime AND (:maxTable = 0 OR maxTable=:maxTable) ORDER BY timestamp DESC")
    List<GameRecord> getGamesFiltered(long cuttoffTime, int maxTable);

    @Query("SELECT COUNT(*) FROM game_history WHERE timestamp >= :cutoffTime AND (:maxTable = 0 OR maxTable=:maxTable)")
    int getGameCount(long cutoffTime, int maxTable);

    @Query("SELECT AVG(averageSpeed) FROM game_history WHERE timestamp >= :cutoffTime AND (:maxTable = 0 OR maxTable=:maxTable)")
    double getAverageSpeed(long cutoffTime, int maxTable);

    @Query("SELECT MIN(averageSpeed) FROM game_history WHERE timestamp >= :cutoffTime AND (:maxTable = 0 OR maxTable = :maxTable)")
    double getBestMatchSpeed(long cutoffTime, int maxTable);
}
