package com.teacherapp.parentcomm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teacherapp.parentcomm.R;
import com.teacherapp.parentcomm.models.Learner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LearnerAdapter extends RecyclerView.Adapter<LearnerAdapter.ViewHolder> {

    public interface OnLearnerActionListener {
        void onCall(Learner learner);
        void onSms(Learner learner);
        void onEdit(Learner learner);
        void onDelete(Learner learner);
    }

    private List<Learner> learners = new ArrayList<>();
    private List<Learner> allLearners = new ArrayList<>();
    private OnLearnerActionListener listener;
    private boolean selectionMode = false;
    private Set<Long> selectedIds = new HashSet<>();

    public LearnerAdapter(OnLearnerActionListener listener) {
        this.listener = listener;
    }

    public void setLearners(List<Learner> list) {
        this.allLearners = new ArrayList<>(list);
        this.learners = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            learners = new ArrayList<>(allLearners);
        } else {
            String q = query.toLowerCase().trim();
            List<Learner> filtered = new ArrayList<>();
            for (Learner l : allLearners) {
                if (l.getFullName().toLowerCase().contains(q)
                        || (l.getParentName() != null && l.getParentName().toLowerCase().contains(q))
                        || (l.getClassName() != null && l.getClassName().toLowerCase().contains(q))
                        || (l.getParentPhone() != null && l.getParentPhone().contains(q))) {
                    filtered.add(l);
                }
            }
            learners = filtered;
        }
        notifyDataSetChanged();
    }

    public void setSelectionMode(boolean enabled) {
        this.selectionMode = enabled;
        if (!enabled) selectedIds.clear();
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() { return selectionMode; }

    public List<Learner> getSelectedLearners() {
        List<Learner> selected = new ArrayList<>();
        for (Learner l : learners) {
            if (selectedIds.contains(l.getId())) selected.add(l);
        }
        return selected;
    }

    public List<Learner> getAllDisplayedLearners() { return learners; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_learner, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Learner l = learners.get(position);

        h.tvName.setText(l.getFullName());
        h.tvParent.setText("Parent: " + (l.getParentName() != null ? l.getParentName() : "—"));
        h.tvPhone.setText(l.getParentPhone() != null ? l.getParentPhone() : "—");
        h.tvClass.setText(l.getClassName() != null && !l.getClassName().isEmpty()
                ? "Class: " + l.getClassName() : "");

        h.btnCall.setOnClickListener(v -> listener.onCall(l));
        h.btnSms.setOnClickListener(v -> listener.onSms(l));
        h.btnEdit.setOnClickListener(v -> listener.onEdit(l));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(l));

        if (selectionMode) {
            h.checkBox.setVisibility(View.VISIBLE);
            h.checkBox.setChecked(selectedIds.contains(l.getId()));
            h.checkBox.setOnCheckedChangeListener((cb, checked) -> {
                if (checked) selectedIds.add(l.getId());
                else selectedIds.remove(l.getId());
            });
        } else {
            h.checkBox.setVisibility(View.GONE);
            h.checkBox.setOnCheckedChangeListener(null);
        }
    }

    @Override
    public int getItemCount() { return learners.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvParent, tvPhone, tvClass;
        ImageButton btnCall, btnSms, btnEdit, btnDelete;
        CheckBox checkBox;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_learner_name);
            tvParent = v.findViewById(R.id.tv_parent_name);
            tvPhone = v.findViewById(R.id.tv_phone);
            tvClass = v.findViewById(R.id.tv_class);
            btnCall = v.findViewById(R.id.btn_call);
            btnSms = v.findViewById(R.id.btn_sms);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
            checkBox = v.findViewById(R.id.checkbox_select);
        }
    }
}
