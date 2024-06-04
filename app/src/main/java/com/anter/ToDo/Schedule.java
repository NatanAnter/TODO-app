package com.anter.ToDo;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Schedule implements Serializable {
    private boolean repeat;
    private String scheduledDateTime;
    private Times repeatOption;
    private int step;
    private boolean[] daysInWeek;
    private String startDate;
    private String endDate;
    private String showTimeInDay;

    public Schedule(LocalDateTime scheduledDateTime) {
        this.repeat = false;
        this.scheduledDateTime = scheduledDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));//todo datetime
    }

    public Schedule(Times repeatOption, int step, boolean[] daysInWeek, LocalDate startDate, LocalDate endDate, LocalTime showTimeInDay) {
        this.repeat = true;
        this.repeatOption = repeatOption;
        this.step = step;
        this.daysInWeek = daysInWeek;
        this.startDate = startDate.format(ScheduleDialog.dateFormat);
        this.endDate = endDate.format(ScheduleDialog.dateFormat);
        this.showTimeInDay = showTimeInDay.toString();
    }

    public void adjustRealStartDate(LocalDate now) {
        if(now.isAfter(LocalDate.parse(startDate,ScheduleDialog.dateFormat))||now.isEqual(LocalDate.parse(startDate,ScheduleDialog.dateFormat))){
            startDate = LocalDate.now().plusDays(1).format(ScheduleDialog.dateFormat);
        }
        switch (repeatOption) {
            case EVERY_DAY:
                break;
            case SPECIFIC_DAYS_IN_WEEK:
                int dayInTheWeek = LocalDate.parse(startDate, ScheduleDialog.dateFormat).getDayOfWeek().getValue() % 7;
                int daysToGoForward = calculateDistanceToNextDay((dayInTheWeek + 6) % 7)-1;
                startDate = LocalDate.parse(startDate, ScheduleDialog.dateFormat).plusDays(daysToGoForward).format(ScheduleDialog.dateFormat);
                break;
            case EVERY_MONTH:
                int daysInMonth = Math.min(step, LocalDate.parse(startDate, ScheduleDialog.dateFormat).lengthOfMonth());
                startDate = LocalDate.of(LocalDate.parse(startDate, ScheduleDialog.dateFormat).getYear(), LocalDate.parse(startDate, ScheduleDialog.dateFormat).getMonth(), daysInMonth).format(ScheduleDialog.dateFormat);
                break;
            default:
                throw new IllegalArgumentException("Invalid repeat option");
        }
    }

    public boolean isTimePast(LocalDateTime now) {
        if (repeat)
            return now.isAfter(LocalDate.parse(endDate, ScheduleDialog.dateFormat).atTime(LocalTime.parse(showTimeInDay)));
        return false;
    }

    public boolean isTimeHasBegan(LocalDateTime now) {
        if (repeat)
            return now.isAfter(LocalDate.parse(startDate, ScheduleDialog.dateFormat).atTime(LocalTime.parse(showTimeInDay)));
        return false;
    }

    private boolean isNeedToShownNonRepeat(LocalDateTime now) {
        if (repeat)
            return false;
        return now.isAfter(LocalDateTime.parse(scheduledDateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

    }
    public boolean isNeedToShown(LocalDateTime now){
        if(isRepeat())
            return isNeedToShownRepeat(now);
        return isNeedToShownNonRepeat(now);

    }

    private boolean isNeedToShownRepeat(LocalDateTime now) {
        if (!repeat)
            return false;
        if (!isTimeHasBegan(now))
            return false;
        LocalDateTime timeToShow = LocalDate.parse(startDate, ScheduleDialog.dateFormat).atTime(LocalTime.parse(showTimeInDay));
        boolean isNeedToShow = now.isAfter(timeToShow);
        adjustRealStartDate(now.toLocalDate());
        return isNeedToShow;
    }

    private boolean isNeedToShownSpecificDaysInWeek(Duration timeSinceLastShown) {
        int lastDayShownInTheWeek = LocalDate.parse(startDate, ScheduleDialog.dateFormat).getDayOfWeek().getValue() % 7;
        int distanceNeededInDays = calculateDistanceToNextDay(lastDayShownInTheWeek);
        long distanceInDays = timeSinceLastShown.toDays();
        if (distanceInDays >= distanceNeededInDays) {
            long distanceToPreviousDay = calculateDistanceToPreviousDay(lastDayShownInTheWeek);
            long daysToAdd = distanceInDays - distanceToPreviousDay;
            startDate = LocalDate.parse(startDate, ScheduleDialog.dateFormat).plusDays(daysToAdd).format(ScheduleDialog.dateFormat);
            return true;
        }
        return false;
    }


    private int calculateDistanceToNextDay(int currentIndex) {
        int index = -1;
        for (int i = 1; i < daysInWeek.length; i++) {
            int in = (currentIndex + i) % daysInWeek.length;
            if (daysInWeek[in]) {
                index = currentIndex + i;
                break;
            }
        }
        if (index < currentIndex)
            return daysInWeek.length - currentIndex + index;
        return index - currentIndex;
    }

    private int calculateDistanceToPreviousDay(int currentIndex) {
        int index = -1;
        for (int i = 0; i < daysInWeek.length; i++) {
            int in = (currentIndex - i + daysInWeek.length) % daysInWeek.length;
            if (daysInWeek[in]) {
                index = currentIndex - i;
                break;
            }
        }
        if (index < currentIndex)
            return currentIndex - index;
        return currentIndex + daysInWeek.length - index;
    }


    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public LocalDateTime getScheduledDateTime() {
        return LocalDateTime.parse(scheduledDateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime.toString();
    }

    public Times getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(Times repeatOption) {
        this.repeatOption = repeatOption;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean[] getDaysInWeek() {
        return daysInWeek;
    }

    public void setDaysInWeek(boolean[] daysInWeek) {
        this.daysInWeek = daysInWeek;
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDate, ScheduleDialog.dateFormat);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate.toString();
    }

    public LocalDate getEndTime() {
        return LocalDate.parse(endDate, ScheduleDialog.dateFormat);
    }

    public void setEndTime(LocalDate endTime) {
        this.endDate = endTime.toString();
    }

    public LocalTime getShowTimeInDay() {
        return LocalTime.parse(showTimeInDay);
    }

    public void setShowTimeInDay(LocalTime showTimeInDay) {
        this.showTimeInDay = showTimeInDay.toString();
    }

    public enum Times {
        HOUR,
        DAY,
        EVERY_DAY,
        EVERY_WEEK,
        SPECIFIC_DAYS_IN_WEEK,
        EVERY_MONTH,
    }
}
