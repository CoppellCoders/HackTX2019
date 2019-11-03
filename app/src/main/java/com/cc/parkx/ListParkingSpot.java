package com.cc.parkx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.squareup.picasso.Picasso;

import org.bson.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ListParkingSpot extends Fragment {

    EditText text, price, name, spots;
    Place good;
    ImageView add_img;
    Uri outputFileUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_parking_spot, container, false);
        final String[] keys = new String[]{"lat", "long", "price", "name", "img", "spots", "phone"};
        ImageButton add = view.findViewById(R.id.add_space);
        // Initialize the SDK
        Places.initialize(getContext(), "AIzaSyAL11387Go5npXaOZQQKFc-Jh6EWwcCE84");
        text = view.findViewById(R.id.location_select);
        add_img = view.findViewById(R.id.add_img);
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePhoto();
            }
        });


        price = view.findViewById(R.id.add_price);
        name = view.findViewById(R.id.add_name);
        spots = view.findViewById(R.id.add_spot);
// Create a new Places client instance
        final PlacesClient placesClient = Places.createClient(getContext());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDB(keys, new String[]{String.valueOf(good.getLatLng().latitude),
                        String.valueOf(good.getLatLng().longitude),
                        price.getText().toString(), name.getText().toString(), "https://i.gyazo.com/e85ad29cf45ff7a74b7f8593bbb4720b.png", spots.getText().toString(),
                        genPhoneNumber()});
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)).build(getActivity());
                startActivityForResult(intent, 69);
            }
        });
        return view;
    }

    private String genPhoneNumber() {
        Random rand = new Random();
        int num1 = (rand.nextInt(7) + 1) * 100 + (rand.nextInt(8) * 10) + rand.nextInt(8);
        int num2 = rand.nextInt(743);
        int num3 = rand.nextInt(10000);

        DecimalFormat df3 = new DecimalFormat("000"); // 3 zeros
        DecimalFormat df4 = new DecimalFormat("0000"); // 4 zeros

        String phoneNumber = df3.format(num1)+ "" +df3.format(num2) + "" + df4.format(num3);

        return phoneNumber;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 69) {
            if (resultCode == RESULT_OK) {
                good = Autocomplete.getPlaceFromIntent(data);
                text.setText(good.getAddress().substring(0, good.getAddress().indexOf(",")));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } if (requestCode == 0 && resultCode == RESULT_OK) {
                    Picasso.get()
                            .load(outputFileUri).fit().centerCrop()
                            .into(add_img);
                }
            }


    private void capturePhoto() {
        final String dir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/Folder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir+ System.currentTimeMillis()+"work.png";


        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {}

        outputFileUri =  FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider",newfile);
        Log.e("work", outputFileUri.toString());

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 0);
    }

    public void addToDB(String[] keys, String[] tokens) {
        MongoClientURI uri = new MongoClientURI(
                "mongodb://cc:cc@cluster0-shard-00-00-zme9k.azure.mongodb.net:27017,cluster0-shard-00-01-zme9k.azure.mongodb.net:27017,cluster0-shard-00-02-zme9k.azure.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&&w=majority");

        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("data");
        final Document doc = new Document();
        for(int i = 0; i < keys.length; i++) {
            doc.append(keys[i], tokens[i]);
        }

        final MongoCollection<Document> locations = database.getCollection("locations");
        new Thread(new Runnable() {
            @Override
            public void run() {
                locations.insertOne(doc);
            }
        }).start();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Added listing!", Toast.LENGTH_SHORT).show();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.content, new MapsActivity()).commit();
            }
        });
    }
}
