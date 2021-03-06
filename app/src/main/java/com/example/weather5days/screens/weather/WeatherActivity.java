package com.example.weather5days.screens.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather5days.BuildConfig;
import com.example.weather5days.Converters;
import com.example.weather5days.R;
import com.example.weather5days.adapters.WeatherAdapter;
import com.example.weather5days.pojo.Weather5days;
import com.example.weather5days.screens.about.AboutActivity;
import com.example.weather5days.screens.options.ChooseBackgroundActivity;
import com.example.weather5days.screens.options.OptionsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;



public class WeatherActivity extends AppCompatActivity implements WeatherView{
    private static double lat = 0.0;
    private static double lon = 0.0;
    private int position = 0;
    private final static String BASE_WEATHER_ICON_URL = "http://openweathermap.org/img/wn/%s@%sx.png";
    private int firstColor;
    private int secondColor;

    public static double getLat() {
        return lat;
    }

    public static double getLon() {
        return lon;
    }

    private ConstraintLayout constraintLayoutMain;
    private RecyclerView recyclerViewWeather;
    private WeatherAdapter weatherAdapter;
    private TextView textViewCityName;
    private TextView textViewLocalTimeDate;
    private TextView textViewCurrentTemperature;
    private TextView textViewCorF;
    private ImageView imageViewCurrentWeatherIcon;
    private TextView textViewCurrentWeatherDescription;
    private TextView textViewFeelsLike;
    private TextView textViewCorF2;
    private TextView textViewCurrentPrecipitation;
    private TextView textViewCurrentPressure;
    private TextView textViewCurrentHumidity;
    private TextView textViewWind;
    private TextView textViewWeatherForecastLabel;
    private ImageView imageViewLocation;
    private WeatherPresenter presenter;

    private static final String TAG = WeatherActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    FusedLocationProviderClient fusedLocationProviderClient;
    protected Location mLastLocation;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemWeather:
                Intent intentWeather = new Intent(this, WeatherActivity.class);
                startActivity(intentWeather);
                break;
            case R.id.itemOptions:
                Intent intentOptions = new Intent(this, OptionsActivity.class);
                startActivity(intentOptions);
                break;
            case R.id.itemAbout:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ActionBar actionBar = getSupportActionBar();
//                if(actionBar != null){
//            actionBar.hide();
//        }
        textViewCityName = findViewById(R.id.textViewCityName);
        imageViewLocation = findViewById(R.id.imageViewLocation);
        textViewLocalTimeDate = findViewById(R.id.textViewLocalTimeDate);
        textViewCurrentTemperature = findViewById(R.id.textViewCurrentTemperature);
        textViewCorF = findViewById(R.id.textViewCorF);
        imageViewCurrentWeatherIcon = findViewById(R.id.imageViewCurrentWeatherIcon);
        textViewCurrentWeatherDescription = findViewById(R.id.textViewCurrentWeatherDescription);
        textViewFeelsLike = findViewById(R.id.textViewFeelsLike);
        textViewCorF2 = findViewById(R.id.textViewCorF2);
        textViewCurrentPrecipitation = findViewById(R.id.textViewCurrentPrecipitation);
        textViewCurrentPressure = findViewById(R.id.textViewCurrentPressure);
        textViewCurrentHumidity = findViewById(R.id.textViewCurrentHumidity);
        textViewWind = findViewById(R.id.textViewWind);
        presenter = new WeatherPresenter(this);
        recyclerViewWeather = findViewById(R.id.recyclerViewWeather);
        if (ChooseBackgroundActivity.isBackgroundColorChanged()) {
            firstColor = ChooseBackgroundActivity.getFirstColor();
            secondColor = ChooseBackgroundActivity.getSecondColor();
        } else {
            firstColor = getResources().getColor(R.color.blue4);
            secondColor = getResources().getColor(R.color.blue5);
        }
        constraintLayoutMain = findViewById(R.id.constraintLayoutMain);
        constraintLayoutMain.setBackgroundColor(firstColor);
        textViewWeatherForecastLabel = findViewById(R.id.textViewWeatherForecastLabel);
        textViewWeatherForecastLabel.setBackgroundColor(secondColor);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        weatherAdapter = new WeatherAdapter(new Weather5days(), secondColor);
        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewWeather.setAdapter(weatherAdapter);
//        getLastLocation();
        presenter.getWeather();
        weatherAdapter.setOnWeatherClickListener(new WeatherAdapter.OnWeatherClickListener() {
            @Override
            public void onWeatherClick(int position) {

            }

            @Override
            public void onWeatherLongClick(int position) {
                showCurrentWeather(position);
            }
        });

    }

    public void onClickImageViewLocation(View view) {
        getLastLocation();
        presenter.getWeather();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            lat = mLastLocation.getLatitude();
                            lon = mLastLocation.getLongitude();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.itemWeather);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(WeatherActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    public void showCurrentWeather(int position) {
        textViewLocalTimeDate.setText(Converters.dateTime(weatherAdapter.getWeatherLists().get(position).getDtTxt(), "EEEE dd.MM HH:mm"));
        textViewCurrentTemperature.setText("" + Math.round(weatherAdapter.getWeatherLists().get(position).getMain().getTemp()));
        textViewCurrentWeatherDescription.setText(weatherAdapter.getWeatherLists().get(position).getWeather().get(0).getDescription());
        textViewFeelsLike.setText("" + Math.round(weatherAdapter.getWeatherLists().get(position).getMain().getFeelsLike()));
        try {
            textViewCurrentPrecipitation.setText((int) (weatherAdapter.getWeatherLists().get(position).getPop() * 100) + "% ("
                    + (Double) weatherAdapter.getWeatherLists().get(position).getSnow().get3h() + "mm)");
        } catch (NullPointerException eSnow) {
            try {
                textViewCurrentPrecipitation.setText((int) (weatherAdapter.getWeatherLists().get(position).getPop() * 100) + "% ("
                        + (Double) weatherAdapter.getWeatherLists().get(position).getRain().get3h() + "mm)");
            } catch (NullPointerException eRain) {
                textViewCurrentPrecipitation.setText((int) (weatherAdapter.getWeatherLists().get(position).getPop() * 100) + "% (0mm)");
            }
        }
        textViewCurrentPressure.setText("" + Math.round(weatherAdapter.getWeatherLists().get(position).getMain().getPressure() * 0.750064) + "mmHg");
        textViewCurrentHumidity.setText(weatherAdapter.getWeatherLists().get(position).getMain().getHumidity() + "%");
        textViewWind.setText(Math.round(weatherAdapter.getWeatherLists().get(position).getWind().getSpeed()) + " m/s");
        Picasso.get().load(String.format(BASE_WEATHER_ICON_URL, weatherAdapter.getWeatherLists().get(position).getWeather().get(0).getIcon(), 4))
                .into(imageViewCurrentWeatherIcon);
    }

    @Override
    protected void onDestroy() {
        presenter.disposeDisposable();
        super.onDestroy();
    }

    public static String getBASE_WEATHER_ICON_URL() {
        return BASE_WEATHER_ICON_URL;
    }

    @Override
    public void showData(Weather5days weather5days) {
        weatherAdapter.setWeather5days(weather5days);
        weatherAdapter.setWeatherLists(weather5days.getWeatherList());
        textViewCityName.setText(weatherAdapter.getWeather5days().getCity().getName());
        showCurrentWeather(position);
    }

    @Override
    public void showError() {
        Toast.makeText(this, "Не могу отобразить данные", Toast.LENGTH_SHORT).show();
    }
}