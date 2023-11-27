package com.example.taskmanager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private static List<Task> taskList;
    private final OnTaskItemClickListener listener;

    public TaskAdapter(List<Task> taskList, OnTaskItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    public List<Task> getTaskList() {
        return taskList;
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);

        holder.statusTextView.setOnClickListener(v -> {
            boolean newStatus = !task.isStatus();
            if (listener != null) {
                listener.onTaskStatusUpdate(position, newStatus);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });

        holder.updateButton.setOnClickListener(v -> {
            boolean newStatus = !task.isStatus();
            if (listener != null) {
                listener.onTaskStatusUpdate(position, newStatus);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTaskLongClick(task);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }


    public interface OnTaskItemClickListener {
        void onTaskClick(Task task);

        void onTaskLongClick(Task task);

        void onTaskStatusUpdate(int position, boolean newStatus);

    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tagTextView;
        private final TextView descriptionTextView;
        private final TextView deadlineTextView;
        private final TextView statusTextView;

        private final Button updateButton;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.text_view_task);
            descriptionTextView = itemView.findViewById(R.id.text_view_description);
            deadlineTextView = itemView.findViewById(R.id.text_view_date);
            statusTextView = itemView.findViewById(R.id.text_view_status);
            updateButton = itemView.findViewById(R.id.button_update_status);

        }


        public void bind(Task task) {
            tagTextView.setText(task.getTag());
            descriptionTextView.setText(task.getDescription());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(task.getDeadline());
            deadlineTextView.setText(formattedDate);

            String statusText = task.isStatus() ? "Complete" : "Incomplete";
            statusTextView.setText(statusText);

            updateButton.setOnClickListener(v -> {
                boolean newStatus = !task.isStatus();
                if (listener != null) {
                    listener.onTaskStatusUpdate(getAdapterPosition(), newStatus);
                }
            });
        }

    }
}

