package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Random rand;

    private EditText maxTable;
    private EditText questionCountInput;
    private EditText playerName;

    private EditText infiniteTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootView = findViewById(R.id.main_root);
        ThemeUtils.applySystemBarInsets(rootView);

        maxTable = findViewById(R.id.editTextNumber);
        questionCountInput = findViewById(R.id.editTextNumber2);
        playerName = findViewById(R.id.et_player_name);
        Button go = findViewById(R.id.go);
        Button history = findViewById(R.id.btn_view_history);
        infiniteTarget = findViewById(R.id.et_infinite_focus_target);
        Button startInfinite = findViewById(R.id.btn_start_infinite);

        rand = new Random();

        Game prevGame = getIntent().getParcelableExtra("game");
        if (prevGame != null) {
            maxTable.setText(String.valueOf(prevGame.MaxTable()));

            if (prevGame.getGameMode().isFinite()) {
                questionCountInput.setText(String.valueOf(prevGame.QuestionCount()));
            }

            String lastName = prevGame.GetPlayerName();
            playerName.setText(Objects.equals(lastName, "Guest") ? "" : lastName);
        }

        go.setOnClickListener(view -> {
            int maxTableValue, questionCount;
            try {
                String val = maxTable.getText().toString().trim();
                if (val.isEmpty()) {
                    Toast.makeText(this, "Enter a value for Max Table", Toast.LENGTH_SHORT).show();
                    return;
                }
                maxTableValue = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Max table should be a number", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                String val = questionCountInput.getText().toString().trim();
                if (val.isEmpty()) {
                    Toast.makeText(this, "Enter a value for # of Questions", Toast.LENGTH_SHORT).show();
                    return;
                }
                questionCount = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Question Count should be a number", Toast.LENGTH_SHORT).show();
                return;
            }
            String currentPlayerName = playerName.getText().toString().trim().isEmpty() ? "Guest" : playerName.getText().toString().trim().toLowerCase(Locale.ROOT);

            List<Question> questionSet = new ArrayList<>();
            for (int i = 0; i < questionCount; i++) {
                questionSet.add(Question.makeQuestion(rand, maxTableValue));
            }
            Game game = new Game(rand, Game.GameMode.REGULAR, currentPlayerName, questionSet, maxTableValue, 0);

            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("game", game);

            startActivity(intent);
        });
        history.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));

        startInfinite.setOnClickListener(view -> {
            int maxTableValue, focusValue;
            try {
                String val = maxTable.getText().toString().trim();
                if (val.isEmpty()) {
                    Toast.makeText(this, "Enter a value for Max Table", Toast.LENGTH_SHORT).show();
                    return;
                }
                maxTableValue = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Max table should be a number", Toast.LENGTH_SHORT).show();
                return;
            }
            String currentPlayerName = playerName.getText().toString().trim().isEmpty() ? "Guest" : playerName.getText().toString().trim();
            try {
                String val = infiniteTarget.getText().toString().trim();
                if (val.isEmpty()) {
                    Toast.makeText(this, "Enter a value for Focus Table Target", Toast.LENGTH_SHORT).show();
                    return;
                }
                focusValue = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Focus Table Target should be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            Game game = new Game(rand, Game.GameMode.FOCUS, currentPlayerName, new ArrayList<>(), maxTableValue, focusValue);

            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("game", game);

            startActivity(intent);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            EditText[] fields = {maxTable, questionCountInput, playerName, infiniteTarget};
            for (EditText et : fields) {
                if (et != null && et.hasFocus()) {
                    Rect outRect = new Rect();
                    et.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        et.clearFocus();

                        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (manager != null) {
                            manager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        }
                    }
                    break;
                }

            }
        }

        return super.dispatchTouchEvent(event);
    }
}