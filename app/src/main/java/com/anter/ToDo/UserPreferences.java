package com.anter.ToDo;

import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

public class UserPreferences {
    private int darkModeStatus = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    private int layoutDirectionStatus = View.LAYOUT_DIRECTION_LOCALE;
    private boolean dynamic = true;
    private String language = "";
    private boolean scheduleRepeat;
    private int lastTabShownIndex;
    public UserPreferences(){

    }

    public UserPreferences(UserPreferences otherUserPreferences) {
        this.darkModeStatus = otherUserPreferences.darkModeStatus;
        this.layoutDirectionStatus = otherUserPreferences.layoutDirectionStatus;
        this.dynamic = otherUserPreferences.dynamic;
        this.language = otherUserPreferences.language;
        this.lastTabShownIndex = otherUserPreferences.lastTabShownIndex;
    }

    public boolean isScheduleRepeat() {
        return scheduleRepeat;
    }

    public void setScheduleRepeat(boolean scheduleRepeat) {
        this.scheduleRepeat = scheduleRepeat;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getDarkModeStatus() {
        return darkModeStatus;
    }

    public void setDarkModeStatus(int darkModeStatus) {
        this.darkModeStatus = darkModeStatus;
    }

    public int getLayoutDirectionStatus() {
        return layoutDirectionStatus;
    }

    public void setLayoutDirectionStatus(int layoutDirectionStatus) {
        this.layoutDirectionStatus = layoutDirectionStatus;
    }
    public void setLanguageAndDirection(int layoutDirectionStatus){
        this.layoutDirectionStatus = layoutDirectionStatus;
        if(layoutDirectionStatus==View.LAYOUT_DIRECTION_LTR)
            setLanguage("en");
        else if (layoutDirectionStatus==View.LAYOUT_DIRECTION_RTL)
            setLanguage("iw");
        else
            setLanguage("");

    }

    public int getLastTabShownIndex() {
        return lastTabShownIndex;
    }

    public void setLastTabShownIndex(int lastTabShownIndex) {
        this.lastTabShownIndex = lastTabShownIndex;
        System.out.println("lastTabShownIndex: " + lastTabShownIndex);
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean isDynamic) {
        this.dynamic = isDynamic;
    }
}
