package com.example.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equalsIgnoreCase("complete")) {
                    filterTasksByStatus(true);
                } else if (newText.equalsIgnoreCase("incomplete")) {
                    filterTasksByStatus(false);
                } else {
                    filterTasksByTag(newText);
                }
                return false;
            }
        });
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

    private void filterTasksByTag(String query) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("tasks").whereEqualTo("userId", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<Task> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Task task = document.toObject(Task.class);
                    task.setTaskId(document.getId());

                    if (task.getTag().toLowerCase().contains(query.toLowerCase())) {
                        tasks.add(task);
                    }
                }

                taskAdapter.setTaskList(tasks);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void filterTasksByStatus(boolean isComplete) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("tasks").whereEqualTo("userId", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<Task> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Task task = document.toObject(Task.class);
                    task.setTaskId(document.getId());

                    boolean taskStatus = task.isStatus();

                    if (taskStatus == isComplete) {
                        tasks.add(task);
                    }
                }

                taskAdapter.setTaskList(tasks);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateTaskStatusInFirestore(Task task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference taskRef = db.collection("tasks").document(task.getTaskId());

        taskRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                boolean currentStatus = documentSnapshot.getBoolean("status");

                boolean newStatus = !currentStatus;

                taskRef.update("status", newStatus).addOnSuccessListener(aVoid -> {
                    task.setStatus(newStatus);
                    taskAdapter.notifyItemChanged(taskAdapter.getTaskList().indexOf(task));
                    Toast.makeText(ViewTask.this, "Task status updated", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {

                    Toast.makeText(ViewTask.this, "Failed to update task status ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
            }
        }).addOnFailureListener(e -> {

            Toast.makeText(ViewTask.this, "Failed to retrieve task details", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    private void deleteTaskFromFirestore(Task task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tasks").document(task.getTaskId()).delete().addOnSuccessListener(aVoid -> {

            taskAdapter.getTaskList().remove(task);
            taskAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {

            Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    private void fetchTasksForCurrentUser() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("tasks").whereEqualTo("userId", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<Task> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Task task = document.toObject(Task.class);
                    task.setTaskId(document.getId());
                    tasks.add(task);
                }

                taskAdapter.setTaskList(tasks);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch tasks. Please try again later.", Toast.LENGTH_SHORT).show();

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
            Intent intent = new Intent(ViewTask.this, ViewTask.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_homepage) {
            Toast.makeText(ViewTask.this, "Returning to homepage", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ViewTask.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}

