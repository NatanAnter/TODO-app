package com.anter.ToDo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.TabViewHolder> {
    Handler handler = new Handler(Looper.getMainLooper());
    boolean isLongClick = false;
    private int id = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    public static final int VIEW_TYPE_ADD_BUTTON = 2;
    public static final int VIEW_TYPE_STAR_TAB = 3;
    public static final int VIEW_TYPE_TIME_BUTTON = 4;

    private final MainActivity mainActivity;
    private final Map<Button, List<Card>> cardMap;
    private DeletedTabInfo deletedTabInfo;
    private Button currentTab;
    private View currentXMLTab;
    private View starTabXML;
    private Button starTab;
    private View timeTabXML;
    private Button timeTab;
    private int boundTabsCount = 0;


    public TabsAdapter(MainActivity mainActivity, Map<Button, List<Card>> cardMap) {
        this.mainActivity = mainActivity;
        this.cardMap = cardMap;
    }

    public void addFirstStarButtonTab() {
        this.currentTab = new Button(MainActivity.coordinatorLayout.getContext());
        cardMap.put(this.currentTab, new ArrayList<>());
        notifyItemInserted(cardMap.size() - 1);
    }

    public void addSecondTimeButtonTab() {
        this.currentTab = new Button(MainActivity.coordinatorLayout.getContext());
        cardMap.put(this.currentTab, new ArrayList<>());
        notifyItemInserted(cardMap.size() - 1);
    }

    public void addThirdAddingButtonTab() {
        this.currentTab = new Button(MainActivity.coordinatorLayout.getContext());
        cardMap.put(this.currentTab, new ArrayList<>());
        notifyItemInserted(cardMap.size() - 1);
    }

    public void addRegularTab() {
        showNewListNameDialog();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_STAR_TAB;
        } else if (position == 1) {
            return VIEW_TYPE_TIME_BUTTON;
        } else if (position == getItemCount() - 1) {
            return VIEW_TYPE_ADD_BUTTON;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void changeCurrentTab(Button tabToShow, View XMLTabToShow) {
        //handle colors:
        //if the previous is regular - restore background
        if (this.currentXMLTab != null && this.currentXMLTab != this.starTabXML && this.currentXMLTab != this.timeTabXML)
            this.currentXMLTab.setBackgroundColor(MainActivity.DEFAULT_COLOR);
        //if the next is regular - change background
        if (XMLTabToShow != starTabXML && XMLTabToShow != timeTabXML)
            XMLTabToShow.setBackgroundColor(MainActivity.CHOSEN_COLOR);

        //handle changing:
        //from star to regular
        if (currentXMLTab == starTabXML && XMLTabToShow != starTabXML)
            mainActivity.changeTabFromStarred(tabToShow);
        //from regular to regular
        else if (XMLTabToShow != starTabXML)
            mainActivity.setCurrentTab(tabToShow);
        //from something to star
        else
            mainActivity.showStarred(tabToShow);

        this.currentXMLTab = XMLTabToShow;
        this.currentTab = tabToShow;
    }

    private void showNewListNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_name, null);
        final EditText editTextListName = view.findViewById(R.id.editTextListName);


        AlertDialog alertDialog = builder.setView(view)
                .setTitle(R.string.choose_a_name_for_the_tab)
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    String tabName = editTextListName.getText().toString();
                    Button tabButton = new Button(MainActivity.coordinatorLayout.getContext());
                    tabButton.setText(tabName);
                    this.currentTab = tabButton;
                    Utils.insertInMap((LinkedHashMap<Button, List<Card>>) cardMap, cardMap.size() - 1, this.currentTab, new ArrayList<>());
                    notifyItemInserted(this.cardMap.size() - 1);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).create();
        editTextListName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                return true;
            }
            return false;
        });

        alertDialog.show();
    }

    private void showExistListNameDialog(TabViewHolder holder, Button changingXMLButton, Button keyButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_name, null);
        final EditText editTextListName = view.findViewById(R.id.editTextListName);
        final ImageButton deleteListButton = view.findViewById(R.id.deleteButton);

        AlertDialog changeNameDialog = builder.setView(view)
                .setTitle(R.string.enter_a_new_name_for_this_tab)
                .setPositiveButton(R.string.update, (dialog, which) -> {
                    String tabName = editTextListName.getText().toString();
                    changingXMLButton.setText(tabName);
                    keyButton.setText(tabName);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).create();
        editTextListName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                changeNameDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                return true;
            }
            return false;
        });
        deleteListButton.setOnClickListener(v -> {
            changeNameDialog.dismiss();
            showYesNoDialog(holder, keyButton);
        });
        changeNameDialog.show();
    }

    private void showYesNoDialog(TabViewHolder holder, Button keyButton) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.coordinatorLayout.getContext());
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_this_list)
                .setPositiveButton(R.string.delete, (dialog, id) -> {

                    Button deletedTab = new ArrayList<>(cardMap.keySet()).get(holder.getAdapterPosition());
                    deletedTabInfo = new DeletedTabInfo(deletedTab, cardMap.get(deletedTab), holder.getAdapterPosition());

                    cardMap.remove(keyButton);
                    List<Card> listToDelete =  cardMap.get(new ArrayList<>(cardMap.keySet()).get(1)).stream().filter(c->c.getTab()==keyButton).collect(Collectors.toList());
                    listToDelete.forEach(c->{
                        cardMap.get(new ArrayList<>(cardMap.keySet()).get(1)).remove(c);
                    });
                    notifyItemRemoved(holder.getAdapterPosition());
                    if(currentTab==starTab){
                        changeCurrentTab(starTab, starTabXML);
                    } else if (currentTab==timeTab) {
                        changeCurrentTab(timeTab, timeTabXML);
                    }
                    if (currentTab == keyButton) {
                        changeCurrentTab(starTab, starTabXML);
                        this.currentXMLTab = starTabXML;
                        this.currentTab = starTab;
                    }

                    Snackbar snackbar = Snackbar.make(MainActivity.coordinatorLayout, R.string.list_deleted, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.undo, v -> {
                        Utils.insertInMap((LinkedHashMap<Button, List<Card>>) cardMap, deletedTabInfo.position, deletedTabInfo.tab, deletedTabInfo.cards);
                        notifyItemInserted(deletedTabInfo.position);
                    });
                    snackbar.show();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @NonNull
    @Override
    public TabsAdapter.TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TabViewHolder tabViewHolder;
        if (viewType == VIEW_TYPE_ADD_BUTTON) {
            View tabView = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_add_list, parent, false);
            tabViewHolder = new TabViewHolder(tabView);
            ImageButton addListButton = tabView.findViewById(R.id.addListButton);
            addListButton.setOnClickListener(view -> {
                addRegularTab();
            });

        } else if (viewType == VIEW_TYPE_STAR_TAB) {
            View tabView = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_star, parent, false);
            tabViewHolder = new TabViewHolder(tabView);
            ImageButton XMLStarButton = tabView.findViewById(R.id.starButton);
            this.starTabXML = XMLStarButton;
            this.starTab = new ArrayList<>(cardMap.keySet()).get(0);
            XMLStarButton.setOnClickListener(view -> {
                changeCurrentTab(starTab, starTabXML);

            });

        } else if (viewType == VIEW_TYPE_TIME_BUTTON) {
            View tabView = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_time, parent, false);
            tabViewHolder = new TabViewHolder(tabView);
            ImageButton XMLStarButton = tabView.findViewById(R.id.timeButton);
            this.timeTabXML = XMLStarButton;
            this.timeTab = new ArrayList<>(cardMap.keySet()).get(1);
            XMLStarButton.setOnClickListener(view -> {
                changeCurrentTab(timeTab, timeTabXML);
            });

        } else {
            View tabView = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_tab, parent, false);
            tabViewHolder = new TabViewHolder(tabView);
        }
        return tabViewHolder;
    }

    @Override
    public long getItemId(int position) {
        // Generate a unique ID based on the tab's text and position
        String tabText = new ArrayList<>(cardMap.keySet()).get(position).getText().toString();
        return tabText.hashCode() + ++id;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TabsAdapter.TabViewHolder holder, int position) {
        boundTabsCount++;
        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            // Handle binding for regular tab items
            Button currentButton = new ArrayList<>(cardMap.keySet()).get(position);
            holder.tab.setText(currentButton.getText());
            Button createdXMLTabButton = holder.tab;
            changeCurrentTab(currentButton, holder.tab);
            holder.tab.setOnClickListener(v -> {
                changeCurrentTab(currentButton, createdXMLTabButton);
                isLongClick = false;
            });
            holder.tab.setOnLongClickListener(v -> {
                isLongClick = true;
                handler.postDelayed(() -> {
                    if (isLongClick) {
                        showExistListNameDialog(holder, createdXMLTabButton, currentButton);
                    }
                }, 1500);
                return true;
            });
            createdXMLTabButton.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    isLongClick = false;
                }
                return false;
            });
        }
        checkForEndFirstTimeBinding();
    }
    private void checkForEndFirstTimeBinding(){
        if(boundTabsCount== cardMap.size() && mainActivity.isNeedToChangeTabFirstTime()){
            if (MainActivity.userPreferences.getLastTabShownIndex() > 0) {
                mainActivity.changeTabFirstTime();
            }
        }
    }

    @Override
    public int getItemCount() {
        return cardMap.size();
    }


    public static class TabViewHolder extends RecyclerView.ViewHolder {
        public Button tab;

        public TabViewHolder(@NonNull View itemView) {
            super(itemView);
            tab = itemView.findViewById(R.id.tabButton);

        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public SpacesItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (ViewCompat.getLayoutDirection(parent) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                // Right-to-left layout
                outRect.left = spacing;

                if (parent.getChildAdapterPosition(view) != Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
                    outRect.left = spacing;
                } else {
                    outRect.left = 0;
                }
            } else {
                // Left-to-right layout
                outRect.right = spacing;

                if (parent.getChildAdapterPosition(view) != Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
                    outRect.right = spacing;
                } else {
                    outRect.right = 0;
                }
            }
        }
    }


    public static class DeletedTabInfo {
        public Button tab;
        public List<Card> cards;
        public int position;

        public DeletedTabInfo(Button tab, List<Card> cards, int position) {
            this.tab = tab;
            this.cards = cards;
            this.position = position;
        }

        @NonNull
        @Override
        public String toString() {
            return "DeletedTabInfo{" +
                    "tab=" + tab +
                    ", cards=" + cards +
                    '}';
        }
    }


}
