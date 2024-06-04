package com.anter.ToDo;

public class Row {
    public static final int ROW_TYPE_1 = 1;
    public static final int ROW_TYPE_3 = 3;
    public static final int ROW_TYPE_4 = 4;
    //all:
    private final int rowType;
    //rows 1, 3, 4:
    private String text;
    //rows 3, 4:
    private int value;
    //rows 1, 3:
    private boolean isChecked;
    //row 1:
    private boolean isStarred;

    public Row(Row row) {
        this.rowType = row.rowType;
        this.text = row.text;
        this.value = row.value;
        this.isChecked = row.isChecked;
        this.isStarred = row.isStarred;
    }


    public Row(int rowType){
        this.rowType = rowType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        switch (rowType){
            case ROW_TYPE_1:
            case ROW_TYPE_4:
                return isChecked;
            default:
                return false;
        }
    }

    public void setChecked(boolean checked) {
        switch (rowType) {
            case ROW_TYPE_1:
            case ROW_TYPE_4:
                isChecked = checked;
        }
    }
    public int getValue(){
        switch (rowType) {
            case ROW_TYPE_3:
            case ROW_TYPE_4:
                return value;
            default:
                return -1;
        }
    }
    public void setValue(int value){
        switch (rowType) {
            case ROW_TYPE_3:
            case ROW_TYPE_4:
                this.value = value;
        }
    }

    public boolean isStarred() {
        if (rowType == ROW_TYPE_1)
            return isStarred;

        return false;
    }

    public void setStarred(boolean starred) {
        if (rowType == ROW_TYPE_1)
            isStarred = starred;
    }
}
