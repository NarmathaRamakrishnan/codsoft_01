package com.example.to_dolist;
import com.google.firebase.FirebaseApp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Locale;
import java.io.Serializable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private EditText dueDateEditText;
    private EditText dueTimeEditText;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTasksFromFirebase();
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_home);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);
        taskAdapter.setOnTaskClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromFirebase();
    }

    private void loadTasksFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("tasks");

            userTasksRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    taskList.clear();

                    for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                        TaskModel task = taskSnapshot.getValue(TaskModel.class);
                        if (task != null) {
                            taskList.add(task);
                        }
                    }

                    taskAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Home.this, "Failed to load tasks from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onTaskClick(TaskModel task) {
        if (!task.isCompleted()) {
            task.setCompleted(true);
            updateTaskCompletionInFirebase(task);
        }
    }

    @Override
    public void onDeleteClick(TaskModel task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteTaskFromFirebase(task);
            int position = taskList.indexOf(task);
            if (position != -1) {
                taskAdapter.deleteTask(position);
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.show();
    }

    @Override
    public void onEditClick(TaskModel task) {
        showEditTaskDialog(task);
    }

    private void addNewTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText taskNameEditText = dialogView.findViewById(R.id.taskNameEditText);
        final EditText taskDescriptionEditText = dialogView.findViewById(R.id.taskDescriptionEditText);
        dueDateEditText = dialogView.findViewById(R.id.dueDateEditText);
        dueTimeEditText = dialogView.findViewById(R.id.dueTimeEditText);

        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        dueTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        builder.setPositiveButton("Add Task", (dialog, which) -> {
            String taskName = taskNameEditText.getText().toString();
            String taskDescription = taskDescriptionEditText.getText().toString();
            String dueDate = dueDateEditText.getText().toString();
            String dueTime = dueTimeEditText.getText().toString();

            if (isValidTaskInput(taskName, taskDescription, dueDate, dueTime)) {
                saveTaskToFirebase(taskName, taskDescription, dueDate, dueTime, false);
            } else {
                Toast.makeText(Home.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        builder.show();
    }

    private boolean isValidTaskInput(String taskName, String taskDescription, String dueDate, String dueTime) {
        return !taskName.isEmpty() && !taskDescription.isEmpty() && !dueDate.isEmpty() && !dueTime.isEmpty();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                dueDateEditText.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay));
                showTimePickerDialog();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                dueTimeEditText.append(String.format(Locale.getDefault(), " %02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void updateTaskCompletionInFirebase(TaskModel task) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("tasks").child(task.getTaskId());

            userTasksRef.child("completed").setValue(true)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(Home.this, "Task completed successfully", Toast.LENGTH_SHORT).show();
                            taskAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Home.this, "Failed to complete task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void deleteTaskFromFirebase(TaskModel task) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("tasks").child(task.getTaskId());

            userTasksRef.removeValue()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(Home.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Home.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void showEditTaskDialog(TaskModel task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText taskNameEditText = dialogView.findViewById(R.id.taskNameEditText);
        final EditText taskDescriptionEditText = dialogView.findViewById(R.id.taskDescriptionEditText);
        dueDateEditText = dialogView.findViewById(R.id.dueDateEditText);
        dueTimeEditText = dialogView.findViewById(R.id.dueTimeEditText);
        taskNameEditText.setText(task.getTaskName());
        taskDescriptionEditText.setText(task.getTaskDescription());
        dueDateEditText.setText(task.getDueDate());
        dueTimeEditText.setText(task.getDueTime());

        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        dueTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        builder.setPositiveButton("Save Changes", (dialog, which) -> {
            String editedTaskName = taskNameEditText.getText().toString();
            String editedTaskDescription = taskDescriptionEditText.getText().toString();
            String editedDueDate = dueDateEditText.getText().toString();
            String editedDueTime = dueTimeEditText.getText().toString();
            updateTaskInFirebase(task, editedTaskName, editedTaskDescription, editedDueDate, editedDueTime);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        builder.show();
    }
    private void updateTaskInFirebase(TaskModel task, String editedTaskName, String editedTaskDescription, String editedDueDate, String editedDueTime) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("tasks").child(task.getTaskId());

            task.setTaskName(editedTaskName);
            task.setTaskDescription(editedTaskDescription);
            task.setDueDate(editedDueDate);
            task.setDueTime(editedDueTime);

            userTasksRef.setValue(task)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(Home.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Home.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                        }
                    });

            int position = taskList.indexOf(task);
            if (position != -1) {
                taskAdapter.notifyItemChanged(position);
                taskAdapter.updateTask(task);
            }
        }
    }

    public static class TaskModel implements Serializable {
        private String taskId;
        private String taskName;
        private String taskDescription;
        private String dueDate;
        private String dueTime;
        private boolean completed;

        public TaskModel() {
        }

        public TaskModel(String taskId, String taskName, String taskDescription, String dueDate, String dueTime) {
            this.taskId = taskId;
            this.taskName = taskName;
            this.taskDescription = taskDescription;
            this.dueDate = dueDate;
            this.dueTime = dueTime;
            this.completed = false;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getDueTime() {
            return dueTime;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public void setTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public void setDueTime(String dueTime) {
            this.dueTime = dueTime;
        }
    }


    private void saveTaskToFirebase(String taskName, String taskDescription, String dueDate, String dueTime, boolean completed) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("tasks");
            String taskId = userTasksRef.push().getKey();

            TaskModel task = new TaskModel(taskId, taskName, taskDescription, dueDate, dueTime);
            task.setCompleted(completed);
            taskList.add(task);
            taskAdapter.notifyDataSetChanged();

            userTasksRef.child(taskId).setValue(task)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(Home.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Home.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
