package com.example.admin.adminoperations;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Registration extends AppCompatActivity {

    EditText username,password,lastname,firstname,email;
    Button signup;
    RadioGroup rb;

    String uname,pass,typee,fname,lname,emaill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        lastname=findViewById(R.id.lastname);
        firstname=findViewById(R.id.firstname);
        rb = (RadioGroup) findViewById(R.id.radioGroup);

        email=findViewById(R.id.email);

        signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected=rb.getCheckedRadioButtonId();
                RadioButton r=(RadioButton)findViewById(selected);
                uname=username.getText().toString();
                pass=password.getText().toString();
                lname=lastname.getText().toString();
                fname=firstname.getText().toString();
                emaill=email.getText().toString();
                typee=r.getText().toString();
                new MyTask().execute();
            }
        });


    }

    private class MyTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {


            URL url = null;

            try {

                url = new URL("http://10.0.2.2:27897/WebApplication1/Myapp/main/registration&"+typee+"&"+uname+"&"+pass+"&"+lname+"&"+fname+"&"+emaill+"");
                HttpURLConnection client = null;
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                int responseCode = client.getResponseCode();
                System.out.println("\n Sending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);
                InputStreamReader myInput = new InputStreamReader(client.getInputStream());
                BufferedReader in = new BufferedReader(myInput);
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();



            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

        }
    }
}
