package com.moltenwolfcub.timestables;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Question implements Parcelable {
    public int first;
    public int second;

    public Question(int firstNum, int secondNum) {
        first=firstNum;
        second=secondNum;
    }

    protected Question(Parcel in) {
        first = in.readInt();
        second = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(first);
        dest.writeInt(second);
    }
}
