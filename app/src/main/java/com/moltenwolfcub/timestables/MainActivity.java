package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Random rand;

    private EditText maxTable;
    private EditText questionCountInput;
    private EditText playerName;
    private Button go;

    private Button history;

    private int maxTableValue;
    private int questionCount;
    private String currentPlayerName = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View rootView = findViewById(R.id.main_root);
        ThemeUtils.applySystemBarInsets(rootView);

        maxTable = findViewById(R.id.editTextNumber);
        questionCountInput = findViewById(R.id.editTextNumber2);
        playerName = findViewById(R.id.et_player_name);
        go = findViewById(R.id.go);
        history = findViewById(R.id.btn_view_history);

        rand = new Random();

        Game prevGame = getIntent().getParcelableExtra("game");
        if (prevGame != null) {
            maxTableValue = prevGame.MaxTable();
            questionCount = prevGame.QuestionCount();
            currentPlayerName = prevGame.GetPlayerName();
        } else {
            maxTableValue = 1;
            questionCount = 1;
            currentPlayerName = "Guest";
        }
        maxTable.setText(String.valueOf(maxTableValue));
        questionCountInput.setText(String.valueOf(questionCount));
        playerName.setText(currentPlayerName);

        maxTable.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                maxTableValue = Integer.parseInt(maxTable.getText().toString());
                return true;
            }
            return false;
        });

        questionCountInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                questionCount = Integer.parseInt(questionCountInput.getText().toString());
                return true;
            }
            return false;
        });

        playerName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String input = playerName.getText().toString().trim();
                currentPlayerName = input.isEmpty() ? "Guest" : input;
                return true;
            }
            return false;
        });

        go.setOnClickListener(view -> {
            List<Question> questionSet = new ArrayList<>();
            for (int i = 0; i < questionCount; i++) {
                int first = rand.nextInt(maxTableValue)+1;
                int second = rand.nextInt(maxTableValue)+1;
                questionSet.add(new Question(first,second));
            }
            Game game = new Game(currentPlayerName, questionSet, maxTableValue);

            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("game", game);

            startActivity(intent);
        });
        history.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (maxTable.hasFocus()) {
                Rect outRect = new Rect();
                maxTable.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    maxTableValue = Integer.parseInt(maxTable.getText().toString());
                    maxTable.clearFocus();

                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(maxTable.getWindowToken(), 0);
                    }
                }
            }
            if (questionCountInput.hasFocus()) {
                Rect outRect = new Rect();
                questionCountInput.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    questionCount = Integer.parseInt(questionCountInput.getText().toString());
                    questionCountInput.clearFocus();

                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(questionCountInput.getWindowToken(), 0);
                    }
                }
            }
            if (playerName.hasFocus()) {
                Rect outRect = new Rect();
                playerName.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    currentPlayerName = playerName.getText().toString().trim().isEmpty() ? "Guest" : playerName.getText().toString().trim();
                    playerName.clearFocus();

                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(playerName.getWindowToken(), 0);
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }
}