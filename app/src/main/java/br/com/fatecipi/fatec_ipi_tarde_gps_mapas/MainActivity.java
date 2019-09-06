package br.com.fatecipi.fatec_ipi_tarde_gps_mapas;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;
    private TextView locationTextView;
    private TextView deactivateGPSTextView;
    private TextView activateGPSTextView;
    boolean gpsStatus;
    //AndroidManifest deve conter linha para permissao
    private static final int REQUEST_PERMISSION_GPS = 1001;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTextView = findViewById(R.id.locationTextView);
        deactivateGPSTextView = findViewById(R.id.deactivateGPSTextView);
        activateGPSTextView = findViewById(R.id.activateGPSTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //floatingButton
        FloatingActionButton fab = findViewById(R.id.fab);

        activateGPSTextView.setOnClickListener(new View.OnClickListener() {
            //ao clicar no texto, ativar GPS
            @Override
            public void onClick(View view) {
                CheckGpsStatus();
                if(gpsStatus == false) onStart();
                else locationTextView.setText("Já ligado");
                onStop();
            }
        });

        deactivateGPSTextView.setOnClickListener(new View.OnClickListener() {
            //ao clicar no texto, desativar GPS
            @Override
            public void onClick(View view) {
                CheckGpsStatus();
                if (gpsStatus) onStop();
                else {
//                    Toast.makeText(this, "Já desativado", Toast.LENGTH_SHORT).show();
                    locationTextView.setText("Já desativado");
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            //ao clicar no botao, redireconar para google maps e pesquisar 'restaurante'
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(String.format("geo: %f, %f?q=restaurantes", latitude,longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //deve conter, no minimo, o google play service para ter acesso
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener =  new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String s = String.format("Lat: %f, Long: %f", latitude, longitude);
                locationTextView.setText(s);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,2000,2,locationListener
            );
        }else{
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_GPS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,2000,2,locationListener
                    );
                }
            }
            else{
                Toast.makeText(this,
                        getString(R.string.no_gps_no_app),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void CheckGpsStatus(){
        try{
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception e){
            locationTextView.setText("Deu ruim");
        }

    }

}
