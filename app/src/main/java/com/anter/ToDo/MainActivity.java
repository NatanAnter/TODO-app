package com.anter.ToDo;

import static java.lang.Thread.sleep;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public static int DEFAULT_COLOR;
    public static int CHOSEN_COLOR;
    public static CoordinatorLayout coordinatorLayout;
    private Button currentTab;
    private final Map<Button, List<Card>> cardMap = new LinkedHashMap<>();
    private TabsAdapter tabsAdapter;
    private CardAdapter cardAdapter;
    public static UserPreferences userPreferences = new UserPreferences();
    private RecyclerView tabButtonsRecycleView;
    private boolean needToChangeTabFirstTime;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        /*if (itemId == R.id.setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else*/
        if (itemId == R.id.theme) {
            showThemeDialog();
            return true;
        } else if (itemId == R.id.data) {
            showDataDialog();
            return true;
        } else if (itemId == R.id.menu) {
            // Handle Item 1 click
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cardMap != null) {
            int st = Utils.manageSchedule(this, cardMap);
            if (st == Utils.TAB_ADDED && tabsAdapter != null)
                tabsAdapter.notifyDataSetChanged();
            else if (st == Utils.CARD_ADDED && cardAdapter != null)
                cardAdapter.notifyDataSetChanged();
        }

    }

    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_data, null);
        builder.setView(view);

        // Initialize radio groups
        RadioGroup radioGroupDataChange = view.findViewById(R.id.radioGroupDataChange);
        Button btnCopyData = view.findViewById(R.id.btnGetData);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        EditText editText = view.findViewById(R.id.editText);


        final AlertDialog dialog = builder.create();

        dialog.getWindow().getDecorView().setLayoutDirection(userPreferences.getLayoutDirectionStatus());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnCopyData.setOnClickListener(v -> {
            String textToCopy = Utils.getAppDataInJson(cardMap);
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("label", textToCopy));
            Toast.makeText(coordinatorLayout.getContext(), R.string.text_copied, Toast.LENGTH_SHORT).show();

        });

        btnSubmit.setOnClickListener(v -> {
            int selectedThemeId = radioGroupDataChange.getCheckedRadioButtonId();
            boolean isAdding = selectedThemeId == R.id.radioDataAdd;
            boolean isADeleting = selectedThemeId == R.id.radioDataReplace;
            String json = editText.getText().toString();
            String backupData = Utils.getAppDataInJson(cardMap);
            if (json.equals("") || isAdding == isADeleting) {
                dialog.dismiss();
                return;
            }
            if (isADeleting)
                cardMap.clear();
            try {
                Utils.addToMap(this, json, cardMap);
            } catch (Exception e) {
                Toast.makeText(this, R.string.not_a_json, Toast.LENGTH_LONG).show();
                cardMap.clear();
                Utils.addToMap(this, backupData, cardMap);
            }
            dialog.dismiss();
            recreate();
        });
        dialog.show();
    }

    private void showThemeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_theme, null);
        builder.setView(view);

        // Initialize radio groups
        RadioGroup radioGroupTheme = view.findViewById(R.id.radioGroupTheme);
        RadioGroup radioGroupColor = view.findViewById(R.id.radioGroupColor);
        RadioGroup radioGroupOrientation = view.findViewById(R.id.radioGroupOrientation);


        if (userPreferences.getDarkModeStatus() == AppCompatDelegate.MODE_NIGHT_YES)
            ((RadioButton) view.findViewById(R.id.radioThemeDark)).setChecked(true);
        else if (userPreferences.getDarkModeStatus() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            ((RadioButton) view.findViewById(R.id.radioThemeDefault)).setChecked(true);
        else ((RadioButton) view.findViewById(R.id.radioThemeLight)).setChecked(true);

        if (userPreferences.isDynamic())
            ((RadioButton) view.findViewById(R.id.radioColorDynamic)).setChecked(true);
        else ((RadioButton) view.findViewById(R.id.radioColorDefault)).setChecked(true);
        if (userPreferences.getLayoutDirectionStatus() == View.LAYOUT_DIRECTION_RTL)
            ((RadioButton) view.findViewById(R.id.radioOrientationRTL)).setChecked(true);
        else if (userPreferences.getLayoutDirectionStatus() == View.LAYOUT_DIRECTION_LTR)
            ((RadioButton) view.findViewById(R.id.radioOrientationLTR)).setChecked(true);
        else ((RadioButton) view.findViewById(R.id.radioOrientationDefault)).setChecked(true);

        // Initialize buttons
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);


        final AlertDialog dialog = builder.create();
        dialog.getWindow().getDecorView().setLayoutDirection(userPreferences.getLayoutDirectionStatus());
        // Set click listeners for buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            // Handle submit button click
            // Get selected values from radio buttons and perform any necessary actions

            // Example: Check which theme is selected
            int selectedThemeId = radioGroupTheme.getCheckedRadioButtonId();
            userPreferences.setDarkModeStatus(selectedThemeId == R.id.radioThemeDark ? AppCompatDelegate.MODE_NIGHT_YES : selectedThemeId == R.id.radioThemeDefault ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM : AppCompatDelegate.MODE_NIGHT_NO);

            // Example: Check which color is selected
            int selectedColorId = radioGroupColor.getCheckedRadioButtonId();
            userPreferences.setDynamic(selectedColorId == R.id.radioColorDynamic);

            // Example: Check which orientation is selected
            int selectedOrientationId = radioGroupOrientation.getCheckedRadioButtonId();
            userPreferences.setLanguageAndDirection(selectedOrientationId == R.id.radioOrientationRTL ? View.LAYOUT_DIRECTION_RTL : selectedOrientationId == R.id.radioOrientationDefault ? View.LAYOUT_DIRECTION_LOCALE : View.LAYOUT_DIRECTION_LTR);


            Utils.getAppName().setDynamic(userPreferences.isDynamic());//todo move to initializePreferences(); (?)
            recreate();
            dialog.dismiss();
        });
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleDialog.setCardMap(cardMap);
        initializeViews();
        setupTabRecyclerView();
        setupCardRecyclerView();
        initializeItemTouchHelpers();
        LoadData();
        manageColors();
        initializePreferences();
    }

    private void initializePreferences() {
        AppCompatDelegate.setDefaultNightMode(userPreferences.getDarkModeStatus());
        getWindow().getDecorView().getRootView().setLayoutDirection(userPreferences.getLayoutDirectionStatus());
        Utils.getAppName().setDynamic(userPreferences.isDynamic());
        if (!userPreferences.getLanguage().equals("")) {
            setAppLocale(userPreferences.getLanguage());
        } else
            setSystemDefaultLocale();
    }

    private void setSystemDefaultLocale() {
        // Retrieve the system's default locale using getLocales() (API 24 and above)
        LocaleList localeList = getResources().getConfiguration().getLocales();

        // If user preferences include a specific language, use it; otherwise, use system default
        String userLanguage = Locale.getDefault().getLanguage();
        if (!userLanguage.equals("")) {
            setAppLocale(userLanguage);
        } else if (localeList.size() > 0) {
            setAppLocale(localeList.get(0).getLanguage());
        }
    }

    private void setAppLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initializeViews() {
        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
    }

    private void setupTabRecyclerView() {
        tabButtonsRecycleView = findViewById(R.id.tabsRecycleView);
        tabsAdapter = new TabsAdapter(this, cardMap);
        tabButtonsRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tabButtonsRecycleView.setAdapter(tabsAdapter);
        tabButtonsRecycleView.addItemDecoration(new TabsAdapter.SpacesItemDecoration(16));
    }

    private void setupCardRecyclerView() {
        RecyclerView cardsRecyclerView = findViewById(R.id.cardsRecyclerView);
        cardAdapter = new CardAdapter(cardMap, currentTab, cardsRecyclerView, this);
        cardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardsRecyclerView.setAdapter(cardAdapter);
    }

    private void initializeItemTouchHelpers() {
        // ItemTouchHelper for tab RecyclerView
        ItemTouchHelper itemTouchHelperTab = new ItemTouchHelper(new TabItemTouchHelperCallback(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0));
        itemTouchHelperTab.attachToRecyclerView(findViewById(R.id.tabsRecycleView));

        // ItemTouchHelper for card RecyclerView
        ItemTouchHelper itemTouchHelperCard = new ItemTouchHelper(new CardItemTouchHelperCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0));
        itemTouchHelperCard.attachToRecyclerView(findViewById(R.id.cardsRecyclerView));
    }

    private void LoadData() {
        Utils.loadCardMap(this, cardMap);
        if (cardMap.size() == 0) {
            tabsAdapter.addFirstStarButtonTab();
            tabsAdapter.addSecondTimeButtonTab();
            tabsAdapter.addThirdAddingButtonTab();
        }
        tabButtonsRecycleView.smoothScrollToPosition(cardMap.size()-1);
        if(userPreferences.getLastTabShownIndex()>0)
            this.needToChangeTabFirstTime = true;

    }
    public void changeTabFirstTime(){
        this.needToChangeTabFirstTime = false;
        new Handler(Looper.getMainLooper()).post(() -> {
            Button tabToShow = new ArrayList<>(cardMap.keySet()).get(userPreferences.getLastTabShownIndex());
            // Get the corresponding XML view for the tab
            View XMLTabToShow = tabButtonsRecycleView.getLayoutManager().findViewByPosition(userPreferences.getLastTabShownIndex());
            // Change the current tab to the specified tab
            tabsAdapter.changeCurrentTab(tabToShow, XMLTabToShow);
        });
    }

    public void manageColors() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        DEFAULT_COLOR = window.getStatusBarColor();
        int[] rgb = new int[]{Color.red(DEFAULT_COLOR), Color.green(DEFAULT_COLOR), Color.blue(DEFAULT_COLOR)};
        int[] newRgb = new int[3];
        for (int i = 0; i < rgb.length; i++) {
            newRgb[i] = (int) (rgb[i] + (255 - rgb[i]) * 0.3);
        }
        CHOSEN_COLOR = Color.rgb(newRgb[0], newRgb[1], newRgb[2]);
    }

    public void setCurrentTab(Button tab) {
        this.currentTab = tab;
        this.cardAdapter.setCurrentTab(tab);
    }

    public void changeTabFromStarred(Button tab) {
        this.currentTab = tab;
        this.cardAdapter.setCurrentTabFromStarred(tab);
    }

    public void showStarred(Button starButton) {
        this.cardAdapter.showStarred(starButton);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveCardMap(this, cardMap, currentTab);
    }


    private class TabItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
        public TabItemTouchHelperCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            tabsAdapter.handler.removeCallbacksAndMessages(null);
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition >= 2 && fromPosition != cardMap.size() - 1 && toPosition >= 2 && toPosition != cardMap.size() - 1) {
                Utils.moveKey((LinkedHashMap<Button, List<Card>>) cardMap, fromPosition, toPosition);
                tabsAdapter.notifyItemMoved(fromPosition, toPosition);
            }
            return true;

        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

    }

    public boolean isNeedToChangeTabFirstTime() {
        return needToChangeTabFirstTime;
    }

    private class CardItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
        public CardItemTouchHelperCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition != cardAdapter.getCurrentCards().size() - 1 && toPosition != cardAdapter.getCurrentCards().size() - 1) {
                Collections.swap(cardAdapter.getCurrentCards(), fromPosition, toPosition);
                cardAdapter.notifyItemMoved(fromPosition, toPosition);
            }
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }
    }
}


