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

