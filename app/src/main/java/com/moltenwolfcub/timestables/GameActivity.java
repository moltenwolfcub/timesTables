package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private Game game;

    private TextView questionText;
    private EditText typedAnswer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        View rootView = findViewById(R.id.game_root);
        ThemeUtils.applySystemBarInsets(rootView);

        game = getIntent().getParcelableExtra("game");

        questionText = findViewById(R.id.q);
        typedAnswer = findViewById(R.id.editTextNumber3);
        RecyclerView keypad = findViewById(R.id.keypad);

        Button btnExitGame = findViewById(R.id.btn_exit_game);
        if (game.getGameMode().hasExitButton()) {
            btnExitGame.setVisibility(View.VISIBLE);

            btnExitGame.setOnClickListener(v -> {
                game.FinishEarly();
                Intent intent = new Intent(this, EndActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("game", game);
                startActivity(intent);
                finish();
            });
        } else {
            btnExitGame.setVisibility(View.GONE);
        }

        typedAnswer.setShowSoftInputOnFocus(false);

        List<String> keys = Arrays.asList(
                getString(R.string.game_keypad_1),
                getString(R.string.game_keypad_2),
                getString(R.string.game_keypad_3),
                getString(R.string.game_keypad_4),
                getString(R.string.game_keypad_5),
                getString(R.string.game_keypad_6),
                getString(R.string.game_keypad_7),
                getString(R.string.game_keypad_8),
                getString(R.string.game_keypad_9),
                getString(R.string.game_keypad_delete),
                getString(R.string.game_keypad_0),
                getString(R.string.game_keypad_clear)
        );

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        keypad.setLayoutManager(gridLayoutManager);

        KeypadAdapter adapter = new KeypadAdapter(keys, this::handleKeyInput);
        keypad.setAdapter(adapter);


        game.getCurrentQuestion().Start();
        updateUI();
    }

    private void handleKeyInput(String key) {
        String currentText = typedAnswer.getText().toString();

        if (key.equals(getString(R.string.game_keypad_delete))) {
            if (!currentText.isEmpty()) {
                typedAnswer.setText(currentText.substring(0, currentText.length() - 1));
            }

        } else if (key.equals(getString(R.string.game_keypad_clear))) {
            typedAnswer.setText("");

        } else {
            String answer = currentText+key;
            typedAnswer.setText(answer);
            if (Integer.parseInt(typedAnswer.getText().toString()) == game.getCurrentQuestion().Answer()) {
                correctAnswer();
            }
        }

        typedAnswer.setSelection(typedAnswer.getText().length());
    }

    private void correctAnswer() {
        game.getCurrentQuestion().End();
        typedAnswer.setText("");
        if (game.hasNextQuestion()) {
            game.nextQuestion();
            game.getCurrentQuestion().Start();
            updateUI();
        } else {
            Intent intent = new Intent(this, EndActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("game", game);
            startActivity(intent);
            finish();
        }
    }

    private void updateUI() {
        Question current = game.getCurrentQuestion();
        questionText.setText(getString(R.string.game_question, current.first, current.second));
    }
}
