package com.example.admin.adminoperations;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText username,password;
    Button login,register;
    RadioGroup rg;
    String uname,pass,type,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        login=findViewById(R.id.btn1);
        register=(Button) findViewById(R.id.btn2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected=rg.getCheckedRadioButtonId();
                RadioButton r=(RadioButton)findViewById(selected);
                uname=username.getText().toString();
                pass=password.getText().toString();
                type= r.getText().toString();
                new MyTask().execute();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Registration.class);
                startActivity(i);
            }
        });

    }

    private class MyTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {


            URL url = null;

            try {

                url = new URL("http://192.168.1.6:8888/internship/Myapp/main/login&"+ "'" + uname + "'"+ "&'" +pass+"'&'"+type+"'");
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


                System.out.println("The response is " + response.toString());
                JSONObject ob = new JSONObject(response.toString());
                status=ob.getString("STATUS");


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result.equalsIgnoreCase("wrong"))
            {
                Toast.makeText(MainActivity.this,"user don't exist",Toast.LENGTH_LONG).show();

            }else{
                if(type.equals("admin")){
                    Intent launchActivity1= new Intent(MainActivity.this,AdminHome.class);
                    startActivity(launchActivity1);

                }else{
                    //redirect to
                    Intent launchActivity1= new Intent(MainActivity.this,Search.class);
                    startActivity(launchActivity1);
                }
            }
        }
    }
}

