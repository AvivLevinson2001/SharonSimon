package com.example.sharonsimon.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sharonsimon.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity
{
    EditText usernameEt;
    Spinner kenSpinner;
    Button loginBtn;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        // logged out. reset shared preferences and finish MainActivity
        if(MainActivity.MainActivityInstance != null){
            sp.edit().putBoolean("isLoggedIn", false)
                    .putString("name", "")
                    .putString("ken","")
                    .apply();
            MainActivity.MainActivityInstance.finish();
        }

        if (sp.getBoolean("isLoggedIn", false))
        {
            login();
        }

        usernameEt = findViewById(R.id.login_name_et);
        kenSpinner = findViewById(R.id.login_ken_spinner);
        loginBtn = findViewById(R.id.login_btn);

        final String[] items = new String[]
                {"בחר קן", "מקורות", "המעפיל", "מעיין", "העוגן", "רמות חפר", "יקום", "געש", "גן שמואל",
                        "להבות חביבה", "מבואות עירון", "כרכור", "כפס מרום", "הרצליה", "חריש"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ken_spinner_row, R.id.ken_tv,items);
        kenSpinner.setAdapter(adapter);
        kenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                }
                else{
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (usernameEt != null && !usernameEt.getText().toString().equals("")
                 && !kenSpinner.getSelectedItem().toString().equals("בחר קן"))
                {
                    sp.edit().putBoolean("isLoggedIn", true)
                            .putString("name", usernameEt.getText().toString())
                            .putString("ken", kenSpinner.getSelectedItem().toString())
                            .apply();
                    login();
                }
                else{

                    Snackbar.make(findViewById(R.id.register_root_layout),"הזן את כל הפרטים", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void login()
    {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
