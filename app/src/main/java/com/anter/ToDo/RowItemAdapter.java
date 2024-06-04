package com.anter.ToDo;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


//import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RowItemAdapter extends RecyclerView.Adapter<RowItemAdapter.RowViewHolder> {
    private final ScoreUpdateListener scoreUpdateListener;

    private final Card card;

    private Snackbar lastSnackbar;

    private final List<DeletedRowInfo> recentlyDeletedRows = new ArrayList<>();
    public RowItemAdapter(Card card, ScoreUpdateListener listener) {
        this.scoreUpdateListener = listener;
        this.card = card;
    }
    public interface ScoreUpdateListener {
        void onScoreUpdated(Card card);
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        int type = card.getCardType();
        switch (type) {
            case Card.CARD_TYPE_1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row1, parent, false);
                break;
            case Card.CARD_TYPE_3:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row3, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row4, parent, false);
                break;
        }
        return new RowViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        bindRowItem(holder, position);
    }
    private void bindRowItem(RowViewHolder holder, int position) {
        int type = card.getCardType();
        Row row = card.getCurrentDisplayedRows().get(position);

        switch (type) {
            case Card.CARD_TYPE_1:
                bindRowType1(holder, row);
                break;
            case Card.CARD_TYPE_3:
                bindRowType3(holder, row);
                break;
            case Card.CARD_TYPE_4:
                bindRowType4(holder, row);
                break;
        }
        holder.editText.requestFocus();
    }
    private void bindRowType1(RowViewHolder holder, Row row) {
        holder.editText.setText(row.getText());
        holder.checkBox.setChecked(row.isChecked());
        holder.starButton.setImageResource(row.isStarred() ? R.drawable.ic_star_chosen : R.drawable.ic_star_not_chosen);
    }

    private void bindRowType3(RowViewHolder holder, Row row) {
        holder.editText.setText(row.getText());
        if (row.getValue() != 0) holder.score.setText(String.valueOf(row.getValue()));
    }

    private void bindRowType4(RowViewHolder holder, Row row) {
        holder.checkBox.setChecked(row.isChecked());
        holder.editText.setText(row.getText());
        if (row.getValue() != 0) holder.score.setText(String.valueOf(row.getValue()));
    }

    @Override
    public int getItemCount() {
        return this.card.getCurrentDisplayedRows().size();
    }

    private void deleteItem(int position) {
        Row deletedRow = this.card.getCurrentDisplayedRows().get(position);
        this.card.getAllRows().remove(deletedRow);
        notifyItemRemoved(position);
        recentlyDeletedRows.add(new DeletedRowInfo(deletedRow, position, this.card));
        if(card.getCardType()== Card.CARD_TYPE_3||card.getCardType()== Card.CARD_TYPE_4)
            scoreUpdateListener.onScoreUpdated(card);
        showSnackBar();
    }

    public void showSnackBar() {
        DeletedRowInfo deletedRowInfo = recentlyDeletedRows.get(recentlyDeletedRows.size() - 1);
        Snackbar snackbar = Snackbar.make(MainActivity.coordinatorLayout, R.string.row_deleted, Snackbar.LENGTH_LONG);
        lastSnackbar = snackbar;
        snackbar.setAction(R.string.undo, v -> {
            card.getAllRows().add(deletedRowInfo.deletedRowPosition, deletedRowInfo.deletedRow);
            notifyItemInserted(deletedRowInfo.deletedRowPosition);
            recentlyDeletedRows.remove(deletedRowInfo);
            if(card.getCardType()== Card.CARD_TYPE_3||card.getCardType()== Card.CARD_TYPE_4){
                scoreUpdateListener.onScoreUpdated(card);
            }
            if (recentlyDeletedRows.size() > 0) showSnackBar();
        });
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == DISMISS_EVENT_TIMEOUT && lastSnackbar == snackbar) {
                    Toast.makeText(MainActivity.coordinatorLayout.getContext(), R.string.clearing_list, Toast.LENGTH_SHORT).show();
                    recentlyDeletedRows.clear();
                }
            }
        });
        snackbar.show();
    }


    public class RowViewHolder extends RecyclerView.ViewHolder {
        //all:
        public EditText editText;
        public ImageButton deleteButton;
        //row1:
        public ImageButton starButton;
        //row1, row4:
        public CheckBox checkBox;
        //row3, row 4:
        public EditText score;
        public RowViewHolder(View view) {
            super(view);
            initializeViews(view);
            setupListeners();
        }
        private void initializeViews(View view) {
            this.starButton = view.findViewById(R.id.starButton);
            this.checkBox = view.findViewById(R.id.checkBox);
            this.editText = view.findViewById(R.id.editText);
            this.deleteButton = view.findViewById(R.id.deleteButton);
            this.score = view.findViewById(R.id.scoreEditText);
        }
        private void setupListeners() {
            this.deleteButton.setOnClickListener(v -> deleteItem(getAdapterPosition()));

            this.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    card.getCurrentDisplayedRows().get(getAdapterPosition()).setText(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            int type = card.getCardType();
            if (type == Card.CARD_TYPE_1) {
                setupRowType1Listeners();
            } else if (type == Card.CARD_TYPE_3) {
                setupRowType3Listeners();
            } else if (type == Card.CARD_TYPE_4) {
                setupRowType4Listeners();
            }
        }
        private void setupRowType1Listeners() {
            this.starButton.setOnClickListener(v -> {
                boolean isStarred = card.getCurrentDisplayedRows().get(getAdapterPosition()).isStarred();
                starButton.setImageResource(isStarred ? R.drawable.ic_star_not_chosen : R.drawable.ic_star_chosen);
                card.getCurrentDisplayedRows().get(getAdapterPosition()).setStarred(!isStarred);
            });

            this.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Row currentRow = card.getCurrentDisplayedRows().get(getAdapterPosition());
                currentRow.setChecked(isChecked);
                updateEditTextStylingRow1(isChecked);
            });
        }
        private void setupRowType3Listeners() {
            this.score.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals("")) {
                        card.getCurrentDisplayedRows().get(getAdapterPosition()).setValue(Integer.parseInt(s.toString()));
                    } else {
                        card.getCurrentDisplayedRows().get(getAdapterPosition()).setValue(0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    scoreUpdateListener.onScoreUpdated(card);
                }
            });
        }
        private void setupRowType4Listeners() {
            this.score.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals("")) {
                        card.getCurrentDisplayedRows().get(getAdapterPosition()).setValue(Integer.parseInt(s.toString()));
                    } else {
                        card.getCurrentDisplayedRows().get(getAdapterPosition()).setValue(0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    scoreUpdateListener.onScoreUpdated(card);
                }
            });

            this.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Row currentRow = card.getCurrentDisplayedRows().get(getAdapterPosition());
                currentRow.setChecked(isChecked);
                updateEditTextStylingRow4(isChecked);
                scoreUpdateListener.onScoreUpdated(card);
            });
        }
        private void updateEditTextStylingRow1(boolean isChecked) {

            if (isChecked) {
                editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                editText.setTypeface(null, Typeface.ITALIC);
            } else {
                editText.setPaintFlags(editText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                editText.setTypeface(null, Typeface.NORMAL);
            }
        }
        private void updateEditTextStylingRow4(boolean isChecked) {

            if (isChecked) {
                editText.setTypeface(null, Typeface.BOLD);
            } else {
                editText.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    public static class DeletedRowInfo {
        Row deletedRow;
        int deletedRowPosition;
        Card deletedRowCard;

        public DeletedRowInfo(Row deleteRow, int deletedRowPosition, Card deletedRowCard) {
            this.deletedRow = deleteRow;
            this.deletedRowPosition = deletedRowPosition;
            this.deletedRowCard = deletedRowCard;
        }
    }


}

