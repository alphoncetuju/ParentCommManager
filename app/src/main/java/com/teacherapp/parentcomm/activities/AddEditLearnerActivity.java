package com.teacherapp.parentcomm.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.Learner;

public class AddEditLearnerActivity extends AppCompatActivity {

    private EditText etFullName, etParentName, etPhone, etClass;
    private DatabaseHelper db;
    private long learnerId = -1;
    private Learner existingLearner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_learner);

        db = DatabaseHelper.getInstance(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etFullName = findViewById(R.id.et_full_name);
        etParentName = findViewById(R.id.et_parent_name);
        etPhone = findViewById(R.id.et_phone);
        etClass = findViewById(R.id.et_class);
        Button btnSave = findViewById(R.id.btn_save);

        learnerId = getIntent().getLongExtra("learner_id", -1);
        if (learnerId != -1) {
            setTitle("Edit Learner");
            existingLearner = db.getLearnerById(learnerId);
            if (existingLearner != null) {
                etFullName.setText(existingLearner.getFullName());
                etParentName.setText(existingLearner.getParentName());
                etPhone.setText(existingLearner.getParentPhone());
                etClass.setText(existingLearner.getClassName());
            }
        } else {
            setTitle("Add Learner");
        }

        btnSave.setOnClickListener(v -> saveLearner());
    }

    private void saveLearner() {
        String fullName = etFullName.getText().toString().trim();
        String parentName = etParentName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String className = etClass.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Learner full name is required");
            etFullName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Parent phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (learnerId == -1) {
            Learner learner = new Learner(fullName, parentName, phone, className);
            long id = db.addLearner(learner);
            if (id > 0) {
                Toast.makeText(this, "Learner added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add learner", Toast.LENGTH_SHORT).show();
            }
        } else {
            existingLearner.setFullName(fullName);
            existingLearner.setParentName(parentName);
            existingLearner.setParentPhone(phone);
            existingLearner.setClassName(className);
            int rows = db.updateLearner(existingLearner);
            if (rows > 0) {
                Toast.makeText(this, "Learner updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update learner", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
