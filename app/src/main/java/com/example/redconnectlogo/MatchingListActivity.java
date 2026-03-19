package com.example.redconnectlogo;

import android.Manifest;import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MatchingListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button sendRequestBtn;
    ImageView editIcon;

    FirebaseFirestore db;
    DatabaseReference liveLocationRef;

    List<Donor> donorList = new ArrayList<>();
    DonorAdapter adapter;

    String recipientBlood, recipientDistrict, recipientState, requestId, name , phone,units, city;
    double recipientLat, recipientLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_list);

        // UI Initialization
        recyclerView = findViewById(R.id.matchingRecyclerView);
        sendRequestBtn = findViewById(R.id.sendRequestBtn);
        editIcon = findViewById(R.id.editIcon);

        db = FirebaseFirestore.getInstance();
        liveLocationRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("liveLocation");

        // Intent Data
        recipientBlood = getIntent().getStringExtra("bloodGroup");
        recipientDistrict = getIntent().getStringExtra("district");
        recipientState = getIntent().getStringExtra("state");
        recipientLat = getIntent().getDoubleExtra("latitude", 0.0);
        recipientLng = getIntent().getDoubleExtra("longitude", 0.0);
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        units = getIntent().getStringExtra("units");
        city = getIntent().getStringExtra("city");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonorAdapter(this, donorList, recipientLat, recipientLng);
        recyclerView.setAdapter(adapter);

        fetchMatchingDonors();

        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MatchingListActivity.this, BloodRequestFormActivity.class);
            intent.putExtra("bloodGroup", recipientBlood);
            intent.putExtra("latitude", recipientLat);
            intent.putExtra("longitude", recipientLng);
            startActivity(intent);
            finish();
        });

        sendRequestBtn.setOnClickListener(v -> {
            // Disable button immediately to prevent multiple sends
            sendRequestBtn.setEnabled(false);
            sendRequestBtn.setText("Sending...");

            storeRequestInFirebase();
            sendRequestToAll();
        });
    }

    private void fetchMatchingDonors() {
        if (recipientBlood == null) return;

        db.collection("Donors")
                .whereEqualTo("bloodGroup", recipientBlood)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    donorList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Donor donor = doc.toObject(Donor.class);
                        if (donor != null) {
                            donor.id = doc.getId();
                            donorList.add(donor);
                        }
                    }
                    sortDonors();
                    adapter.notifyDataSetChanged();
                });
    }

    private void sortDonors() {
        Collections.sort(donorList, (d1, d2) -> {
            float[] res1 = new float[1];
            float[] res2 = new float[1];
            Location.distanceBetween(recipientLat, recipientLng, d1.latitude, d1.longitude, res1);
            Location.distanceBetween(recipientLat, recipientLng, d2.latitude, d2.longitude, res2);
            return Float.compare(res1[0], res2[0]);
        });
    }

    private void storeRequestInFirebase() {
        HashMap<String, Object> request = new HashMap<>();
        request.put("name", getIntent().getStringExtra("name"));
        request.put("bloodGroup", recipientBlood);
        request.put("phone", getIntent().getStringExtra("phone"));
        request.put("status", "pending");
        request.put("timestamp", System.currentTimeMillis());
        request.put("city",city);
        request.put("district",recipientDistrict);
        request.put("units",units);

        db.collection("requests").add(request).addOnSuccessListener(doc -> {
            requestId = doc.getId();
            listenForDonorResponse();
        });
    }

    private void listenForDonorResponse() {
        if (requestId == null) return;
        db.collection("requests").document(requestId).addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                if ("accepted".equals(status)) {
                    Toast.makeText(this, "A donor accepted your request!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendRequestToAll() {
        if (donorList.isEmpty()) {
            Toast.makeText(this, "No donors found", Toast.LENGTH_SHORT).show();
            sendRequestBtn.setEnabled(true);
            sendRequestBtn.setText("Send Request");
            return;
        }

        // 1. Permission Check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }

        // 2. Modern SmsManager Instance
        SmsManager smsManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            smsManager = this.getSystemService(SmsManager.class);
        } else {
            smsManager = SmsManager.getDefault();
        }

        String patientName = getIntent().getStringExtra("name");
        String units = getIntent().getStringExtra("units");
        String location=city+","+recipientDistrict;

        String locationLink = "https://maps.google.com/?q=" + recipientLat + "," + recipientLng;

        int count = 0;

        for (Donor donor : donorList) {

            String phoneNumber = donor.phone;

            if (phoneNumber == null) continue;

            phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

            if (phoneNumber.length() == 10) {
                phoneNumber = "+91" + phoneNumber;
            }

            String message = "RED CONNECT: Urgent " + recipientBlood + " required.\n" +
                    "Patient Name: " + name + "\n" +
                    "Blood Unit: " + units + "\n" +
                    "Location: " + locationLink + "\n" +
                    "City: " + city + "," + recipientDistrict + "\n\n" +
                    "Are you willing to donate?\n" +
                    "Reply YES if willing\n" +
                    "Reply NO if not.";

            try {

                ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

                count++;

            } catch (Exception e) {
                Log.e("SMS_ERROR", "Failed to send to " + phoneNumber, e);
            }
        }

        Toast.makeText(this, "Request sent to " + count + " donors.", Toast.LENGTH_LONG).show();
        sendRequestBtn.setText("Success");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendRequestToAll();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            sendRequestBtn.setEnabled(true);
            sendRequestBtn.setText("Send Request");
        }
    }
}