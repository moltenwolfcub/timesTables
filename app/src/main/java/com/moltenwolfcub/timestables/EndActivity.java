package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.os.Bundle;
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

    private TextView maxTable;
    private TextView questionCount;
    private Button done;
    private TextView results;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        game = getIntent().getParcelableExtra("game");

        maxTable = findViewById(R.id.tableMax);
        questionCount = findViewById(R.id.questionCount);
        done = findViewById(R.id.done);
        results = findViewById(R.id.results);

        maxTable.setText(""+game.MaxTable());
        questionCount.setText(""+game.QuestionCount());

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
    }
}
