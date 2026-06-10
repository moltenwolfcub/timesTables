package com.moltenwolfcub.timestables;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Game implements Parcelable {
    private final String playerName;
    private final int maxTable;
    private final List<Question> questions;
    private int index = 0;

    public Game(String playerName, List<Question> questions, int maxTable) {
        this.playerName = playerName;
        this.questions = questions;
        this.maxTable = maxTable;
    }

    public Question getCurrentQuestion() {
        return questions.get(index);
    }

    public boolean hasNextQuestion() {
        return index < questions.size() - 1;
    }

    public Question nextQuestion() {
        if (hasNextQuestion()) {
            index++;
            return questions.get(index);
        }
        return null;
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

    protected Game(Parcel in) {
        playerName = in.readString();
        maxTable = in.readInt();
        questions = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
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
        dest.writeString(playerName);
        dest.writeInt(maxTable);
        dest.writeTypedList(questions);
    }
}
