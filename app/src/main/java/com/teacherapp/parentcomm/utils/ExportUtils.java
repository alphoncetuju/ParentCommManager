package com.teacherapp.parentcomm.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.teacherapp.parentcomm.models.Learner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportUtils {
    private static final String TAG = "ExportUtils";

    public static String exportLearnersToCSV(Context context, List<Learner> learners) {
        try {
            String fileName = "learners_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";

            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            } else {
                dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), "ParentComm");
                if (!dir.exists()) dir.mkdirs();
            }

            if (dir == null) dir = context.getFilesDir();
            File file = new File(dir, fileName);

            FileWriter writer = new FileWriter(file);
            writer.append("Full Name,First Name,Parent Name,Phone Number,Class\n");
            for (Learner l : learners) {
                writer.append(csvEscape(l.getFullName())).append(",")
                        .append(csvEscape(l.getFirstName())).append(",")
                        .append(csvEscape(l.getParentName())).append(",")
                        .append(csvEscape(l.getParentPhone())).append(",")
                        .append(csvEscape(l.getClassName())).append("\n");
            }
            writer.flush();
            writer.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Export failed: " + e.getMessage());
            return null;
        }
    }

    private static String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
