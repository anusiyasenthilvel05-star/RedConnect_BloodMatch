package com.example.redconnectlogo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BloodRequestFormActivity extends AppCompatActivity {

    EditText etName, etAge, etDate, etPhone,
            etCountry, etCity, etUnit, etHospital,
            etState, etDistrict, etBloodGroup;

    RadioGroup radioGender;
    Button btnSubmit;
    ImageView ivLocation;

    FirebaseFirestore db;
    FusedLocationProviderClient fusedLocationClient;

    double latitude = 0.0;
    double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request_form);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etDate = findViewById(R.id.etDate);
        etPhone = findViewById(R.id.etPhone);
        etCountry = findViewById(R.id.etCountry);
        etCity = findViewById(R.id.etCity);
        etUnit = findViewById(R.id.etUnit);
        etHospital = findViewById(R.id.etHospital);
        etState = findViewById(R.id.etState);
        etDistrict = findViewById(R.id.etDistrict);
        etBloodGroup = findViewById(R.id.etBloodGroup);

        radioGender = findViewById(R.id.radioGender);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivLocation = findViewById(R.id.imgLocation);

        etPhone.setText("+91 ");
        etCountry.setText("India");

        etDate.setOnClickListener(v -> openDatePicker());
        ivLocation.setOnClickListener(v -> showLocationOptions());
        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    month = month + 1;
                    String date = dayOfMonth + "/" + month + "/" + year;
                    etDate.setText(date);

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void submitRequest() {

        Toast.makeText(this,"Submit Button Clicked",Toast.LENGTH_SHORT).show();

        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String units = etUnit.getText().toString().trim();
        String hospital = etHospital.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String bloodGroup = etBloodGroup.getText().toString().trim();

        int selectedId = radioGender.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this,"Select gender",Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedGender = findViewById(selectedId);
        String gender = selectedGender.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Enter patient name",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!age.matches("\\d+")){
            Toast.makeText(this,"Age format wrong",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(date)){
            Toast.makeText(this,"Select date",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!phone.matches("\\+91\\s?[6-9]\\d{9}")){
            Toast.makeText(this,"Phone format wrong",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(city)){
            Toast.makeText(this,"Enter city",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(hospital)){
            Toast.makeText(this,"Enter hospital name",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!bloodGroup.matches("^(A|B|AB|O)[+-]$")){
            Toast.makeText(this,"Blood group format wrong",Toast.LENGTH_SHORT).show();
            return;
        }

        if(latitude == 0.0 || longitude == 0.0){
            Toast.makeText(this,"Select location",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,Object> request = new HashMap<>();

        request.put("name",name);
        request.put("age",age);
        request.put("gender",gender);
        request.put("bloodGroup",bloodGroup);
        request.put("units",units);
        request.put("phone",phone);
        request.put("country",country);
        request.put("state",state);
        request.put("district",district);
        request.put("city",city);
        request.put("hospitalName",hospital);
        request.put("date",date);
        request.put("latitude",latitude);
        request.put("longitude",longitude);
        request.put("timestamp", FieldValue.serverTimestamp());

        db.collection("blood_requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(this,
                            "Request Submitted Successfully",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(
                            BloodRequestFormActivity.this,
                            MatchingListActivity.class
                    );

                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    intent.putExtra("units", units);
                    intent.putExtra("city", city);
                    intent.putExtra("district", district);
                    intent.putExtra("bloodGroup", bloodGroup);

                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Submission Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void showLocationOptions(){

        String[] options = {"My Current Location","Select From Map"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Location");

        builder.setItems(options,(dialog,which)->{

            if(which==0){
                getCurrentLocation();
            }else{
                Intent intent = new Intent(this,SelectLocationActivity.class);
                startActivityForResult(intent,200);
            }
        });

        builder.show();
    }

    private void getCurrentLocation(){

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location->{

                    if(location!=null){

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Toast.makeText(this,
                                "Current Location Selected",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Unable to get location",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==200 && resultCode==RESULT_OK && data!=null){

            latitude = data.getDoubleExtra("latitude",0);
            longitude = data.getDoubleExtra("longitude",0);

            Toast.makeText(this,
                    "Location Selected",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){

        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if(requestCode==100 && grantResults.length>0
                && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            getCurrentLocation();
        }
    }
}