package com.teacherapp.parentcomm.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.models.CallLog;
import com.teacherapp.parentcomm.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CALL = 0;
    private static final int TYPE_MESSAGE = 1;

    public static class HistoryItem {
        public final int type;
        public final CallLog callLog;
        public final Message message;
        public final long dateTime;

        public HistoryItem(CallLog c) { type = TYPE_CALL; callLog = c; message = null; dateTime = c.getDateTime(); }
        public HistoryItem(Message m) { type = TYPE_MESSAGE; message = m; callLog = null; dateTime = m.getDateTime(); }
    }

    private List<HistoryItem> items = new ArrayList<>();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    public void setItems(List<HistoryItem> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) { return items.get(position).type; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CALL) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_call_log, parent, false);
            return new CallViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_log, parent, false);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        if (item.type == TYPE_CALL) {
            ((CallViewHolder) holder).bind(item.callLog);
        } else {
            ((MessageViewHolder) holder).bind(item.message);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class CallViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvLearner, tvParent, tvPhone, tvDateTime, tvDuration;

        CallViewHolder(View v) {
            super(v);
            tvType = v.findViewById(R.id.tv_call_type);
            tvLearner = v.findViewById(R.id.tv_call_learner);
            tvParent = v.findViewById(R.id.tv_call_parent);
            tvPhone = v.findViewById(R.id.tv_call_phone);
            tvDateTime = v.findViewById(R.id.tv_call_datetime);
            tvDuration = v.findViewById(R.id.tv_call_duration);
        }

        void bind(CallLog c) {
            tvType.setText("📞 " + (c.getCallType() != null ? c.getCallType() : "CALL"));
            tvLearner.setText("Learner: " + c.getLearnerName());
            tvParent.setText("Parent: " + c.getParentName());
            tvPhone.setText(c.getPhoneNumber());
            tvDateTime.setText(SDF.format(new Date(c.getDateTime())));
            tvDuration.setText("Duration: " + c.getFormattedDuration());
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvDirection, tvLearner, tvParent, tvPhone, tvDateTime, tvContent;

        MessageViewHolder(View v) {
            super(v);
            tvDirection = v.findViewById(R.id.tv_msg_direction);
            tvLearner = v.findViewById(R.id.tv_msg_learner);
            tvParent = v.findViewById(R.id.tv_msg_parent);
            tvPhone = v.findViewById(R.id.tv_msg_phone);
            tvDateTime = v.findViewById(R.id.tv_msg_datetime);
            tvContent = v.findViewById(R.id.tv_msg_content);
        }

        void bind(Message m) {
            boolean isSent = "SENT".equals(m.getDirection());
            tvDirection.setText(isSent ? "✉️ SENT" : "📩 RECEIVED");
            tvDirection.setTextColor(isSent ? Color.parseColor("#1976D2") : Color.parseColor("#388E3C"));
            tvLearner.setText("Learner: " + m.getLearnerName());
            tvParent.setText("Parent: " + m.getParentName());
            tvPhone.setText(m.getPhoneNumber());
            tvDateTime.setText(SDF.format(new Date(m.getDateTime())));
            tvContent.setText(m.getContent());
        }
    }
}
