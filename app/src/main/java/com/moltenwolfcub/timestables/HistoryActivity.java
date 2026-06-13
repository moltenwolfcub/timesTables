package com.moltenwolfcub.timestables;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private Button btnAll, btnMonth, btnWeek, btnToday;
    private TextView tvTotalGames, tvAvgSpeed, tvBestSpeed, tvAvgDev;
    private EditText etFilterTable, etFilterPlayer;
    private HistoryAdapter adapter;
    private AppDatabase db;

    private long currentCutoffTime = 0;
    private int currentMaxTableFilter = 0;
    private String currentPlayerFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        View rootView = findViewById(R.id.history_root);
        ThemeUtils.applySystemBarInsets(rootView);

        db = AppDatabase.getDatabase(this);

        btnAll = findViewById(R.id.filter_all);
        btnMonth = findViewById(R.id.filter_month);
        btnWeek = findViewById(R.id.filter_week);
        btnToday = findViewById(R.id.filter_today);

        tvTotalGames = findViewById(R.id.stat_total_games);
        tvAvgSpeed = findViewById(R.id.stat_avg_speed);
        tvBestSpeed = findViewById(R.id.stat_best_speed);
        tvAvgDev = findViewById(R.id.stat_avg_deviation);
        etFilterTable = findViewById(R.id.et_filter_table);
        etFilterPlayer = findViewById(R.id.et_filter_player);
        RecyclerView rvHistory = findViewById(R.id.rv_history_list);

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
        etFilterPlayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPlayerFilter = s.toString().trim();
                loadDashboardData();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setActiveButton(btnAll);
        loadDashboardData();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            EditText[] fields = {etFilterPlayer, etFilterTable};
            for (EditText et : fields) {
                if (et != null && et.hasFocus()) {
                    Rect outRect = new Rect();
                    et.getGlobalVisibleRect(outRect);

                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        et.clearFocus();

                        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (manager != null) {
                            manager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        }
                    }
                    break;
                }

            }
        }

        return super.dispatchTouchEvent(event);
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
            int totalGames = db.gameHistoryDao().getGameCount(currentCutoffTime, currentMaxTableFilter, currentPlayerFilter);
            double avgSpeed = db.gameHistoryDao().getAverageSpeed(currentCutoffTime, currentMaxTableFilter, currentPlayerFilter);
            double bestSpeed = db.gameHistoryDao().getBestMatchSpeed(currentCutoffTime, currentMaxTableFilter, currentPlayerFilter);
            double avgDev = db.gameHistoryDao().getAverageSD(currentCutoffTime, currentMaxTableFilter, currentPlayerFilter);
            List<GameRecord> records = db.gameHistoryDao().getGamesFiltered(currentCutoffTime, currentMaxTableFilter, currentPlayerFilter);

            runOnUiThread(() -> {
                tvTotalGames.setText(getString(R.string.history_total_games, totalGames));
                tvAvgSpeed.setText(totalGames > 0 ? getString(R.string.history_average_speed, Question.formatDuration(this, avgSpeed)) : getString(R.string.history_average_speed_empty));
                tvBestSpeed.setText(totalGames > 0 ? getString(R.string.history_best_speed, Question.formatDuration(this, bestSpeed)) : getString(R.string.history_best_speed_empty));
                tvAvgDev.setText(totalGames > 0 ? getString(R.string.history_average_consistency, Question.formatDuration(this, avgDev)) : getString(R.string.history_average_consistency_empty));
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
            h.player.setText(h.player.getContext().getString(R.string.history_game_player, StringUtils.Title(r.getPlayerName())));
            h.table.setText(h.table.getContext().getString(R.string.history_game_table, r.getMaxTable()));
            h.questions.setText(h.questions.getContext().getResources().getQuantityString(R.plurals.history_game_question, r.getTotalQuestions(), r.getTotalQuestions()));
            h.speed.setText(h.speed.getContext().getString(R.string.history_game_speed, Question.formatDuration(h.speed.getContext(), r.getAverageSpeed())));
            h.stdev.setText(h.stdev.getContext().getString(R.string.history_game_consistency, Question.formatDuration(h.stdev.getContext(), r.getStandardDeviation())));

            Context context = h.rosette1.getContext();

            // ROSETTE 1
            int colRos1 = r.getRosette1();
            if (colRos1 == -1) {
                h.rosette1.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_rosette_history_empty));
                h.rosette1.setBackgroundTintList(null);
            } else {
                h.rosette1.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_rosette_history));
                h.rosette1.setBackgroundTintList(ColorStateList.valueOf(colRos1));
            }

            // ROSETTE 2
            int colRos2 = r.getRosette2();
            if (colRos2 == -1) {
                h.rosette2.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_rosette_history_empty));
                h.rosette2.setBackgroundTintList(null);
            } else {
                h.rosette2.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_rosette_history));
                h.rosette2.setBackgroundTintList(ColorStateList.valueOf(colRos2));
            }

            // ROSETTE 3
            int colRos3 = r.getRosette3();
            if (colRos3 == -1) {
                h.rosette3.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_rosette_history_empty));
                h.rosette3.setBackgroundTintList(null);
            } else {
                h.rosette3.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_rosette_history));
                h.rosette3.setBackgroundTintList(ColorStateList.valueOf(colRos3));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView date, player, table, questions, speed, stdev;
            View rosette1, rosette2, rosette3;
            ViewHolder(View v) {
                super(v);
                date = v.findViewById(R.id.row_date);
                player = v.findViewById(R.id.row_player_display);
                table = v.findViewById(R.id.row_table_info);
                questions = v.findViewById(R.id.row_score);
                speed = v.findViewById(R.id.row_speed);
                stdev = v.findViewById(R.id.row_consistency);

                rosette1 = v.findViewById(R.id.rosette_1);
                rosette2 = v.findViewById(R.id.rosette_2);
                rosette3 = v.findViewById(R.id.rosette_3);
            }
        }
    }
}
