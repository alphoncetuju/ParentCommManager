package com.teacherapp.parentcomm.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.adapters.HistoryAdapter;
import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.CallLog;
import com.teacherapp.parentcomm.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private HistoryAdapter adapter;
    private DatabaseHelper db;
    private Button btnAll, btnCalls, btnMessages;
    private TextView tvEmpty;

    private static final int TAB_ALL = 0, TAB_CALLS = 1, TAB_MESSAGES = 2;
    private int currentTab = TAB_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = DatabaseHelper.getInstance(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Communication History");
        }

        recycler = findViewById(R.id.recycler_history);
        tvEmpty = findViewById(R.id.tv_history_empty);
        btnAll = findViewById(R.id.btn_tab_all);
        btnCalls = findViewById(R.id.btn_tab_calls);
        btnMessages = findViewById(R.id.btn_tab_messages);

        adapter = new HistoryAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnAll.setOnClickListener(v -> switchTab(TAB_ALL));
        btnCalls.setOnClickListener(v -> switchTab(TAB_CALLS));
        btnMessages.setOnClickListener(v -> switchTab(TAB_MESSAGES));

        switchTab(TAB_ALL);
    }

    private void switchTab(int tab) {
        currentTab = tab;
        btnAll.setSelected(tab == TAB_ALL);
        btnCalls.setSelected(tab == TAB_CALLS);
        btnMessages.setSelected(tab == TAB_MESSAGES);

        List<HistoryAdapter.HistoryItem> items = new ArrayList<>();

        if (tab == TAB_ALL || tab == TAB_CALLS) {
            for (CallLog c : db.getAllCallLogs()) {
                items.add(new HistoryAdapter.HistoryItem(c));
            }
        }
        if (tab == TAB_ALL || tab == TAB_MESSAGES) {
            for (Message m : db.getAllMessages()) {
                items.add(new HistoryAdapter.HistoryItem(m));
            }
        }

        // Sort by dateTime descending
        Collections.sort(items, (a, b) -> Long.compare(b.dateTime, a.dateTime));

        adapter.setItems(items);

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
