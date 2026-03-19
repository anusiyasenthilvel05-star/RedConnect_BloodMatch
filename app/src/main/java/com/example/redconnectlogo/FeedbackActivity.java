package com.example.redconnectlogo;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redconnectlogo.FeedbackAdapter;
import com.example.redconnectlogo.FeedbackModel;
import com.example.redconnectlogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etFeedback;
    private Button btnSubmit;
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<FeedbackModel> feedbackList;
    private FeedbackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ratingBar = findViewById(R.id.ratingBar);
        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmitFeedback);
        recyclerView = findViewById(R.id.recyclerFeedback);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadFeedback();

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {

        float rating = ratingBar.getRating();
        String feedbackText = etFeedback.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();

        if (rating == 0 || feedbackText.isEmpty()) {
            Toast.makeText(this, "Please provide rating and feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("userId", userId);
        feedback.put("rating", rating);
        feedback.put("feedbackText", feedbackText);
        feedback.put("timestamp", FieldValue.serverTimestamp());

        db.collection("feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Feedback Submitted Successfully", Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(0);
                    etFeedback.setText("");
                    loadFeedback();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadFeedback() {

        String userId = auth.getCurrentUser().getUid();

        db.collection("feedback")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) return;

                    feedbackList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        FeedbackModel model = doc.toObject(FeedbackModel.class);
                        feedbackList.add(model);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}