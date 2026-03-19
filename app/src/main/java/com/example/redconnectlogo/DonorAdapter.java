package com.example.redconnectlogo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    Context context;
    List<Donor> donorList;
    double recipientLat, recipientLng;

    public DonorAdapter(Context context, List<Donor> donorList,
                        double recipientLat, double recipientLng) {

        this.context = context;
        this.donorList = donorList;
        this.recipientLat = recipientLat;
        this.recipientLng = recipientLng;
        sortDonors();
    }

    private void sortDonors() {
        Collections.sort(donorList, (d1, d2) -> {
            if (d1.willing && !d2.willing) return -1;
            if (!d1.willing && d2.willing) return 1;
            return 0;
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_matching_donor_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donor donor = donorList.get(position);

        holder.name.setText(donor.name);
        holder.blood.setText("Blood Group: " + donor.bloodGroup);
        holder.phone.setText("Phone: " + donor.phone);
        holder.city.setText("City: " + donor.city);
        holder.status.setText("Status: " + donor.status);

        if (donor.willing) {
            holder.donorCardView.setAlpha(1.0f);
        } else {
            holder.donorCardView.setAlpha(0.6f);
        }

        float[] result = new float[1];
        Location.distanceBetween(recipientLat, recipientLng,
                donor.latitude, donor.longitude, result);

        float distanceKm = result[0] / 1000;
        holder.distance.setText(String.format("%.2f km", distanceKm));

        holder.viewCard.setOnClickListener(v -> {
            if (holder.expandLayout.getVisibility() == View.GONE)
                holder.expandLayout.setVisibility(View.VISIBLE);
            else
                holder.expandLayout.setVisibility(View.GONE);
        });

        holder.call.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + donor.phone));
            context.startActivity(i);
        });

        holder.message.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + donor.phone));
            context.startActivity(i);
        });

        holder.whatsapp.setOnClickListener(v -> {
            String phone = donor.phone.replace("+", "");
            String url = "https://wa.me/" + phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        });

        holder.shareDetails.setOnClickListener(v -> {
            String text = "Donor Details:\n" + donor.name + "\n" + donor.bloodGroup + "\n" + donor.phone + "\n" + donor.city;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(share, "Share"));
        });

        holder.location.setOnClickListener(v -> {
            String uri = "https://www.google.com/maps/dir/?api=1"
                    + "&origin=" + recipientLat + "," + recipientLng
                    + "&destination=" + donor.latitude + "," + donor.longitude
                    + "&travelmode=driving";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        });

        holder.willing.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("Donors")
                    .document(donor.id)
                    .update("willing", true)
                    .addOnSuccessListener(unused -> {
                        donor.willing = true;
                        sortDonors();
                        notifyDataSetChanged();

                        // SEND MESSAGE TO DONOR
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("sms:" + donor.phone));
                        smsIntent.putExtra("sms_body", "Thanks for donating the blood!");
                        context.startActivity(smsIntent);
                    });
        });

        holder.notWilling.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("Donors")
                    .document(donor.id)
                    .update("willing", false)
                    .addOnSuccessListener(unused -> {
                        donor.willing = false;
                        sortDonors();
                        notifyDataSetChanged();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, blood, phone, city, status, distance;
        Button viewCard, willing, notWilling;
        ImageView call, message, whatsapp, shareDetails, location;
        LinearLayout expandLayout;
        CardView donorCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            blood = itemView.findViewById(R.id.txtBloodGroup);
            phone = itemView.findViewById(R.id.txtPhone);
            city = itemView.findViewById(R.id.txtCity);
            status = itemView.findViewById(R.id.txtStatus);
            distance = itemView.findViewById(R.id.txtDistance);
            viewCard = itemView.findViewById(R.id.btnViewCard);
            willing = itemView.findViewById(R.id.btnWilling);
            notWilling = itemView.findViewById(R.id.btnNotWilling);
            call = itemView.findViewById(R.id.callIcon);
            message = itemView.findViewById(R.id.messageIcon);
            whatsapp = itemView.findViewById(R.id.whatsappIcon);
            shareDetails = itemView.findViewById(R.id.shareDetailsIcon);
            location = itemView.findViewById(R.id.locationIcon);
            expandLayout = itemView.findViewById(R.id.expandLayout);
            donorCardView = itemView.findViewById(R.id.donorCardView);
        }
    }
}