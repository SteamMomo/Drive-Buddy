package com.release.drivebuddy;

import android.util.Log;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    boolean flag = false;
    String number = null;

    public boolean isFlag() {
        return flag;
    }

    public String getNumber() {
        return number;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setNumber(String number) {
        this.number = number;
        Log.wtf("number", "number = " + number);
    }
}
