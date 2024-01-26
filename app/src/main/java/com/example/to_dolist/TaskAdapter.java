package com.example.to_dolist;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.to_dolist.Home.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<Home.TaskModel> taskList;
    private OnTaskClickListener onTaskClickListener;

    public interface OnTaskClickListener {
        void onTaskClick(Home.TaskModel task);

        void onDeleteClick(Home.TaskModel task);

        void onEditClick(Home.TaskModel task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    public TaskAdapter(List<Home.TaskModel> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Home.TaskModel task = taskList.get(position);

        holder.taskNameTextView.setText(task.getTaskName());
        holder.descriptionTextView.setText(task.getTaskDescription());
        holder.dueDateTextView.setText("Due Date: " + task.getDueDate());
        holder.dueTimeTextView.setText("Due Time: " + task.getDueTime());

        holder.completeImageView.setOnClickListener(view -> {
            if (onTaskClickListener != null) {
                onTaskClickListener.onTaskClick(task);
            }
        });

        holder.deleteImageView.setOnClickListener(view -> {
            if (onTaskClickListener != null) {
                onTaskClickListener.onDeleteClick(task);
            }
        });

        holder.editImageView.setOnClickListener(view -> {
            if (onTaskClickListener != null) {
                onTaskClickListener.onEditClick(task);
            }
        });
        updateCompletionUI(holder.completeImageView, task.isCompleted());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskNameTextView;
        public TextView descriptionTextView;
        public TextView dueDateTextView;
        public TextView dueTimeTextView;
        public ImageView completeImageView;
        public ImageView deleteImageView;
        public ImageView editImageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNameTextView = itemView.findViewById(R.id.taskname);
            descriptionTextView = itemView.findViewById(R.id.description);
            dueDateTextView = itemView.findViewById(R.id.duedate);
            dueTimeTextView = itemView.findViewById(R.id.duetime);
            completeImageView = itemView.findViewById(R.id.complete);
            deleteImageView = itemView.findViewById(R.id.delete);
            editImageView = itemView.findViewById(R.id.edit);
        }
    }

    public void deleteTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    private void updateCompletionUI(ImageView completeImageView, boolean isCompleted) {
        if (isCompleted) {
            completeImageView.setImageResource(R.drawable.baseline_check_circle_24);
        } else {
            completeImageView.setImageResource(R.drawable.baseline_check_circle_outline_24);
        }
    }
    public void updateTask(TaskModel task) {
        int position = taskList.indexOf(task);
        if (position != -1) {
            taskList.set(position, task);
            notifyItemChanged(position);
        }
    }
}
