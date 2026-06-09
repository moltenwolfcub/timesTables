package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.os.Bundle;
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

        game = getIntent().getParcelableExtra("game");

        questionText = findViewById(R.id.q);
        typedAnswer = findViewById(R.id.editTextNumber3);
        keypad = findViewById(R.id.keypad);

        typedAnswer.setShowSoftInputOnFocus(false);

        List<String> keys = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "⌫", "0", "");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        keypad.setLayoutManager(gridLayoutManager);

        KeypadAdapter adapter = new KeypadAdapter(keys, this::handleKeyInput);
        keypad.setAdapter(adapter);

        updateUI();
    }

    private void handleKeyInput(String key) {
        String currentText = typedAnswer.getText().toString();

        switch (key) {
            case "":
                break;
            case "⌫":
                if (!currentText.isEmpty()) {
                    typedAnswer.setText(currentText.substring(0, currentText.length() - 1));
                }
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
        typedAnswer.setText("");
        if (game.hasNextQuestion()) {
            game.nextQuestion();
            updateUI();
        } else {
            Toast.makeText(this, "Out of questions", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, DiscussionActivity.class);
//            intent.putExtra("game", game);
//            startActivity(intent);
        }
    }

    private void updateUI() {
        Question current = game.getCurrentQuestion();
        questionText.setText(current.first + " × " + current.second + " = ");
    }
}
