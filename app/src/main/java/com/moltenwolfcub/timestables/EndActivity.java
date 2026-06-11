package com.moltenwolfcub.timestables;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class EndActivity extends AppCompatActivity {
    private Game game;

    private TextView playerName;
    private TextView maxTable;
    private TextView questionCount;
    private TextView speed;
    private TextView consistency;
    private Button done;
    private RecyclerView results;
    private LinearLayout rosetteContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        View rootView = findViewById(R.id.end_root);
        ThemeUtils.applySystemBarInsets(rootView);

        game = getIntent().getParcelableExtra("game");

        playerName = findViewById(R.id.playerName);
        maxTable = findViewById(R.id.tableMax);
        questionCount = findViewById(R.id.questionCount);
        speed = findViewById(R.id.speed);
        consistency = findViewById(R.id.standardDeviation);
        done = findViewById(R.id.done);
        results = findViewById(R.id.rv_end_questions_list);
        rosetteContainer = findViewById(R.id.ll_rosette_container);

        results.setLayoutManager(new LinearLayoutManager(this));
        QuestionSummaryAdapter summaryAdapter = new QuestionSummaryAdapter(game.GetQuestions());
        results.setAdapter(summaryAdapter);

        playerName.setText(Pattern.compile("^.").matcher(game.GetPlayerName().toLowerCase(Locale.ROOT)).replaceFirst(m -> m.group().toUpperCase(Locale.ROOT)));
        maxTable.setText(String.valueOf(game.MaxTable()));
        questionCount.setText(String.valueOf(game.QuestionCount()));

        long sum = 0;
        for (int i = 0; i<game.GetQuestions().size();i++) {
            sum+= game.GetQuestions().get(i).Duration();
        }
        double avg = (double) sum / game.QuestionCount();
        speed.setText(Question.formatDuration(avg));

        double varianceSum = 0;
        for (int i = 0; i < game.GetQuestions().size(); i++) {
            double diff = game.GetQuestions().get(i).Duration() - avg;
            varianceSum += diff * diff;
        }
        double stddev = Math.sqrt(varianceSum / game.QuestionCount());
        consistency.setText(Question.formatDuration(stddev));

        calculateRosettes(avg, stddev);

        done.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("game", game);

            startActivity(intent);
            finish();
        });
        if (game.getGameMode().shouldStore()) {
            saveResults(game.GetPlayerName(), game.MaxTable(), game.QuestionCount(), avg,stddev);
        }
    }

    private void calculateRosettes(double rawAvg, double rawStdev) {
        rosetteContainer.removeAllViews();

        boolean allSub5 = true;
        boolean allSub3 = true;
        boolean allSub15 = true;
        boolean allSub1 = true;
        for (Question qItem : game.GetQuestions()) {
            double d = qItem.Duration()/1_000_000_000.0;
            if (d >= 5.0) allSub5 = false;
            if (d >= 3.0) allSub3 = false;
            if (d >= 1.5) allSub15 = false;
            if (d >= 1.0) allSub1 = false;
        }

        double avg = rawAvg/1_000_000_000.0;
        if (avg < 1.0) {
            addRosette("AVG 1", getRankedColour(0.9));
        } else if (avg < 1.5) {
            addRosette("AVG 1.5", getRankedColour(1.4));
        } else if (avg < 3.0) {
            addRosette("AVG 3", getRankedColour(2.9));
        } else if (avg < 5.0) {
            addRosette("AVG 5", getRankedColour(4.9));
        }

        if (allSub1) {
            addRosette("All SUB 1", getRankedColour(0.9));
        } else if (allSub15) {
            addRosette("All SUB 1.5", getRankedColour(1.4));
        } else if (allSub3) {
            addRosette("All SUB 3", getRankedColour(2.9));
        } else if (allSub5) {
            addRosette("All SUB 5", getRankedColour(4.9));
        }

        if (game.GetQuestions().size() > 20) {
            double stdev = rawStdev/1_000_000_000.0;
            if (stdev < 0.15) {
                addRosette("STD 0.15", getRankedColour(0.9));
            } else if (stdev < 0.35) {
                addRosette("STD 0.35", getRankedColour(1.4));
            } else if (stdev < 0.6) {
                addRosette("STD 0.6", getRankedColour(2.9));
            } else if (stdev < 1.2) {
                addRosette("STD 1.2", getRankedColour(4.9));
            }
        }
    }

    private static int getRankedColour(double seconds) {
        if (seconds < 1.0) {
            return Color.parseColor("#10B981");
        } else if (seconds <= 1.5) {
            return Color.parseColor("#38BDF8");
        } else if (seconds <= 3.0) {
            return Color.parseColor("#2563EB");
        } else if (seconds <= 5.0) {
            return Color.parseColor("#94A3B8");
        } else if (seconds <= 10.0) {
            return Color.parseColor("#F59E0B");
        } else {
            return Color.parseColor("#EF4444");
        }
    }

    private void addRosette(String label, int color) {
        View rosetteView = LayoutInflater.from(this).inflate(R.layout.view_achievement_rosette, rosetteContainer, false);

        TextView center = rosetteView.findViewById(R.id.tv_rosette_center);
        TextView tails = rosetteView.findViewById(R.id.tv_rosette_tails);
        TextView labelTv = rosetteView.findViewById(R.id.tv_rosette_label);

        center.setBackgroundTintList(ColorStateList.valueOf(color));
        tails.setTextColor(color);
        labelTv.setText(label);
        labelTv.setTextColor(color);

        rosetteContainer.addView(rosetteView);
    }

    private void saveResults(String playerName, int maxTable, int totalQuestions, double avgSpeed, double standardDeviation) {
        AppDatabase db = AppDatabase.getDatabase(this);

        GameRecord newRecord = new GameRecord(
                playerName,
                maxTable,
                totalQuestions,
                avgSpeed,
                standardDeviation,
                System.currentTimeMillis()
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.gameHistoryDao().insertGame(newRecord);
        });
    }

    private static class QuestionSummaryAdapter extends RecyclerView.Adapter<QuestionSummaryAdapter.ViewHolder> {
        private final List<Question> questionList;

        QuestionSummaryAdapter(List<Question> questionList) {
            this.questionList = questionList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_end_question_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionSummaryAdapter.ViewHolder holder, int position) {
            Question q = questionList.get(position);

            holder.tvQuestion.setText(String.format(Locale.UK, "%d × %d = %d", q.first, q.second, q.Answer()));
            holder.tvTime.setText(Question.formatDuration(q.Duration()));

            double seconds = q.Duration()/1_000_000_000.0;
            holder.tvTime.setTextColor(getRankedColour(seconds));
        }

        @Override
        public int getItemCount() {
            return questionList != null ? questionList.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestion, tvTime;
            ViewHolder(View itemView) {
                super(itemView);
                tvQuestion = itemView.findViewById(R.id.tv_end_row_question);
                tvTime = itemView.findViewById(R.id.tv_end_row_time);
            }
        }
    }
}
