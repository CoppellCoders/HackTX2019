package com.cc.parkx;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class ParkingSelectActivity extends AppCompatActivity {

    TextView subtotal, fees, tax, total;
    double pricePerHour;
    ImageButton checkout;
    ImageButton contact;
    ImageView done;
    NumberPicker np;
    String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_select);

        pricePerHour = getIntent().getDoubleExtra("price", .1);
        phoneNumber = getIntent().getStringExtra("phone") == null ? "6189175697" : getIntent().getStringExtra("phone");

        subtotal = findViewById(R.id.check_subtotal);
        fees = findViewById(R.id.check_fees);
        tax = findViewById(R.id.check_tax);
        total = findViewById(R.id.check_total);
        checkout = findViewById(R.id.check_checkout);
        contact = findViewById(R.id.contact);
        done = findViewById(R.id.parking_res);

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

        checkout.setVisibility(View.GONE);
        np.setVisibility(View.GONE);

    }

    public void setSubtotal(int hours) {
        subtotal.setText(String.format("$%.2f", hours * pricePerHour));
        fees.setText("$0.15");
        tax.setText(String.format("$%.2f", hours * pricePerHour * .02));
        total.setText(String.format("$%.2f", hours * pricePerHour * .02 + .15 + hours * pricePerHour));
    }
}
