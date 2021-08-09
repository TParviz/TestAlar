package ru.main.testalar;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapCallback extends AppCompatActivity implements OnMapReadyCallback {

    private double latitude = 0, longitude = 0;
    private String id, name, country;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        id = bundle.getString(AdapterClass.STRING_ID);
        name = bundle.getString(AdapterClass.STRING_NAME);
        country = bundle.getString(AdapterClass.STRING_COUNTRY);
        latitude = bundle.getDouble(AdapterClass.STRING_LAT);
        longitude = bundle.getDouble(AdapterClass.STRING_LON);

        TextView tvID = findViewById(R.id.tv_id);
        tvID.setText("ID : " + id);

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText("Name : " + name);

        TextView tvCountry = findViewById(R.id.tv_country);
        tvCountry.setText("Country : " + country);

        ImageButton imgBtnBack = findViewById(R.id.img_btn_back);
        imgBtnBack.setOnClickListener(v -> onBackPressed());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 11f));
    }
}
