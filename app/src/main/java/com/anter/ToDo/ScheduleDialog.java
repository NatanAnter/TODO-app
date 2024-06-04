package com.anter.ToDo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleDialog {


    private static final String[] daysEn = {"every day", "specific days in the week", "specific day in month"};
    private static final String[] daysHe = {"כל יום", "ימים ספציפיים בשבוע", "יום ספציפי בחודש"};
    public static String dateFormatString = "%02d/%02d/%04d";
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static Map<Button, List<Card>> cardMap;

    public static void setCardMap(Map<Button, List<Card>> cardMap) {
        ScheduleDialog.cardMap = cardMap;
    }

    public static void showScheduleDialog(CardAdapter cardAdapter, List<Card> list, Card card, MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.coordinatorLayout.getContext());
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_schedule, null);
        builder.setView(view);
        builder.setTitle("Schedule");
        Dialog dialog = builder.create();

        // Initialize radio groups
        RadioGroup radioGroupDataChange = view.findViewById(R.id.radioGroupScheduleType);
        RadioButton btnRepeat = view.findViewById(R.id.optionRepeatRadioButton);
        RadioButton btnOnce = view.findViewById(R.id.optionScheduleRadioButton);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        Spinner comboBoxASpinner = view.findViewById(R.id.comboBoxASpinner);

        EditText editTextDate = view.findViewById(R.id.editTextDate);
        EditText editTextTime = view.findViewById(R.id.editTextTime);

        EditText editTextFromDate = view.findViewById(R.id.editTextFromDate);
        EditText editTextToDate = view.findViewById(R.id.editTextToDate);
        EditText numberEditText = view.findViewById(R.id.numberEditText);

        CheckBox checkboxSunday = view.findViewById(R.id.checkboxSunday);
        CheckBox checkboxMonday = view.findViewById(R.id.checkboxMonday);
        CheckBox checkboxTuesday = view.findViewById(R.id.checkboxTuesday);
        CheckBox checkboxWednesday = view.findViewById(R.id.checkboxWednesday);
        CheckBox checkboxThursday = view.findViewById(R.id.checkboxThursday);
        CheckBox checkboxFriday = view.findViewById(R.id.checkboxFriday);
        CheckBox checkboxSaturday = view.findViewById(R.id.checkboxSaturday);

        // Populate the spinners with options for question A and question B
        String[] arr1 = daysEn;

        ArrayAdapter<String> adapterA = new ArrayAdapter<>(MainActivity.coordinatorLayout.getContext(),
                android.R.layout.simple_spinner_item, arr1);
        adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboBoxASpinner.setAdapter(adapterA);

        if (card.getSchedule() != null) {
            if (card.getSchedule().isRepeat()) {
                btnRepeat.setChecked(true);
                showScheduleRepeatDialog(view);
                if (card.getSchedule().getRepeatOption() == Schedule.Times.EVERY_MONTH) {
                    comboBoxASpinner.setSelection(2);
                    numberEditText.setText(card.getSchedule().getStep() + "");
                } else if (card.getSchedule().getRepeatOption() == Schedule.Times.SPECIFIC_DAYS_IN_WEEK) {
                    comboBoxASpinner.setSelection(1);
                    checkboxSunday.setChecked(card.getSchedule().getDaysInWeek()[0]);
                    checkboxMonday.setChecked(card.getSchedule().getDaysInWeek()[1]);
                    checkboxTuesday.setChecked(card.getSchedule().getDaysInWeek()[2]);
                    checkboxWednesday.setChecked(card.getSchedule().getDaysInWeek()[3]);
                    checkboxThursday.setChecked(card.getSchedule().getDaysInWeek()[4]);
                    checkboxFriday.setChecked(card.getSchedule().getDaysInWeek()[5]);
                    checkboxSaturday.setChecked(card.getSchedule().getDaysInWeek()[6]);
                }

                editTextFromDate.setText(card.getSchedule().getStartDate().format(ScheduleDialog.dateFormat));
                editTextToDate.setText(card.getSchedule().getEndTime().format(ScheduleDialog.dateFormat));
            } else {
                btnOnce.setChecked(true);
                showScheduleOnceDialog(view);
                editTextDate.setText(card.getSchedule().getScheduledDateTime().toLocalDate().format(ScheduleDialog.dateFormat));
                editTextTime.setText(card.getSchedule().getScheduledDateTime().toLocalTime().toString());
            }

        } else {
            editTextFromDate.setText(LocalDate.now().format(dateFormat));
            editTextToDate.setText(LocalDate.now().plusMonths(1).format(dateFormat));
            if (MainActivity.userPreferences.isScheduleRepeat()) {
                btnRepeat.setChecked(true);
                showScheduleRepeatDialog(view);
            } else {
                btnOnce.setChecked(true);
                showScheduleOnceDialog(view);
            }
        }

        radioGroupDataChange.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedScheduleId = radioGroupDataChange.getCheckedRadioButtonId();
            boolean isRepeat = selectedScheduleId == R.id.optionRepeatRadioButton;
            if (isRepeat)
                showScheduleRepeatDialog(view);
            else
                showScheduleOnceDialog(view);
        });

        dialog.getWindow().getDecorView().setLayoutDirection(MainActivity.userPreferences.getLayoutDirectionStatus());
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            if (!cardMap.values().stream().flatMap(List::stream).collect(Collectors.toList()).contains(card)) {
                dialog.dismiss();
                Toast.makeText(mainActivity, "card deleted!", Toast.LENGTH_LONG).show();
                return;
            }

            int selectedScheduleId = radioGroupDataChange.getCheckedRadioButtonId();
            boolean isRepeat = selectedScheduleId == R.id.optionRepeatRadioButton;
            MainActivity.userPreferences.setScheduleRepeat(isRepeat);
            if (isRepeat) {
                Schedule.Times times = getTimeType(comboBoxASpinner.getSelectedItem().toString());
                int step = 1;
                if (!numberEditText.getText().toString().equals("")) {
                    step = Integer.parseInt(numberEditText.getText().toString());
                }
                boolean[] days = new boolean[]{checkboxSunday.isChecked(), checkboxMonday.isChecked(), checkboxTuesday.isChecked()
                        , checkboxWednesday.isChecked(), checkboxThursday.isChecked(), checkboxFriday.isChecked(), checkboxSaturday.isChecked()};
                LocalDate startTime = LocalDate.parse(editTextFromDate.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate endTime = LocalDate.parse(editTextToDate.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalTime showTimeInDay = LocalTime.parse("04:00");
                card.setSchedule(new Schedule(times, step, days, startTime, endTime, showTimeInDay));
                card.getSchedule().adjustRealStartDate(LocalDate.now());

            } else {
                if (editTextDate.getText().toString().equals("") || editTextTime.getText().toString().equals("")) {
                    Toast.makeText(mainActivity.getLayoutInflater().getContext(), "not a date", Toast.LENGTH_SHORT).show();
                } else {
                    LocalDate localDate = LocalDate.parse(editTextDate.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalTime localTime = LocalTime.parse(editTextTime.getText().toString());
                    LocalDateTime dateTime = localDate.atTime(localTime);
                    card.setSchedule(new Schedule(dateTime));
                    int index = list.indexOf(card);
                    list.remove(card);
                    cardAdapter.notifyItemRemoved(index);
                    Toast.makeText(mainActivity.getLayoutInflater().getContext(), "card moved to schedule List", Toast.LENGTH_LONG).show();
                }

            }
            Card cardSchedule = addCardToScheduleListIfNotUpdate(card);
            Utils.saveCardTabAsIndex((LinkedHashMap<Button, List<Card>>) cardMap, cardSchedule);
            dialog.dismiss();
        });
        dialog.show();
    }

    public static Card addCardToScheduleListIfNotUpdate(Card card) {
        List<Card> cards = cardMap.get(new ArrayList<>(cardMap.keySet()).get(1));
        int index = 0;
        while (index < cards.size() - 1 && cards.get(index).getDateTimeOrder().isBefore(cards.get(index + 1).getDateTimeOrder())) {
            index++;
        }
        Card cardSchedule = new Card(card);
        cards.add(index, cardSchedule);
        return cardSchedule;

    }

    private static Schedule.Times getTimeType(String time) {
        if (time.equals(daysEn[0]) || time.equals(daysHe[0])) {
            return Schedule.Times.EVERY_DAY;
        } else if (time.equals(daysEn[1]) || time.equals(daysHe[1])) {
            return Schedule.Times.SPECIFIC_DAYS_IN_WEEK;
        } else {
            return Schedule.Times.EVERY_MONTH;
        }
    }

    private static void showScheduleOnceDialog(View view) {
        // Initialize views
        LinearLayout thisLinearLayout = view.findViewById(R.id.linearLayoutSpecificDate);
        LinearLayout otherLinearLayout = view.findViewById(R.id.linearLayoutRepeat);

        thisLinearLayout.setVisibility(View.VISIBLE);
        otherLinearLayout.setVisibility(View.GONE);


        EditText editTextDate = view.findViewById(R.id.editTextDate);
        EditText editTextTime = view.findViewById(R.id.editTextTime);
        Button datePickerButton = view.findViewById(R.id.datePickerButton);
        Button timePickerButton = view.findViewById(R.id.timePickerButton);

        // Date Picker Dialog
        datePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.coordinatorLayout.getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), dateFormatString, selectedDay, selectedMonth + 1, selectedYear);
                        editTextDate.setText(selectedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });

        // Time Picker Dialog
        timePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    MainActivity.coordinatorLayout.getContext(),
                    (view12, selectedHour, selectedMinute) -> {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        editTextTime.setText(selectedTime);
                    },
                    hour, minute, false);

            timePickerDialog.show();
        });

    }

    private static void showScheduleRepeatDialog(View view) {

        LinearLayout thisLinearLayout = view.findViewById(R.id.linearLayoutRepeat);
        LinearLayout otherLinearLayout = view.findViewById(R.id.linearLayoutSpecificDate);
        thisLinearLayout.setVisibility(View.VISIBLE);
        otherLinearLayout.setVisibility(View.GONE);

        LinearLayout secondQuestion = view.findViewById(R.id.question2);
        LinearLayout days = view.findViewById(R.id.daysOfTheWeek);
        secondQuestion.setVisibility(View.GONE);
        days.setVisibility(View.GONE);

        // Initialize views
        Spinner comboBoxASpinner = view.findViewById(R.id.comboBoxASpinner);
        Button fromDate = view.findViewById(R.id.rangeStartTimePickerButton);
        Button toDate = view.findViewById(R.id.rangeEndTimePickerButton);
        EditText editTextFromDate = view.findViewById(R.id.editTextFromDate);
        EditText editTextToDate = view.findViewById(R.id.editTextToDate);

        fromDate.setOnClickListener(o -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.coordinatorLayout.getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), dateFormatString, selectedDay, selectedMonth + 1, selectedYear);
                        editTextFromDate.setText(selectedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
        toDate.setOnClickListener(o -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.coordinatorLayout.getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), dateFormatString, selectedDay, selectedMonth + 1, selectedYear);
                        editTextToDate.setText(selectedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });


        // Set up logic for question A Spinner
        comboBoxASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                if (selectedItem.equals(daysEn[1]) || selectedItem.equals(daysHe[1])) {
                    // Show/hide relevant views for question B
                    days.setVisibility(View.VISIBLE);
                    secondQuestion.setVisibility(View.GONE);

                } else if (selectedItem.equals(daysEn[2]) || selectedItem.equals(daysHe[2])) {
                    days.setVisibility(View.GONE);
                    secondQuestion.setVisibility(View.VISIBLE);
                    editTextToDate.setText(LocalDate.now().plusYears(1).format(dateFormat));
                } else {
                    days.setVisibility(View.GONE);
                    secondQuestion.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

    }


}
