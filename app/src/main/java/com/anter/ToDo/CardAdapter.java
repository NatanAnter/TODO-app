package com.anter.ToDo;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements RowItemAdapter.ScoreUpdateListener {
    public static final int VIEW_TYPE_BUTTON = 0;
    public static final int VIEW_TYPE_CARD_TYPE_1 = 1;
    public static final int VIEW_TYPE_CARD_TYPE_2 = 2;
    public static final int VIEW_TYPE_CARD_TYPE_3 = 3;
    public static final int VIEW_TYPE_CARD_TYPE_4 = 4;
    private final Map<Button, List<Card>> cardMap;
    private Button currentTab;
    private Button starButton;
    private final List<DeletedCardInfo> recentlyDeletedCards = new ArrayList<>();
    private Snackbar lastSnackbar;
    private final RecyclerView recyclerView;
    private MainActivity mainActivity;


    public CardAdapter(Map<Button, List<Card>> cardMap, Button currentTab, RecyclerView recyclerView, MainActivity mainActivity) {
        this.cardMap = cardMap;
        this.currentTab = currentTab;
        this.recyclerView = recyclerView;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onScoreUpdated(Card card) {
        int cardPosition = getCurrentCards().indexOf(card);
        if (cardPosition != -1) {
            CardViewHolder holder = (CardViewHolder) recyclerView.findViewHolderForAdapterPosition(cardPosition);
            if (holder != null) {
                updateProgressBarWithAnimation(holder, calculateCardPercent(holder));
            }
        }
    }

    public void addCard0() {
        addCard(VIEW_TYPE_BUTTON);
    }

    public void addCard1() {
        addCard(VIEW_TYPE_CARD_TYPE_1);
    }

    public void addCard2() {
        addCard(VIEW_TYPE_CARD_TYPE_2);
    }

    public void addCard3() {
        addCard(VIEW_TYPE_CARD_TYPE_3);
    }

    public void addCard4() {
        addCard(VIEW_TYPE_CARD_TYPE_4);
    }


    public void addCard(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_BUTTON:
                getCurrentCards().add(getCurrentCards().size(), new Card(currentTab, viewType));
                notifyItemInserted(getItemCount() - 1);
                break;
            case VIEW_TYPE_CARD_TYPE_1:
            case VIEW_TYPE_CARD_TYPE_2:
            case VIEW_TYPE_CARD_TYPE_3:
            case VIEW_TYPE_CARD_TYPE_4:
                getCurrentCards().add(getCurrentCards().size() - 1, new Card(currentTab, viewType));
                notifyItemInserted(getItemCount() - 2);
                break;
        }
        recyclerView.smoothScrollToPosition(getCurrentCards().size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return getCurrentCards().get(position).getCardType();
    }

    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView;
        if (viewType == VIEW_TYPE_CARD_TYPE_1) {
            cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card1, parent, false);
        } else if (viewType == VIEW_TYPE_CARD_TYPE_2) {
            cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card2, parent, false);
        } else if (viewType == VIEW_TYPE_CARD_TYPE_3) {
            cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card3, parent, false);
        } else if (viewType == VIEW_TYPE_CARD_TYPE_4) {
            cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card4, parent, false);
        } else {
            cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_add, parent, false);
        }
        return new CardViewHolder(cardView);
    }


    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
        Card currentCard = getCurrentCards().get(holder.getAdapterPosition());
        int type = currentCard.getCardType();

        switch (type) {
            case VIEW_TYPE_CARD_TYPE_1:
                bindCardType1(holder, position);
                break;
            case VIEW_TYPE_CARD_TYPE_2:
                bindCardType2(holder, position);
                break;
            case VIEW_TYPE_CARD_TYPE_3:
                bindCardType3(holder, position);
                break;
            case VIEW_TYPE_CARD_TYPE_4:
                bindCardType4(holder, position);
                break;
            default:
                bindAddCard(holder);
                break;
        }
    }

    private void bindCardType1(CardViewHolder holder, int position) {
        bindStarHeaderAndDeleteButtons(holder, position);
        bindScheduleButton(holder, position);
        RowItemAdapter rowItemAdapter = new RowItemAdapter(getCurrentCards().get(position), this);
        setupRowRecyclerView(holder, rowItemAdapter);
    }

    private void bindCardType2(CardViewHolder holder, int position) {
        bindStarHeaderAndDeleteButtons(holder, position);
        bindCardType2EditTexts(holder, position);
        updateProgressBarWithAnimation(holder, calculateCard2Percent(holder));
    }

    private void bindCardType3(CardViewHolder holder, int position) {
        bindStarHeaderAndDeleteButtons(holder, position);
        updateProgressBarWithAnimation(holder, calculateCardPercent(holder));
        bindCardType3DesirableScore(holder, position);
        RowItemAdapter rowItemAdapter = new RowItemAdapter(getCurrentCards().get(position), this);
        setupRowRecyclerView(holder, rowItemAdapter);
    }

    private void bindCardType4(CardViewHolder holder, int position) {
        bindStarHeaderAndDeleteButtons(holder, position);
        updateProgressBarWithAnimation(holder, calculateCardPercent(holder));
        RowItemAdapter rowItemAdapter = new RowItemAdapter(getCurrentCards().get(position), this);
        setupRowRecyclerView(holder, rowItemAdapter);
    }

    private void bindAddCard(CardViewHolder holder) {
        bindAddCardButtons(holder);
    }

    private void bindCardType2EditTexts(CardViewHolder holder, int position) {
        Card currentCard = getCurrentCards().get(position);

        if (currentCard.getCurrentScore() != 0) {
            holder.currentScoreEditText.setText(String.valueOf(currentCard.getCurrentScore()));
        } else {
            holder.currentScoreEditText.setText("");
        }

        if (currentCard.getDesiredScore() != 0) {
            holder.desirableScoreEditText.setText(String.valueOf(currentCard.getDesiredScore()));
        } else {
            holder.desirableScoreEditText.setText("");
        }

        holder.currentScoreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(""))
                    getCurrentCards().get(holder.getAdapterPosition()).setCurrentScore(Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProgressBarWithAnimation(holder, calculateCard2Percent(holder));
            }
        });

        holder.desirableScoreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(""))
                    getCurrentCards().get(holder.getAdapterPosition()).setDesiredScore(Integer.parseInt(s.toString()));
                else
                    getCurrentCards().get(holder.getAdapterPosition()).setDesiredScore(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProgressBarWithAnimation(holder, calculateCard2Percent(holder));
            }
        });

        updateProgressBarWithAnimation(holder, calculateCard2Percent(holder));
    }

    private void bindCardType3DesirableScore(CardViewHolder holder, int position) {
        Card currentCard = getCurrentCards().get(position);

        if (currentCard.getDesiredScore() != 0) {
            holder.desirableScoreEditText.setText(String.valueOf(currentCard.getDesiredScore()));
        } else {
            holder.desirableScoreEditText.setText("");
        }

        holder.desirableScoreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(""))
                    getCurrentCards().get(holder.getAdapterPosition()).setDesiredScore(Integer.parseInt(s.toString()));
                else
                    getCurrentCards().get(holder.getAdapterPosition()).setDesiredScore(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProgressBarWithAnimation(holder, calculateCardPercent(holder));

            }
        });
    }

    private void bindAddCardButtons(CardViewHolder holder) {
        holder.addCard1Button.setOnClickListener(v -> addCard1());
        holder.addCard2Button.setOnClickListener(v -> addCard2());
        holder.addCard3Button.setOnClickListener(v -> addCard3());
        holder.addCard4Button.setOnClickListener(v -> addCard4());
    }

    private void setupRowRecyclerView(CardViewHolder holder, RowItemAdapter rowItemAdapter) {
        holder.rowRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rowRecyclerView.setAdapter(rowItemAdapter);

        holder.addRowButton.setOnClickListener(v -> {
            Card card = getCurrentCards().get(holder.getAdapterPosition());
            card.addRow();
            notifyItemChanged(holder.getAdapterPosition());
            recyclerView.smoothScrollToPosition(holder.getAdapterPosition());
        });
    }


    private int calculateCard2Percent(CardViewHolder holder) {
        int percent;
        try {
            percent = (int) (Double.parseDouble(holder.currentScoreEditText.getText().toString()) / Double.parseDouble(holder.desirableScoreEditText.getText().toString()) * 100);
        } catch (NumberFormatException e) {
            percent = 0;
        }
        return percent;
    }

    private int calculateCardPercent(CardViewHolder holder) {
        Card card = getCurrentCards().get(holder.getAdapterPosition());
        int goal = card.getDesiredScore();
        double current = card.getCurrentScore();

        if (goal != 0)
            return (int) (current / goal * 100);
        return 0;
    }

    public void bindScheduleButton(CardViewHolder holder, int position) {
        try {

            ImageButton scheduleButton = holder.scheduleButton;
            scheduleButton.setOnClickListener(v -> ScheduleDialog.showScheduleDialog(this, getCurrentCards(),getCurrentCards().get(position), mainActivity));
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }


    public void updateProgressBarWithAnimation(CardViewHolder holder, int percent) {
        String percentText = MainActivity.coordinatorLayout.getContext().getString(R.string.percent_text, percent);
        holder.progressText.setText(percentText);
        ObjectAnimator.ofInt(holder.progressBar, "progress", holder.progressBar.getProgress(), percent).setDuration(500).start();
    }

    private void bindStarHeaderAndDeleteButtons(@NonNull CardAdapter.CardViewHolder holder, int position) {
        Card currentCard = getCurrentCards().get(position);
        holder.header.setText(currentCard.getHeader());
        if (currentCard.isStarred()) {
            holder.starButton.setImageResource(R.drawable.ic_star_chosen);
        } else {
            holder.starButton.setImageResource(R.drawable.ic_star_not_chosen);
        }
        holder.header.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getCurrentCards().get(holder.getAdapterPosition()).setHeader(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        holder.starButton.setOnClickListener(v -> {
            Card starredCard = getCurrentCards().get(holder.getAdapterPosition());
            if (starredCard.isStarred()) {
                holder.starButton.setImageResource(R.drawable.ic_star_not_chosen);
                starredCard.setStarred(false);


            } else {
                holder.starButton.setImageResource(R.drawable.ic_star_chosen);

                starredCard.setStarred(true);
            }
        });

        holder.deleteButton.setOnClickListener(v -> deleteCard(holder.getAdapterPosition()));
    }

    public void deleteCard(int position) {
        if (this.currentTab != this.starButton) {
            Card card = getCurrentCards().get(position);
            getCurrentCards().remove(position);
            notifyItemRemoved(position);
            recentlyDeletedCards.add(new DeletedCardInfo(card, -1, position));
            showSnackBar();
        } else {
            Card cardToDelete = getCurrentCards().get(position);
            int realPosition = Objects.requireNonNull(cardMap.get(cardToDelete.getTab())).indexOf(cardToDelete);
            Objects.requireNonNull(cardMap.get(cardToDelete.getTab())).remove(cardToDelete);
            getCurrentCards().remove(cardToDelete);
            notifyItemRemoved(position);
            recentlyDeletedCards.add(new DeletedCardInfo(cardToDelete, position, realPosition));
            showSnackBar();
        }
    }

    public void showSnackBar() {
        DeletedCardInfo deletedCardInfo = recentlyDeletedCards.get(recentlyDeletedCards.size() - 1);
        Snackbar snackbar = Snackbar.make(MainActivity.coordinatorLayout, R.string.card_deleted, Snackbar.LENGTH_LONG);
        lastSnackbar = snackbar;

        snackbar.setAction(R.string.undo, v -> {
            Objects.requireNonNull(cardMap.get(deletedCardInfo.deletedCard.getTab())).add(deletedCardInfo.cardPosition, deletedCardInfo.deletedCard);
            if (this.currentTab.equals(deletedCardInfo.deletedCard.getTab()))
                notifyItemInserted(deletedCardInfo.cardPosition);
            if (this.currentTab == starButton) {
                Objects.requireNonNull(cardMap.get(starButton)).add(deletedCardInfo.cardStarPosition, deletedCardInfo.deletedCard);
                notifyItemInserted(deletedCardInfo.cardStarPosition);
            }
            recentlyDeletedCards.remove(recentlyDeletedCards.size() - 1);

            if (recentlyDeletedCards.size() > 0)
                showSnackBar();

        });
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == DISMISS_EVENT_TIMEOUT && lastSnackbar == snackbar) {
                    Toast.makeText(MainActivity.coordinatorLayout.getContext(), R.string.clearing_list, Toast.LENGTH_SHORT).show();
                    recentlyDeletedCards.clear();
                }
            }
        });

        snackbar.show();
    }


    @Override
    public int getItemCount() {
        List<Card> currentCards = this.cardMap.get(currentTab);
        return currentCards != null ? currentCards.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentTab(Button currentTab) {
        this.currentTab = currentTab;
        notifyDataSetChanged();
        if (this.getCurrentCards().size() == 0 && !cardMap.keySet().stream().limit(2).collect(Collectors.toList()).contains(currentTab))
            addCard0();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentTabFromStarred(Button currentTab) {

        this.cardMap.values().stream().flatMap(List::stream).collect(Collectors.toList())
                .forEach(Card::switchToAll);
        this.currentTab = currentTab;
        notifyDataSetChanged();
        if (this.getCurrentCards().size() == 0 && !cardMap.keySet().stream().limit(2).collect(Collectors.toList()).contains(currentTab))
            addCard0();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showStarred(Button starTab) {
        this.starButton = starTab;
        calculateStarred(starTab);
        this.currentTab = starTab;

        notifyDataSetChanged();

    }

    public void calculateStarred(Button starTab) {
        Objects.requireNonNull(this.cardMap.get(starTab)).clear();

        this.cardMap.put(starButton, this.cardMap.values().stream().flatMap(List::stream)
                .filter(Card::isOkTOAddToStarred).collect(Collectors.toList()));
    }


    public List<Card> getCurrentCards() {
        return this.cardMap.get(this.currentTab);
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        //card0
        public ImageButton addCard1Button;
        public ImageButton addCard2Button;
        public ImageButton addCard3Button;
        public ImageButton addCard4Button;

        //all
        public ImageButton starButton;
        public EditText header;
        public ImageButton deleteButton;

        //card1
        public RecyclerView rowRecyclerView;
        public Button addRowButton;
        public ImageButton scheduleButton;

        //card2
        public ProgressBar progressBar;
        public TextView progressText;
        public EditText currentScoreEditText;
        public EditText desirableScoreEditText;


        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            this.addCard1Button = itemView.findViewById(R.id.addCard1Button);
            this.addCard2Button = itemView.findViewById(R.id.addCard2Button);
            this.addCard3Button = itemView.findViewById(R.id.addCard3Button);
            this.addCard4Button = itemView.findViewById(R.id.addCard4Button);
            this.starButton = itemView.findViewById(R.id.starButton);
            this.header = itemView.findViewById(R.id.header);
            this.deleteButton = itemView.findViewById(R.id.deleteButton);
            this.rowRecyclerView = itemView.findViewById(R.id.rowsRecyclerView);
            this.addRowButton = itemView.findViewById(R.id.addRowButton);
            this.progressBar = itemView.findViewById(R.id.progressBar);
            this.progressText = itemView.findViewById(R.id.progressText);
            this.currentScoreEditText = itemView.findViewById(R.id.currentScoreEditText);
            this.desirableScoreEditText = itemView.findViewById(R.id.desirableScoreEditText);
            this.scheduleButton = itemView.findViewById(R.id.scheduleButton);
        }
    }

    public static class DeletedCardInfo {
        Card deletedCard;
        int cardStarPosition;
        int cardPosition;

        public DeletedCardInfo(Card deletedCard, int cardStarPosition, int cardPosition) {
            this.deletedCard = deletedCard;
            this.cardStarPosition = cardStarPosition;
            this.cardPosition = cardPosition;
        }
    }
}
