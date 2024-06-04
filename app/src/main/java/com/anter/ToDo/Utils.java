package com.anter.ToDo;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private static final String PREF_NAME = "CardMapPrefs";
    private static final String CARD_MAP_KEY = "cardMap";
    private static final String BUTTON_NAMES = "buttonNames";
    public static final int NO_CARD_ADDED = 0;
    public static final int CARD_ADDED = 1;
    public static final int TAB_ADDED = 2;
    private static AppName appName;


    public static void saveCardMap(Context context, Map<Button, List<Card>> cardMap, Button lastTab) {
        MainActivity.userPreferences.setLastTabShownIndex(new ArrayList<>(cardMap.keySet()).indexOf(lastTab));
        saveCardsTabAsIndex((LinkedHashMap<Button, List<Card>>) cardMap);
        String json = getAppDataInJson(cardMap);

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(CARD_MAP_KEY, json);
        editor.apply();
    }


    public static String getAppDataInJson(Map<Button, List<Card>> cardMap) {
        Gson gson = new Gson();
        String cardMapJson = gson.toJson(cardMap);

        List<String> buttonNames = cardMap.keySet().stream().map(b -> b.getText().toString()).collect(Collectors.toList());
        String buttonNamesJson = gson.toJson(buttonNames);
        String userPrefJson = gson.toJson(MainActivity.userPreferences);

        return "{cardMap:" + cardMapJson + ",buttonsNames:" + buttonNamesJson + ",userPreferences:" + userPrefJson + "}";
    }

    public static void loadCardMap(Context context, Map<Button, List<Card>> cardMap) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(CARD_MAP_KEY, null);
        if (json != null && json.length() > 10 && json.substring(0, 10).contains("{cardMap:") && !json.contains(",userPreferences:")) {
            json = json.substring(0, json.length() - 1) + ",userPreferences:{}}";

        } else if (json != null && json.length() > 10 && !json.substring(0, 10).contains("{cardMap:")) {
            String jsonButtons = sharedPreferences.getString(BUTTON_NAMES, null);
            json = "{cardMap:" + json + ",buttonsNames:" + jsonButtons + ",userPreferences:{}}";
        }

        if (json != null) {
            cardMap.clear();
            addToMap(context, json, cardMap);
            manageSchedule(context, cardMap);
        }
    }

    public static void addToMap(Context context, String json, Map<Button, List<Card>> cardMap) {
        List<String> jsonList = splitJsonObjects(json);
        String cardMapJson = jsonList.get(0);
        String buttonNamesJson = jsonList.get(1);
        String userPreferencesJson = jsonList.get(2);

        Gson gson = new Gson();

        UserPreferences userPreferences = gson.fromJson(userPreferencesJson, UserPreferences.class);
        MainActivity.userPreferences = new UserPreferences(userPreferences);

        Type buttonNamesType = new TypeToken<List<String>>() {
        }.getType();
        List<String> buttonNames = gson.fromJson(buttonNamesJson, buttonNamesType);

        // Parse card map JSON
        Type cardMapType = new TypeToken<Map<String, List<Card>>>() {
        }.getType();
        Map<String, List<Card>> serializableMap = gson.fromJson(cardMapJson, cardMapType);

        Map<Button, List<Card>> newCardMap = new LinkedHashMap<>();
        Iterator<String> buttonNameIterator = buttonNames.iterator();
        int i = 0;

        for (Map.Entry<String, List<Card>> entry : serializableMap.entrySet()) {

            Button button = new Button(context);
            button.setText(buttonNameIterator.next());
            entry.getValue().forEach(card -> card.setTab(button));
            if (i++ == 1 && entry.getValue().stream().map(c -> c.cardType).collect(Collectors.toList()).contains(0)) {
                newCardMap.put(new Button(context), new ArrayList<>());//todo wtf does this do?
            }

            newCardMap.put(button, entry.getValue());
        }
        newCardMap.get(new ArrayList<>(newCardMap.keySet()).get(1)).stream().filter(c -> !c.isCardScheduleTabHasDeleted()).filter(c -> c.getTabIndexForSaving() != 0).forEach(c -> {
            c.setTab(new ArrayList<>(newCardMap.keySet()).get(c.getTabIndexForSaving()));
        });
        cardMap.putAll(newCardMap);

    }

    public static List<String> splitJsonObjects(String jsonString) {

        if (jsonString == null || jsonString.length() < 2)
            return null;
        String newJsonString = jsonString.substring(1, jsonString.length() - 1);
        int a = newJsonString.contains("{") ? newJsonString.indexOf('{') : newJsonString.length();
        int b = newJsonString.contains("[") ? newJsonString.indexOf('[') : newJsonString.length();
        int c = Math.min(a, b);
        newJsonString = newJsonString.substring(c);
        int counter = 1; // Counter to keep track of nested curly braces
        List<String> jsonList = new ArrayList<>();

        for (int i = 1; i < newJsonString.length(); i++) {
            char currentChar = newJsonString.charAt(i);
            if (currentChar == '{' || currentChar == '[') {
                ++counter;
                System.out.println(counter);
            } else if (currentChar == '}' || currentChar == ']') {
                --counter;
                System.out.println(counter);
            }
            if (counter == 0) {
                String firstJsonObject = newJsonString.substring(0, i + 1);
                jsonList.add(firstJsonObject);
                newJsonString = newJsonString.substring(i + 1).trim();
                a = newJsonString.contains("{") ? newJsonString.indexOf('{') : newJsonString.length();
                b = newJsonString.contains("[") ? newJsonString.indexOf('[') : newJsonString.length();
                c = Math.min(a, b);
                newJsonString = newJsonString.substring(c);
                counter = 1;
                i = 0;
            }
        }
        return jsonList;
    }

    private static void saveCardsTabAsIndex(LinkedHashMap<Button, List<Card>> cardMap) {
        List<Card> list = cardMap.get(new ArrayList<>(cardMap.keySet()).get(1));
        list.forEach(c -> {
            saveCardTabAsIndex(cardMap, c);
        });
    }

    public static void saveCardTabAsIndex(LinkedHashMap<Button, List<Card>> cardMap, Card card) {
        int index = new ArrayList<>(cardMap.keySet()).indexOf(card.tab);
        card.setTabIndexForSaving(index);
    }


    public static void insertInMap(LinkedHashMap<Button, List<Card>> map, int position, Button key, List<Card> value) {
        if (position < 0 || position > map.size()) {
            throw new IndexOutOfBoundsException("Invalid position for insertion");
        }

        List<Map.Entry<Button, List<Card>>> entries = new ArrayList<>(map.entrySet());

        entries.add(position, new AbstractMap.SimpleEntry<>(key, value));

        map.clear();

        for (Map.Entry<Button, List<Card>> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public static void moveKey(LinkedHashMap<Button, List<Card>> map, int fromPosition, int toPosition) {
        List<Map.Entry<Button, List<Card>>> entries = new ArrayList<>(map.entrySet());
        entries.add(toPosition, entries.remove(fromPosition));
        map.clear();
        for (Map.Entry<Button, List<Card>> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public static int manageSchedule(Context context, Map<Button, List<Card>> cardMap) {
        int st = NO_CARD_ADDED;
        List<Card> list = cardMap.get(new ArrayList<>(cardMap.keySet()).get(1));
        for (Card c : list) {
            if (c.getSchedule() != null && c.getSchedule().isNeedToShown(LocalDateTime.now())) {
                int sta = addCardToList(context, cardMap, new Card(c));
                if (st != TAB_ADDED)
                    st = sta;
                if (c.getSchedule().isRepeat()) {
                    if (c.getSchedule().getStartDate().isAfter(c.getSchedule().getEndTime())) {
                        list.remove(c);
                        c.setSchedule(null);
                    }
                } else {
                    list.remove(c);
                    c.setSchedule(null);
                }
            }
        }
        return st;
    }

    private static int addCardToList(Context context, Map<Button, List<Card>> cardMap, Card card) {
        if (!card.isCardScheduleTabHasDeleted()) {
            List<Card> list = cardMap.get(card.tab);
            list.add(0, card);
            return CARD_ADDED;

        } else {
            cardMap.get(new ArrayList<>(cardMap.keySet()).get(1)).remove(card);
            return NO_CARD_ADDED;
        }
    }


    public static void setAppName(AppName appName) {
        Utils.appName = appName;
    }

    public static AppName getAppName() {
        return appName;
    }
}
