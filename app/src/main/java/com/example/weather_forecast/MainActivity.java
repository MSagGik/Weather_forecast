package com.example.weather_forecast;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    // Указание полей
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Установление значений указанным полям
        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);

        // Создание обработчика событий кнопки
        main_btn.setOnClickListener(new View.OnClickListener() { // пропись выделения памяти
            @Override
            public void onClick(View v) {
                if (user_field.getText().toString().trim().equals(""))// условие
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();// Создание всплывающего окна при пустом вводе текста пользователя
                else {
                    String city = user_field.getText().toString();
                    String key = "d565224404e0ee6cde4dd715f512454f";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });

    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) { // Ссылка на интернет адрес и получение данной информации
            HttpsURLConnection connection = null; // Открытие соединения
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]); // Создание объекта для обращения к указанному интернет адресу
                connection = (HttpsURLConnection) url.openConnection(); // Открытие соединения
                connection.connect();

                InputStream stream = connection.getInputStream(); // Считывание данных из потока (интернет адреса)
                reader = new BufferedReader(new InputStreamReader(stream)); // Запись данных и выделение памяти

                StringBuffer buffer = new StringBuffer(); // Запись данных и выделение памяти
                String line = ""; // По умолчанию пустая строка

                while ((line = reader.readLine()) != null) // Постройное считывание текста
                    buffer.append(line).append("\n");
                return buffer.toString(); // Возврат результата

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect(); // В конце закрытие интернет соединения
                try {  // В конце закрытие интернет чтения
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) { // Принятие возвращения результата
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                result_info.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + " градусов");// Вывод результата внутри текстовой надписи в приложении
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}