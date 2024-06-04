package com.anter.ToDo;

import android.widget.Button;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Card implements Serializable {
    public static final int CARD_TYPE_1 = 1;
    public static final int CARD_TYPE_2 = 2;
    public static final int CARD_TYPE_3 = 3;
    public static final int CARD_TYPE_4 = 4;
    protected String header;
    protected boolean starred;
    protected boolean isInRegularTab;
    protected transient Button tab;
    protected int cardType;
    //cards 1, 3, 4:
    private final List<Row> allRows;
    //cards 2, 3:
    private int desiredScore;
    //card 2:
    private int currentScore;
    //card 1:
    private Schedule schedule;
    private int tabIndexForSaving;
    private boolean isCardScheduleTabHasDeleted;


    public Card(Card other) {
        this.header = other.header;
        this.starred = other.starred;
        this.isInRegularTab = other.isInRegularTab;
        this.tab = other.tab;
        this.cardType = other.cardType;
        this.allRows = new ArrayList<>(other.allRows.size());
        for (Row row : other.allRows) {
            this.allRows.add(new Row(row));
        }
        this.desiredScore = other.desiredScore;
        this.currentScore = other.currentScore;
        this.schedule = other.schedule;
        this.isCardScheduleTabHasDeleted = other.isCardScheduleTabHasDeleted;
    }

    public Card(Button tab, int cardType) {
        isInRegularTab = true;
        this.tab = tab;
        this.cardType = cardType;
        List<Row> temp = null;
        switch (cardType) {
            case CARD_TYPE_1:
            case CARD_TYPE_3:
            case CARD_TYPE_4:
                temp = new ArrayList<>();
        }
        this.allRows = temp;
    }

    public void setTab(Button tab) {
        this.tab = tab;
    }

    public void addRow() {
        switch (cardType) {
            case CARD_TYPE_1:
            case CARD_TYPE_3:
            case CARD_TYPE_4:
                this.allRows.add(new Row(cardType));
        }
    }

    public List<Row> getCurrentDisplayedRows() {
        switch (cardType) {
            case CARD_TYPE_1:
                if (isInRegularTab) return allRows;
                return this.allRows.stream().filter(Row::isStarred).collect(Collectors.toList());
            case CARD_TYPE_3:
            case CARD_TYPE_4:
                return allRows;
            default:
                return null;
        }
    }

    public int getCardType() {
        return cardType;
    }

    public List<Row> getAllRows() {
        switch (cardType) {
            case CARD_TYPE_1:
            case CARD_TYPE_3:
            case CARD_TYPE_4:
                return allRows;
            default:
                return null;
        }
    }


    public Button getTab() {
        return tab;
    }

    public boolean isOkTOAddToStarred() {
        switch (cardType) {
            case CARD_TYPE_1:
                this.isInRegularTab = starred;
                return starred || this.allRows.stream().anyMatch(Row::isStarred);
            case CARD_TYPE_3:
            case CARD_TYPE_2:
            case CARD_TYPE_4:
                return starred;
            default:
                return false;
        }
    }

    public void switchToAll() {
        isInRegularTab = true;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public int getCurrentScore() {
        switch (cardType) {
            case CARD_TYPE_2:
                return currentScore;
            case CARD_TYPE_3:
                return this.allRows.stream().mapToInt(Row::getValue).sum();
            case CARD_TYPE_4:
                return allRows.stream().filter(Row::isChecked).mapToInt(Row::getValue).sum();
            default:
                return -1;
        }
    }

    public void setCurrentScore(int currentScore) {
        if (cardType == CARD_TYPE_2) {
            this.currentScore = currentScore;
        }
    }

    public int getDesiredScore() {
        switch (cardType) {
            case CARD_TYPE_2:
            case CARD_TYPE_3:
                return desiredScore;
            case CARD_TYPE_4:
                return allRows.stream().mapToInt(Row::getValue).sum();
            default:
                return -1;
        }
    }

    public void setDesiredScore(int desiredScore) {
        switch (cardType) {
            case CARD_TYPE_2:
            case CARD_TYPE_3:
                this.desiredScore = desiredScore;
        }
    }

    public Schedule getSchedule() {
        return schedule;

    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public int getTabIndexForSaving() {
        return tabIndexForSaving;
    }

    public void setTabIndexForSaving(int tabIndexForSaving) {
        isCardScheduleTabHasDeleted = tabIndexForSaving == -1;
        if (tabIndexForSaving != -1)
            this.tabIndexForSaving = tabIndexForSaving;
    }

    public boolean isCardScheduleTabHasDeleted() {
        return isCardScheduleTabHasDeleted;
    }

    public LocalDateTime getDateTimeOrder() {
        if (getSchedule().isRepeat()) {
            return getSchedule().getStartDate().atTime(4, 0);
        }
        return getSchedule().getScheduledDateTime();
    }


}
