package com.open_sports.login3;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends ActionBarActivity {

    InputStream is=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final EditText e_voornaam=(EditText) findViewById(R.id.editText1);
        final EditText e_achternaam=(EditText) findViewById(R.id.editText2);
        final EditText e_emailadres=(EditText) findViewById(R.id.editText3);
        final EditText e_wachtwoord1=(EditText) findViewById(R.id.editText4);
        final EditText e_wachtwoord2=(EditText) findViewById(R.id.editText5);

        Button Register=(Button) findViewById(R.id.button1);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> info = new ArrayList<String>();
                info.add(e_voornaam.getText().toString());
                info.add(e_achternaam.getText().toString());
                info.add(e_emailadres.getText().toString());
                if (e_wachtwoord1.getText().toString().equals(e_wachtwoord2.getText().toString())){

                    String pwdmd5 = md5(e_wachtwoord2.getText().toString());
                    info.add(pwdmd5);

                    //new SummaryAsyncTask().execute(voornaam, achternaam);
                    try {
                        // postData(voornaam, achternaam);
                        new postData().execute(info);
                    }
                    catch(Exception ex){
                        Toast.makeText(getApplicationContext(), "foutje", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "wachtwoorden zijn niet gelijk", Toast.LENGTH_SHORT).show();
                    e_wachtwoord1.setText("");
                    e_wachtwoord2.setText("");
                }
            }
        });
    }

    private class postData extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... info) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.open-sports.com/insert.php");
            ArrayList<String> result = new ArrayList<String>();
            ArrayList<String> passed = info[0];
            String emailadres = passed.get(2).toString();
            String voornaam = passed.get(0).toString();
            String achternaam = passed.get(1).toString();
            String wachtwoord = passed.get(3).toString();
            ArrayList<String> line = new ArrayList<String>();

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("voornaam", voornaam));
                nameValuePairs.add(new BasicNameValuePair("achternaam", achternaam));
                nameValuePairs.add(new BasicNameValuePair("email", emailadres));
                nameValuePairs.add(new BasicNameValuePair("wachtwoord", wachtwoord));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                is = httpEntity.getContent();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8);
                StringBuilder sb = new StringBuilder();
                line.add(reader.readLine());
                is.close();

            } catch(Exception ex){

            }

            return line;
        }
        // Create a new HttpClient and Post Header
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
