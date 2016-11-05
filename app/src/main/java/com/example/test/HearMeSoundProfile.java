package com.example.test;

import android.app.Application;

/**
 * Created by USER on 5/14/2016.
 */
public class HearMeSoundProfile extends Application {
    private String leftHeadsetVolumeLevel = null;
    private String rightHeadsetVolumeLevel = null;
    private String headsetProfileID = null;

    public String getheadsetProfileID() {

        return headsetProfileID;
    }

    public void setheadsetProfileID(String headsetProfileID) {

        headsetProfileID = headsetProfileID;

    }

    public String getleftHeadsetVolumeLevel() {

        return leftHeadsetVolumeLevel;
    }

    public void setrightHeadsetVolumeLevel(String rightHeadsetVolumeLevel) {

        rightHeadsetVolumeLevel = rightHeadsetVolumeLevel;
    }

    public String getrightHeadsetVolumeLevel() {
        return rightHeadsetVolumeLevel;
    }

    public void setleftHeadsetVolumeLevel(String leftHeadsetVolumeLevel) {

        leftHeadsetVolumeLevel = leftHeadsetVolumeLevel;
    }


}
