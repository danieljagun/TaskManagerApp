package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewTask extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        recyclerView = findViewById(R.id.recycler_view);

        taskAdapter = new TaskAdapter(new ArrayList<>(), new TaskAdapter.OnTaskItemClickListener() {

            @Override
            public void onTaskClick(Task task) {
            }

            @Override
            public void onTaskLongClick(Task task) {

                deleteTaskFromFirestore(task);
            }

            @Override
            public void onTaskStatusUpdate(int position, boolean newStatus) {

                Task updatedTask = taskAdapter.getTaskList().get(position);
                updatedTask.setStatus(newStatus);

                updateTaskStatusInFirestore(updatedTask);
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);


        db = FirebaseFirestore.getInstance();

        fetchTasksForCurrentUser();


        FloatingActionButton fab = findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTask.this, CreateTask.class); // Replace YourOtherActivity with the activity you want to launch
                startActivity(intent);
            }
        });

    }

    private void updateTaskStatusInFirestore(Task task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference taskRef = db.collection("tasks").document(task.getTaskId());

        taskRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        boolean currentStatus = documentSnapshot.getBoolean("status");

                        boolean newStatus = !currentStatus;

                        taskRef.update("status", newStatus)
                                .addOnSuccessListener(aVoid -> {
                                    task.setStatus(newStatus);
                                    taskAdapter.notifyItemChanged(taskAdapter.getTaskList().indexOf(task));
                                    Toast.makeText(ViewTask.this, "Task status updated", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(ViewTask.this, "Failed to update task status ", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                });
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(ViewTask.this, "Failed to retrieve task details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void deleteTaskFromFirestore(Task task){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tasks").document(task.getTaskId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    taskAdapter.getTaskList().remove(task);
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void fetchTasksForCurrentUser() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("tasks")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Task> tasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Task task = document.toObject(Task.class);
                            task.setTaskId(document.getId());
                            tasks.add(task);
                        }

                        taskAdapter.setTaskList(tasks);
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_task) {
            Toast.makeText(ViewTask.this, "Loading Task Screen", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ViewTask.this, CreateTask.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_seetasks) {
            Toast.makeText(ViewTask.this, "Loading Tasks", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ViewTask.this, CreateTask.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}

