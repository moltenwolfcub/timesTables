package com.moltenwolfcub.timestables;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private Button btnAll, btnMonth, btnWeek, btnToday;
    private TextView tvTotalGames, tvAvgSpeed, tvBestSpeed;
    private EditText etFilterTable;
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private AppDatabase db;

    private long currentCutoffTime = 0;
    private int currentMaxTableFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = AppDatabase.getDatabase(this);

        btnAll = findViewById(R.id.filter_all);
        btnMonth = findViewById(R.id.filter_month);
        btnWeek = findViewById(R.id.filter_week);
        btnToday = findViewById(R.id.filter_today);

        tvTotalGames = findViewById(R.id.stat_total_games);
        tvAvgSpeed = findViewById(R.id.stat_avg_speed);
        tvBestSpeed = findViewById(R.id.stat_best_speed);
        etFilterTable = findViewById(R.id.et_filter_table);
        rvHistory = findViewById(R.id.rv_history_list);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(new ArrayList<>());
        rvHistory.setAdapter(adapter);

        btnAll.setOnClickListener(v -> {
            updateTimeFilter(0);
            setActiveButton(btnAll);
        });
        btnMonth.setOnClickListener(v -> {
            updateTimeFilter(getCutoffTimestamp(Calendar.MONTH, -1));
            setActiveButton(btnMonth);
        });
        btnWeek.setOnClickListener(v -> {
            updateTimeFilter(getCutoffTimestamp(Calendar.WEEK_OF_YEAR, -1));
            setActiveButton(btnWeek);
        });
        btnToday.setOnClickListener(v -> {
            updateTimeFilter(getCutoffTimestamp(Calendar.DAY_OF_YEAR, 0));
            setActiveButton(btnToday);
        });

        etFilterTable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                if (input.isEmpty()) {
                    currentMaxTableFilter = 0;
                } else {
                    try {
                        currentMaxTableFilter = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        currentMaxTableFilter = 0;
                    }
                }
                loadDashboardData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setActiveButton(btnAll);
        loadDashboardData();
    }

    private void updateTimeFilter(long newCutoff) {
        this.currentCutoffTime = newCutoff;
        loadDashboardData();
    }

    private long getCutoffTimestamp(int calendarField, int amount) {
        Calendar cal = Calendar.getInstance();
        if (calendarField == Calendar.DAY_OF_YEAR && amount == 0) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            return cal.getTimeInMillis();
        }
        cal.add(calendarField, amount);
        return cal.getTimeInMillis();
    }

    private void setActiveButton(Button activeIntent) {
        Button[] allButtons = {btnAll, btnMonth, btnWeek, btnToday};

        int selectedBg = android.graphics.Color.parseColor("#38BDF8");
        int selectedText = android.graphics.Color.parseColor("#0F172A");

        int unselectedBg = android.graphics.Color.parseColor("#1E293B");
        int unselectedText = android.graphics.Color.parseColor("#64748B");

        for (Button btn : allButtons) {
            if (btn == activeIntent) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(selectedBg));
                btn.setTextColor(selectedText);
                btn.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(unselectedBg));
                btn.setTextColor(unselectedText);
                btn.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void loadDashboardData() {
        // Run database queries on a background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int totalGames = db.gameHistoryDao().getGameCount(currentCutoffTime, currentMaxTableFilter);
            double avgSpeed = db.gameHistoryDao().getAverageSpeed(currentCutoffTime, currentMaxTableFilter);
            double bestSpeed = db.gameHistoryDao().getBestMatchSpeed(currentCutoffTime, currentMaxTableFilter);
            List<GameRecord> records = db.gameHistoryDao().getGamesFiltered(currentCutoffTime, currentMaxTableFilter);

            runOnUiThread(() -> {
                tvTotalGames.setText("Total Games: " + totalGames);
                tvAvgSpeed.setText(totalGames > 0 ? String.format(Locale.UK, "Overall Avg Speed: %s", Question.formatDuration(avgSpeed)) : "Overall Avg Speed: --");
                tvBestSpeed.setText(totalGames > 0 ? String.format(Locale.UK, "Personal Best Speed: %s", Question.formatDuration(bestSpeed)) : "Personal Best Speed: --");
                adapter.updateList(records);
            });
        });
    }

    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<GameRecord> items;

        HistoryAdapter(List<GameRecord> items) {
            this.items = items;
        }

        void updateList(List<GameRecord> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_history_row, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            GameRecord r = items.get(pos);
            h.date.setText(DateFormat.format("dd MMM yyyy HH:mm", r.getTimestamp()));
            h.table.setText("Max Table: " + r.getMaxTable());
            h.score.setText(r.getTotalQuestions() + " Questions");
            h.speed.setText(String.format(Locale.UK, "%s avg", Question.formatDuration(r.getAverageSpeed())));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView date, table, score, speed;
            ViewHolder(View v) {
                super(v);
                date = v.findViewById(R.id.row_date);
                table = v.findViewById(R.id.row_table_info);
                score = v.findViewById(R.id.row_score);
                speed = v.findViewById(R.id.row_speed);
            }
        }
    }
}
