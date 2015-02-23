package com.open_sports.login3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.*;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import io.fabric.sdk.android.Fabric;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "nBCTQp6dUzvyTycHFxLJ67AD9";
    private static final String TWITTER_SECRET = "AVWJHM2xtA08mQ88p7mxkcNb93mvmrZKMWRRH6DgfT8mtaDZZp";
    //private static final String TWITTER_KEY = "Lxo6bFHwCWsL4ao67AbgJXBln";
    //private static final String TWITTER_SECRET = "PuF2SOIwBKKJDcoM1EpxJ9u3eC09cB7WxkwdFPHH5T8FrcMQ9b";
    private TwitterLoginButton loginButton;

    InputStream is=null;
    String result=null;
    String line=null;
    int code;
    TextView TV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        ArrayList<String> line = new ArrayList<String>();
        final EditText e_emailadres=(EditText) findViewById(R.id.editText1);
        final EditText e_wachtwoord=(EditText) findViewById(R.id.editText2);
        final Context context = this;

        Button Login=(Button) findViewById(R.id.button1);
        Button Register = (Button) findViewById(R.id.button2);
        Button FbInlog = (Button) findViewById(R.id.button3);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                //Toast.makeText(getApplicationContext(), result.data.getUserName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
        //Button TwInlog = (Button) findViewById(R.id.button4);

        /*TwInlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInTwitter();
            }
        });*/

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> info = new ArrayList<String>();
                info.add(e_emailadres.getText().toString());
                info.add(md5(e_wachtwoord.getText().toString()));

                try {
                    // postData(voornaam, achternaam);
                    new postData().execute(info);
                }
                catch(Exception ex){
                    Toast.makeText(MainActivity.this, "foutje", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FbInlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookSession();
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(context, MainActivity2.class);
                startActivity(register);
            }
        });
    }

    /*private void LogInTwitter(){
        new Callback<TwitterSession>(){
            @Override
            public void success(Result<TwitterSession> result){
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String user = session.getUserName();
                //TextView naam = (TextView) findViewById(R.id.fbnaam);
                //naam.setText("Hello " + user + "!");
            }

            @Override
            public void failure(TwitterException exception){

            }
        };
    }*/

    private void openFacebookSession(){
        Session session = openActiveSession(MainActivity.this, true, Arrays.asList("email", "user_birthday", "user_hometown", "user_location"), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                Intent main = new Intent(MainActivity.this, MainActivity3.class);
                                startActivity(main);
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    private static Session openActiveSession(Activity activity, boolean allowLoginUI, List permissions, Session.StatusCallback callback) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(activity).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;
    }

    private class postData extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... info) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.open-sports.com/login2.php");
            ArrayList<String> passed = info[0];
            String emailadres = passed.get(0).toString();
            String wachtwoord = passed.get(1).toString();
            ArrayList<String> line = new ArrayList<String>();

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
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
                result = line.get(0);

            } catch(Exception ex){

            }

            return line;
        }
        // Create a new HttpClient and Post Header
        @Override
        protected void onPostExecute(ArrayList<String> result2)
        {
            super.onPostExecute(result2);
            Intent intent = new Intent(MainActivity.this, MainActivity3.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
