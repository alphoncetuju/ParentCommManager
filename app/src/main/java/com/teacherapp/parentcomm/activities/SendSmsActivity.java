package com.teacherapp.parentcomm.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.Learner;
import com.teacherapp.parentcomm.models.Message;

public class SendSmsActivity extends AppCompatActivity {

    private Learner learner;
    private EditText etMessage;
    private TextView tvLearnerInfo;
    private DatabaseHelper db;

    private static final String[] TEMPLATES = {
            "Custom message...",
            "Hello [Parent], this message concerns [FirstName]. Please assist the learner regarding school matters.",
            "Dear [Parent], [FirstName] has been absent today. Please contact the school.",
            "Hello [Parent], [FirstName]'s performance needs attention. Please schedule a meeting.",
            "Dear [Parent], please ensure [FirstName] brings [item] tomorrow.",
            "Hello [Parent], [FirstName] has outstanding fees. Please settle at your earliest convenience.",
            "Dear [Parent], [FirstName] has done excellent work this week. Well done!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        db = DatabaseHelper.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Send SMS");
        }

        tvLearnerInfo = findViewById(R.id.tv_sms_learner_info);
        etMessage = findViewById(R.id.et_sms_message);
        Spinner spinnerTemplates = findViewById(R.id.spinner_templates);
        Button btnSend = findViewById(R.id.btn_send_sms);

        long learnerId = getIntent().getLongExtra("learner_id", -1);
        if (learnerId != -1) {
            learner = db.getLearnerById(learnerId);
        }

        if (learner == null) {
            Toast.makeText(this, "Learner not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvLearnerInfo.setText("To: " + learner.getParentName() + "  |  " + learner.getParentPhone()
                + "\nLearner: " + learner.getFullName());

        // Templates spinner
        ArrayAdapter<String> templateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TEMPLATES);
        templateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplates.setAdapter(templateAdapter);

        spinnerTemplates.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) return;
                String template = TEMPLATES[pos]
                        .replace("[Parent]", learner.getParentName() != null ? learner.getParentName() : "Parent")
                        .replace("[FirstName]", learner.getFirstName() != null ? learner.getFirstName() : learner.getFullName());
                etMessage.setText(template);
                etMessage.setSelection(etMessage.getText().length());
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnSend.setOnClickListener(v -> sendSms());
    }

    private void sendSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        String msgText = etMessage.getText().toString().trim();
        if (msgText.isEmpty()) {
            etMessage.setError("Message cannot be empty");
            return;
        }

        String phone = learner.getParentPhone();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, msgText, null, null);

            // Save to DB
            Message msg = new Message(learner.getId(), learner.getFullName(),
                    learner.getParentName(), phone, msgText, "SENT");
            db.addMessage(msg);

            Toast.makeText(this, "SMS sent to " + learner.getParentName(), Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
