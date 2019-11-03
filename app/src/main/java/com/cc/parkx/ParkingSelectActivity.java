package com.cc.parkx;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ParkingSelectActivity extends AppCompatActivity {

    TextView name, dist, price, subtotal, fees, tax, total;
    double pricePerHour;
    double lat, lng;
    ImageButton checkout;
    ImageButton contact;
    ImageButton takeMe;
    ImageView done, topImg;
    NumberPicker np;
    String phoneNumber, title, dista, priceP, url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_select);

        lat = getIntent().getDoubleExtra("lat", 1);
        lng = getIntent().getDoubleExtra("lng", 1);

        pricePerHour = getIntent().getDoubleExtra("price", .1);
        phoneNumber = getIntent().getStringExtra("phone") == null ? "6189175697" : getIntent().getStringExtra("phone");
        title = getIntent().getStringExtra("name") == null ? "Jake's Tire Shop" : getIntent().getStringExtra("name");
        dista = getIntent().getStringExtra("dist") == null ? ".22 miles" : (getIntent().getStringExtra("dist") + " miles");
        priceP = String.format("$%.2f", pricePerHour) + "/hr";
        url = getIntent().getStringExtra("img") == null ? "https://houseofhouston.com/wp-content/blogs.dir/279/files/2014/10/IMG_41361.jpg" : (getIntent().getStringExtra("img"));

        subtotal = findViewById(R.id.check_subtotal);
        fees = findViewById(R.id.check_fees);
        tax = findViewById(R.id.check_tax);
        total = findViewById(R.id.check_total);
        checkout = findViewById(R.id.check_checkout);
        contact = findViewById(R.id.contact);
        done = findViewById(R.id.parking_res);
        name = findViewById(R.id.title_check);
        price = findViewById(R.id.price);
        dist = findViewById(R.id.distance);
        topImg = findViewById(R.id.top_img);
        name.setText(title);
        dist.setText(dista);
        price.setText(priceP);
        Picasso.get()
                .load(Uri.parse(url)).fit().centerCrop()
                .into(topImg);

        np = findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(72);
        setSubtotal(np.getValue());
        np.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                if(i == 0) {
                    setSubtotal(numberPicker.getValue());
                }
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus();
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        });

    }

    private void changeStatus() {
        done.setVisibility(View.VISIBLE);
        contact.setVisibility(View.VISIBLE);

        checkout.setImageResource(R.drawable.takemetherebtn);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.toString(lat) + "," + Double.toString(lng));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        np.setVisibility(View.GONE);

    }

    public void setSubtotal(int hours) {
        subtotal.setText(String.format("$%.2f", hours * pricePerHour));
        fees.setText(String.format("$%.2f", hours * pricePerHour * .01));
        tax.setText(String.format("$%.2f", hours * pricePerHour * .02));
        total.setText(String.format("$%.2f", hours * pricePerHour * .02 + hours * pricePerHour * .01 + hours * pricePerHour));
    }
}
