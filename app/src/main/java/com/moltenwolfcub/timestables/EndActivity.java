package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndActivity extends AppCompatActivity {
    private Game game;

    private TextView playerName;
    private TextView maxTable;
    private TextView questionCount;
    private TextView speed;
    private Button done;
    private TextView results;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        View rootView = findViewById(R.id.end_root);
        ThemeUtils.applySystemBarInsets(rootView);

        game = getIntent().getParcelableExtra("game");

        playerName = findViewById(R.id.playerName);
        maxTable = findViewById(R.id.tableMax);
        questionCount = findViewById(R.id.questionCount);
        speed = findViewById(R.id.speed);
        done = findViewById(R.id.done);
        results = findViewById(R.id.results);

        playerName.setText(game.GetPlayerName());
        maxTable.setText(String.valueOf(game.MaxTable()));
        questionCount.setText(String.valueOf(game.QuestionCount()));

        long sum = 0;
        for (int i = 0; i<game.GetQuestions().size();i++) {
            sum+= game.GetQuestions().get(i).Duration();
        }
        double avg = (double) sum /game.QuestionCount();
        speed.setText(Question.formatDuration(avg));

        StringBuilder questionResults = new StringBuilder();
        for (int i = 0; i<game.GetQuestions().size();i++) {
            questionResults.append(game.GetQuestions().get(i).toString()).append("\n");
        }

        results.setText(questionResults.toString());

        done.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("game", game);

            startActivity(intent);
            finish();
        });
        if (game.ShouldStore()) {
            saveResults(game.GetPlayerName(), game.MaxTable(), game.QuestionCount(), avg);
        }
    }

    private void saveResults(String playerName, int maxTable, int totalQuestions, double avgSpeed) {
        AppDatabase db = AppDatabase.getDatabase(this);

        GameRecord newRecord = new GameRecord(
                playerName,
                maxTable,
                totalQuestions,
                avgSpeed,
                System.currentTimeMillis()
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.gameHistoryDao().insertGame(newRecord);
        });
    }
}
