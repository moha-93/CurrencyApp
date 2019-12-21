package com.mycompany.exchangerateapp.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable {
    private String name;
    private String value;

    public Currency(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    protected Currency(Parcel in) {
        name = in.readString();
        value = in.readString();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(value);
    }
}
