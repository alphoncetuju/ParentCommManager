package com.teacherapp.parentcomm.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.adapters.LearnerAdapter;
import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.Learner;
import com.teacherapp.parentcomm.models.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BulkSmsActivity extends AppCompatActivity implements LearnerAdapter.OnLearnerActionListener {

    private LearnerAdapter adapter;
    private DatabaseHelper db;
    private EditText etMessage;
    private TextView tvCount;
    private ProgressBar progressBar;

    private static final String[] TEMPLATES = {
            "Custom message...",
            "Dear [Parent], this is an important school notice. Please contact us.",
            "Hello [Parent], [FirstName] is required to attend school tomorrow.",
            "Dear [Parent], fees are due this week. Please settle urgently.",
            "Hello [Parent], school closes early today at 12:00. Please make arrangements.",
            "Dear [Parent], [FirstName] has been selected for a special programme. Well done!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_sms);

        db = DatabaseHelper.getInstance(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Bulk SMS");
        }

        RecyclerView recycler = findViewById(R.id.recycler_bulk);
        etMessage = findViewById(R.id.et_bulk_message);
        tvCount = findViewById(R.id.tv_selected_count);
        progressBar = findViewById(R.id.progress_bulk);
        Button btnSendSelected = findViewById(R.id.btn_send_selected);
        Button btnSendAll = findViewById(R.id.btn_send_all);
        Button btnSelectAll = findViewById(R.id.btn_select_all);
        Spinner spinnerTemplates = findViewById(R.id.spinner_bulk_templates);

        adapter = new LearnerAdapter(this);
        adapter.setSelectionMode(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        List<Learner> all = db.getAllLearners();
        adapter.setLearners(all);
        updateCount();

        // Templates
        ArrayAdapter<String> tAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TEMPLATES);
        tAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplates.setAdapter(tAdapter);
        spinnerTemplates.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                if (pos != 0) etMessage.setText(TEMPLATES[pos]);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnSelectAll.setOnClickListener(v -> {
            // re-set learners to trigger all checkboxes
            adapter.setSelectionMode(false);
            adapter.setLearners(all);
            adapter.setSelectionMode(true);
            // select all by tapping checkboxes programmatically via filter trick
            // Easiest: send to all directly uses getAllDisplayedLearners
            Toast.makeText(this, "Use 'Send to All' to message everyone", Toast.LENGTH_SHORT).show();
        });

        btnSendSelected.setOnClickListener(v -> {
            List<Learner> selected = adapter.getSelectedLearners();
            if (selected.isEmpty()) {
                Toast.makeText(this, "No learners selected", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmAndSend(selected, "Send to " + selected.size() + " selected parents?");
        });

        btnSendAll.setOnClickListener(v -> {
            List<Learner> allLearners = adapter.getAllDisplayedLearners();
            if (allLearners.isEmpty()) {
                Toast.makeText(this, "No learners in list", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmAndSend(allLearners, "Send to ALL " + allLearners.size() + " parents?");
        });
    }

    private void updateCount() {
        int total = adapter.getAllDisplayedLearners().size();
        tvCount.setText("Total: " + total + " parents");
    }

    private void confirmAndSend(List<Learner> recipients, String confirmMsg) {
        String msgTemplate = etMessage.getText().toString().trim();
        if (msgTemplate.isEmpty()) {
            etMessage.setError("Message cannot be empty");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Bulk SMS")
                .setMessage(confirmMsg + "\n\nMessage:\n" + msgTemplate)
                .setPositiveButton("Send", (d, w) -> sendBulk(recipients, msgTemplate))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendBulk(List<Learner> recipients, String template) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(recipients.size());
        progressBar.setProgress(0);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            int sent = 0, failed = 0;
            SmsManager smsManager = SmsManager.getDefault();

            for (int i = 0; i < recipients.size(); i++) {
                Learner l = recipients.get(i);
                String personalised = template
                        .replace("[Parent]", l.getParentName() != null ? l.getParentName() : "Parent")
                        .replace("[FirstName]", l.getFirstName() != null ? l.getFirstName() : l.getFullName());

                try {
                    smsManager.sendTextMessage(l.getParentPhone(), null, personalised, null, null);
                    Message msg = new Message(l.getId(), l.getFullName(), l.getParentName(),
                            l.getParentPhone(), personalised, "SENT");
                    db.addMessage(msg);
                    sent++;
                } catch (Exception e) {
                    failed++;
                }

                final int progress = i + 1;
                handler.post(() -> progressBar.setProgress(progress));

                // Small delay to avoid flooding
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }

            final int finalSent = sent, finalFailed = failed;
            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this,
                        "Done! Sent: " + finalSent + (finalFailed > 0 ? ", Failed: " + finalFailed : ""),
                        Toast.LENGTH_LONG).show();
                if (finalSent > 0) finish();
            });
        });
    }

    @Override public void onCall(Learner learner) {}
    @Override public void onSms(Learner learner) {}
    @Override public void onEdit(Learner learner) {}
    @Override public void onDelete(Learner learner) {}

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
