package com.example.it2019firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LevelActivity extends AppCompatActivity {

    public static final String LEVEL_KEY = "LEVEL_KEY";

    private ProgressDialog progressDialog;
    private DatabaseReference reference;

    private Level level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        progressDialog = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference();

        level = new Level();
        level.setKey(getIntent().getStringExtra(LEVEL_KEY));
        loadLevel(level.getKey());
    }

    private void loadLevel(String key) {
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.show();
        reference.child("levels").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Image image;
                    ArrayList<Image> images = new ArrayList<>();
                    level.setName(dataSnapshot.child("name").getValue().toString());
                    for (DataSnapshot imageData : dataSnapshot.child("images").getChildren()) {
                        image = new Image();
                        image.setName(imageData.child("name").getValue().toString());
                        image.setSrc(imageData.child("src").getValue().toString());
                        image.setValue(Integer.parseInt(imageData.child("src").getValue().toString()));
                        images.add(image);
                    }
                    level.setImages(images);
                }
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.cancel();
            }
        });
    }
}