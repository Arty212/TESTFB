package com.example.it2019firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private RecyclerView listLevels;
    private LevelsAdapter listLevelsAdapter;

    private DatabaseReference reference;

    private ArrayList<Level> levels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);

        listLevels = findViewById(R.id.list_levels);
        listLevels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listLevelsAdapter = new LevelsAdapter();
        listLevels.setAdapter(listLevelsAdapter);
        listLevelsAdapter.setOnItemClickListener(new LevelsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, Level level) {
                Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
                intent.putExtra(LevelActivity.LEVEL_KEY, level.getKey());
                startActivity(intent);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference();
        loadLevels();
    }

    private void loadLevels() {
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.show();
        reference.child("levels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Level level;
                    for (DataSnapshot levelData : dataSnapshot.getChildren()) {
                        level = new Level();
                        level.setKey(levelData.getKey());
                        level.setName(levelData.child("name").getValue().toString());
                        levels.add(level);
                    }
                    listLevelsAdapter.setData(levels);
                    listLevelsAdapter.notifyDataSetChanged();
                }
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(listLevels, "Произошла ошибка...", Snackbar.LENGTH_LONG).setAction("Повтороить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadLevels();
                    }
                }).show();
                progressDialog.cancel();
            }
        });
    }
}
