package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private RecyclerView keypad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        View rootView = findViewById(R.id.game_root);
        ThemeUtils.applySystemBarInsets(rootView);

        game = getIntent().getParcelableExtra("game");

        questionText = findViewById(R.id.q);
        typedAnswer = findViewById(R.id.editTextNumber3);
        keypad = findViewById(R.id.keypad);

        typedAnswer.setShowSoftInputOnFocus(false);

        List<String> keys = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "⌫", "0", "[C]");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        keypad.setLayoutManager(gridLayoutManager);

        KeypadAdapter adapter = new KeypadAdapter(keys, this::handleKeyInput);
        keypad.setAdapter(adapter);

        game.getCurrentQuestion().Start();
        updateUI();
    }

    private void handleKeyInput(String key) {
        String currentText = typedAnswer.getText().toString();

        switch (key) {
            case "⌫":
                if (!currentText.isEmpty()) {
                    typedAnswer.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;

            case "[C]":
                typedAnswer.setText("");
                break;

            default:
                typedAnswer.setText(currentText + key);
                if (Integer.parseInt(typedAnswer.getText().toString()) == game.getCurrentQuestion().Answer()) {
                    correctAnswer();
                }
                break;
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
        questionText.setText(current.first + " × " + current.second + " = ");
    }
}
