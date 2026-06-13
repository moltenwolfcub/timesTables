package com.moltenwolfcub.timestables;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Random;

public class Game implements Parcelable {
    private final Random random;
    private final GameMode gameMode;
    private final String playerName;
    private final int maxTable;
    private final int focusTable;
    private final List<Question> questions;
    private int index = 0;

    public Game(Random rand, GameMode gameMode, String playerName, List<Question> questions, int maxTable, int focusTable) {
        this.random = rand;
        this.gameMode = gameMode;
        this.playerName = playerName;
        this.questions = questions;
        this.maxTable = maxTable;
        this.focusTable = focusTable;
    }

    public Question getCurrentQuestion() {
        switch (gameMode) {
            case REGULAR:
                return questions.get(index);

            case FOCUS:
                if (questions.isEmpty()) {
                    this.addFocusQuestion();
                }
                return questions.get(questions.size() - 1);

            default:
                throw new IllegalArgumentException();
        }
    }

    private void addFocusQuestion() {
        Question q = Question.makeQuestion(random, maxTable, focusTable);
        this.questions.add(q);
    }

    public boolean hasNextQuestion() {
        switch (gameMode) {
            case REGULAR:
                return index < questions.size() - 1;
            case FOCUS:
                return true;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void nextQuestion() {
        switch (gameMode) {
            case REGULAR:
                if (hasNextQuestion()) {
                    index++;
                }
                break;
            case FOCUS:
                addFocusQuestion();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public List<Question> GetQuestions() {
        return questions;
    }

    public int QuestionCount() {
        return questions.size();
    }

    public int MaxTable() {
        return maxTable;
    }

    public String GetPlayerName() {
        return playerName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void FinishEarly() {
        if (!getGameMode().isFinite()) {
            questions.remove(questions.size() - 1);
        }
    }

    protected Game(Parcel in) {
        random = (Random) in.readSerializable();
        gameMode = (GameMode) in.readSerializable();
        playerName = in.readString();
        maxTable = in.readInt();
        focusTable = in.readInt();
        questions = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<Game> CREATOR = new Creator<>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(random);
        dest.writeSerializable(gameMode);
        dest.writeString(playerName);
        dest.writeInt(maxTable);
        dest.writeInt(focusTable);
        dest.writeTypedList(questions);
    }

    public enum GameMode {
        REGULAR(true, true, false),
        FOCUS(false, false, true);

        private final boolean finite;
        private final boolean inHistory;
        private final boolean hasExitButton;

        GameMode(boolean finite, boolean history, boolean exit) {
            this.finite = finite;
            this.inHistory = history;
            this.hasExitButton = exit;
        }

        public boolean isFinite() {
            return finite;
        }

        public boolean shouldStore() {
            return inHistory;
        }

        public boolean hasExitButton() {
            return hasExitButton;
        }
    }
}
