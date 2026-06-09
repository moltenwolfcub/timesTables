package com.moltenwolfcub.timestables;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Question implements Parcelable {
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

    public static final Creator<Question> CREATOR = new Creator<Question>() {
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

    @NonNull
    @Override
    public String toString() {
        return this.first + " × " + this.second + " = " + this.Answer()+"; "+(Math.round((this.Duration()/1_000_000_000.0)*1000.0)/1000.0)+"s";
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
