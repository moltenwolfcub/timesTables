package com.moltenwolfcub.timestables;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {GameRecord.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract GameHistoryDao gameHistoryDao();

    private static volatile AppDatabase INSTANCE;


    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "times_tables_database")
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_6)
                            .addMigrations(MIGRATION_6_7)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE game_history ADD COLUMN playerName TEXT DEFAULT 'Guest'");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE game_history ADD COLUMN standardDeviation REAL NOT NULL DEFAULT 0.0");
        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE game_history ADD COLUMN rosette1 int NOT NULL DEFAULT -1");
            database.execSQL("ALTER TABLE game_history ADD COLUMN rosette2 int NOT NULL DEFAULT -1");
            database.execSQL("ALTER TABLE game_history ADD COLUMN rosette3 int NOT NULL DEFAULT -1");
        }
    };
    static final Migration MIGRATION_4_6 = new Migration(4, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // empty on purpose. did a bad migration here originally
        }
    };
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("UPDATE game_history SET rosette1=-15681151 WHERE averageSpeed <  1000000000");
            database.execSQL("UPDATE game_history SET rosette1=-13058568 WHERE averageSpeed >= 1000000000 AND averageSpeed < 1500000000");
            database.execSQL("UPDATE game_history SET rosette1=-14326805 WHERE averageSpeed >= 1500000000 AND averageSpeed < 3000000000");
            database.execSQL("UPDATE game_history SET rosette1=- 7036488 WHERE averageSpeed >= 3000000000 AND averageSpeed < 5000000000");

            database.execSQL("UPDATE game_history SET rosette3=-15681151 WHERE totalQuestions >= 20 AND standardDeviation <  1500000000");
            database.execSQL("UPDATE game_history SET rosette3=-13058568 WHERE totalQuestions >= 20 AND standardDeviation >= 1500000000 AND standardDeviation <  3500000000");
            database.execSQL("UPDATE game_history SET rosette3=-14326805 WHERE totalQuestions >= 20 AND standardDeviation >= 3500000000 AND standardDeviation <  6000000000");
            database.execSQL("UPDATE game_history SET rosette3=- 7036488 WHERE totalQuestions >= 20 AND standardDeviation >= 6000000000 AND standardDeviation < 12000000000");
        }
    };
}
