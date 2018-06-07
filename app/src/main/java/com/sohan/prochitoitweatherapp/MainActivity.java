package com.sohan.prochitoitweatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, updatedField, todayTips, errorReport;
    JSONObject data = null;
    EditText cityEdit;
    Button searchBtn;
    ImageView weatherIcon;
    LinearLayout weatherReports;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        weatherReports = (LinearLayout) findViewById(R.id.report);
        weatherReports.setVisibility(View.INVISIBLE);

        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        cityEdit = (EditText) findViewById(R.id.type_city);
        searchBtn = (Button) findViewById(R.id.search);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        todayTips = (TextView) findViewById(R.id.tips);
        errorReport = (TextView) findViewById(R.id.error_report);
        errorReport.setVisibility(View.INVISIBLE);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityEdit.getText().toString();
                if (cityName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Type a City Name", Toast.LENGTH_SHORT).show();
                } else {
                    cityEdit.getText().clear();
                    getJSON(cityName);
                }
            }
        });

    }

    public void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=6a1478102a6110ee43a0b40606644889");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if (data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {

                    System.out.println("Exception " + e.getMessage());
                    return null;


                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if (data != null) {
                    Log.d("my weather received", data.toString());

                    try {
                        if (data != null) {
                            JSONObject details = data.getJSONArray("weather").getJSONObject(0);
                            JSONObject main = data.getJSONObject("main");
                            DateFormat df = DateFormat.getDateTimeInstance();


                            String city = data.getString("name").toUpperCase(Locale.US) + ", " + data.getJSONObject("sys").getString("country");
                            String description = details.getString("description").toUpperCase(Locale.US);
                            String icon = details.getString("icon");
                            String temperature = String.format("%.2f", main.getDouble("temp")) + "°";
                            String humidity = main.getString("humidity") + "%";
                            String pressure = main.getString("pressure") + " hPa";
                            String updatedOn = df.format(new Date(data.getLong("dt") * 1000));


                            cityField.setText(city);
                            updatedField.setText(updatedOn);
                            detailsField.setText(description);
                            currentTemperatureField.setText(temperature);
                            humidity_field.setText("Humidity: " + humidity);
                            pressure_field.setText("Pressure: " + pressure);

                            doSomeOtherStuff(icon);
                            weatherReports.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                    }
                }

            }
        }.execute();

    }

    public void doSomeOtherStuff(String icon) {
        Picasso.with(this).load("http://openweathermap.org/img/w/" + icon + ".png").into(weatherIcon);

        switch (icon) {
            case "01d":
                todayTips.setText("It's a sunny day. Wear coloful and light dresses.");
            case "02d":
                todayTips.setText("Keep Umbrella with you. ");
            case "03d":
                todayTips.setText("Keep Umbrella with you. ");
            case "04d":
                todayTips.setText("Keep Umbrella with you. ");
            case "09d":
                todayTips.setText("It's raining...");
            case "10d":
                todayTips.setText("Stay dry");
            case "11d":
                todayTips.setText("wear coat");
            case "13d":
                todayTips.setText("it's gonna be too cold outside. wear something accordingly");
            case "50d":
                todayTips.setText("do you have something for mist situation?");
            default:
                todayTips.setText("It's you life. Live the way you want to...");
        }

    }
}
