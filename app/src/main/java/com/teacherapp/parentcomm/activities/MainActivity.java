package com.teacherapp.parentcomm.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.adapters.LearnerAdapter;
import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.CallLog;
import com.teacherapp.parentcomm.models.Learner;
import com.teacherapp.parentcomm.utils.CallTracker;
import com.teacherapp.parentcomm.utils.ExportUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LearnerAdapter.OnLearnerActionListener {

    private static final int REQ_PERMISSIONS = 100;
    private static final int REQ_ADD_LEARNER = 200;
    private static final int REQ_EDIT_LEARNER = 201;

    private RecyclerView recyclerView;
    private LearnerAdapter adapter;
    private DatabaseHelper db;
    private TextView tvEmpty, tvStats;
    private EditText etSearch;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        recyclerView = findViewById(R.id.recycler_learners);
        tvEmpty = findViewById(R.id.tv_empty);
        tvStats = findViewById(R.id.tv_stats);
        etSearch = findViewById(R.id.et_search);

        adapter = new LearnerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // FAB - Add Learner
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditLearnerActivity.class);
            startActivityForResult(i, REQ_ADD_LEARNER);
        });

        // Bulk SMS Button
        findViewById(R.id.btn_bulk_sms).setOnClickListener(v -> {
            Intent i = new Intent(this, BulkSmsActivity.class);
            startActivity(i);
        });

        // History Button
        findViewById(R.id.btn_history).setOnClickListener(v -> {
            Intent i = new Intent(this, HistoryActivity.class);
            startActivity(i);
        });

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                adapter.filter(s.toString());
                updateEmptyView();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        requestPermissionsIfNeeded();
        loadLearners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLearners();
        // Check for pending call log completion
        CallTracker.checkPendingCall(this);
    }

    private void loadLearners() {
        List<Learner> list = db.getAllLearners();
        adapter.setLearners(list);
        updateEmptyView();
        updateStats();
    }

    private void updateEmptyView() {
        boolean isEmpty = adapter.getItemCount() == 0;
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updateStats() {
        int learners = db.getLearnerCount();
        int calls = db.getCallCount();
        int msgs = db.getMessageCount();
        tvStats.setText("👨‍🎓 " + learners + " Learners  |  📞 " + calls + " Calls  |  ✉️ " + msgs + " Messages");
    }

    // =============== LEARNER ACTIONS ===============

    @Override
    public void onCall(Learner learner) {
        if (!hasPermission(Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQ_PERMISSIONS);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Call Parent")
                .setMessage("Call " + learner.getParentName() + "\n" + learner.getParentPhone() + "\nfor learner " + learner.getFullName() + "?")
                .setPositiveButton("Call", (d, w) -> {
                    CallTracker.setPendingCall(learner);
                    Intent intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + learner.getParentPhone()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onSms(Learner learner) {
        Intent i = new Intent(this, SendSmsActivity.class);
        i.putExtra("learner_id", learner.getId());
        startActivity(i);
    }

    @Override
    public void onEdit(Learner learner) {
        Intent i = new Intent(this, AddEditLearnerActivity.class);
        i.putExtra("learner_id", learner.getId());
        startActivityForResult(i, REQ_EDIT_LEARNER);
    }

    @Override
    public void onDelete(Learner learner) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Learner")
                .setMessage("Delete " + learner.getFullName() + " and all their records?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteLearner(learner.getId());
                    loadLearners();
                    Toast.makeText(this, "Learner deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // =============== MENU ===============

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_history) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        } else if (id == R.id.menu_export) {
            exportData();
            return true;
        } else if (id == R.id.menu_bulk_sms) {
            startActivity(new Intent(this, BulkSmsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportData() {
        new AlertDialog.Builder(this)
                .setTitle("Export Data")
                .setMessage("Export all learner records to CSV file?")
                .setPositiveButton("Export", (d, w) -> {
                    String path = ExportUtils.exportLearnersToCSV(this, db.getAllLearners());
                    if (path != null) {
                        Toast.makeText(this, "Exported to: " + path, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // =============== PERMISSIONS ===============

    private void requestPermissionsIfNeeded() {
        boolean allGranted = true;
        for (String p : REQUIRED_PERMISSIONS) {
            if (!hasPermission(p)) { allGranted = false; break; }
        }
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQ_PERMISSIONS);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Silently continue — individual features will check again
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) loadLearners();
    }
}
