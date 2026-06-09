package com.moltenwolfcub.timestables;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KeypadAdapter extends RecyclerView.Adapter<KeypadAdapter.KeyViewHolder> {

    // Interface to communicate button clicks back to your Activity
    public interface OnKeyClickListener {
        void onKeyClick(String key);
    }

    private final List<String> keyList;
    private final OnKeyClickListener listener;

    public KeypadAdapter(List<String> keyList, OnKeyClickListener listener) {
        this.keyList = keyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_keypad_key, parent, false);
        return new KeyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        String keyLabel = keyList.get(position);
        holder.buttonKey.setText(keyLabel);

        // Single click listener manages all interactions
        holder.buttonKey.setOnClickListener(v -> {
            if (listener != null) {
                listener.onKeyClick(keyLabel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public static class KeyViewHolder extends RecyclerView.ViewHolder {
        Button buttonKey;

        public KeyViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonKey = itemView.findViewById(R.id.btn_key);
        }
    }
}
