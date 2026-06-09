package com.moltenwolfcub.timestables;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private Game game;

    private EditText typedAnswer;
    private RecyclerView keypad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = getIntent().getParcelableExtra("game");

        typedAnswer = findViewById(R.id.editTextNumber3);
        keypad = findViewById(R.id.keypad);

        typedAnswer.setShowSoftInputOnFocus(false);

        List<String> keys = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "⌫", "0", "Enter");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        keypad.setLayoutManager(gridLayoutManager);

        KeypadAdapter adapter = new KeypadAdapter(keys, this::handleKeyInput);
        keypad.setAdapter(adapter);

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

            case "Enter":
                String playerAnswer = typedAnswer.getText().toString();
                if (!playerAnswer.isEmpty()) {
                    Toast.makeText(this, "Submitted: " + playerAnswer, Toast.LENGTH_SHORT).show();
                    typedAnswer.setText(""); // Clear field for next question
                }
                break;

            default:
                typedAnswer.setText(currentText + key);
                break;
        }

        typedAnswer.setSelection(typedAnswer.getText().length());
    }

    private void updateUI() {
        Question current = game.getCurrentQuestion();
    }
}
