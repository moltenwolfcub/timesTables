package com.moltenwolfcub.timestables;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.Random;

public class Question implements Parcelable {

    public static Question makeQuestion(Random rand, int maxTable) {
        int first = rand.nextInt(maxTable)+1;
        int second = rand.nextInt(maxTable)+1;
        return new Question(first,second);
    }

    public static Question makeQuestion(Random rand, int maxTable, int focusTable) {
        int first = rand.nextInt(maxTable)+1;
        return new Question(first,focusTable);
    }

    public int first;
    public int second;

    private long startTime;
    private long endTime;

    public Question(int firstNum, int secondNum) {
        first=firstNum;
        second=secondNum;
        startTime=-1;
        endTime=-1;
    }

    protected Question(Parcel in) {
        first = in.readInt();
        second = in.readInt();
        startTime = in.readLong();
        endTime = in.readLong();
    }

    public static final Creator<Question> CREATOR = new Creator<>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int Answer() {
        return first*second;
    }

    public void Start() {
        startTime = System.nanoTime();
    }
    public void End() {
        endTime = System.nanoTime();
    }

    public long Duration() {
        return endTime-startTime;
    }

    public static String formatDuration(Context ctx, long d) {
        return formatDuration(ctx, (double) d);
    }
    public static String formatDuration(Context ctx, double d) {
        DecimalFormat df = new DecimalFormat("#.###");
        return ctx.getString(R.string.question_duration, df.format(d/1_000_000_000.0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(first);
        dest.writeInt(second);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
    }
}
