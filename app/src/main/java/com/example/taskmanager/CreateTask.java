package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateTask extends AppCompatActivity {

    private EditText taskTag, taskDescription, taskDeadline;
    private CheckBox taskStatus;
    private Button createTask;
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        taskTag = findViewById(R.id.task_tag);
        taskDescription = findViewById(R.id.task_description);
        taskDeadline = findViewById(R.id.task_deadline);
        taskStatus = findViewById(R.id.task_status);
        createTask = findViewById(R.id.task_button);

        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
    }

    private void createTask() {
        String tag = taskTag.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String deadlineStr = taskDeadline.getText().toString().trim();
        boolean status = taskStatus.isChecked();

        // Validate and process deadline
        Date deadline = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            deadline = sdf.parse(deadlineStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }
        // Firebase Firestore Collection Reference
        CollectionReference tasksRef = db.collection("tasks");

        // Create a Task object
        Map<String, Object> task = new HashMap<>();
        task.put("tag", tag);
        task.put("description", description);
        task.put("deadline", deadline);
        task.put("status", status);
        task.put("userId", auth.getCurrentUser().getUid());

        // Add the task to Firestore
        tasksRef.add(task)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateTask.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields after creating task
                    taskTag.getText().clear();
                    taskDescription.getText().clear();
                    taskDeadline.getText().clear();
                    taskStatus.setChecked(false);
                })
                .addOnFailureListener(e -> Toast.makeText(CreateTask.this, "Failed to create task", Toast.LENGTH_SHORT).show());



    }
}