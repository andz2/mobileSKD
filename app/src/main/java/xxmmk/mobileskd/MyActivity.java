package xxmmk.mobileskd;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import com.cengalabs.flatui.FlatUI;
import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatCheckBox;
import com.cengalabs.flatui.views.FlatEditText;
import com.cengalabs.flatui.views.FlatRadioButton;
import com.cengalabs.flatui.views.FlatSeekBar;
import com.cengalabs.flatui.views.FlatTextView;
import com.cengalabs.flatui.views.FlatToggleButton;
import java.util.ArrayList;


public class MyActivity extends Activity {

    @Override
    protected void onStart(){

        super.onStart();


        TextView loginInfo = (TextView) findViewById(R.id.LognText);
        loginInfo.setText(statusConnect());



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
//---------------------------------------------------------



     //---------------------------------------------------
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //политика сетевого доступа
        StrictMode.setThreadPolicy(policy); //применяем политику

        Button loginButton = (Button) findViewById(R.id.Loginbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(intent);
            }
        }
        );

        Button scanButton = (Button) findViewById(R.id.ScanBtn);
        scanButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Intent intent = new Intent(view.getContext(),ScanActivity.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               view.getContext().startActivity(intent);
                                           }
                                       }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String statusConnect() {
        MobileSKDApp app = ((MobileSKDApp) this.getApplication());

        Log.d(app.getLOG_TAG(), "StartActivity.statusConnect");
        StringBuilder builder = new StringBuilder();
        Button btn=(Button) findViewById(R.id.ScanBtn);

        HttpClient client = app.getNewHttpClient(); //createHttpClient(); // new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(app.getDataURL("400"));
        Log.d(app.getLOG_TAG(), "StartActivity.statusConnect " + app.getDataURL("400"));
        String vErrorToken="Аутентификация OK";
        btn.setEnabled(true);
        String vErrorNetwork="Сеть OK";
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200 || statusCode == 500)
            {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                try {
                    //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                    JSONArray jsonArray = new JSONArray(builder.toString());
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        vErrorToken = jsonObject.getString("ERROR");
                        btn.setEnabled(false);

                    }
                    //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    vErrorToken="Аутентификация OK";
                    app.getmDbHelper().refreshOrgs(builder.toString());
                    btn.setEnabled(true);
                    //e.printStackTrace();
                }
            }
            else {
                vErrorNetwork = "Сеть ERROR";

                vErrorToken="Аутентификация ERROR";
                btn.setEnabled(false);
                //Log.e("Login fail", "Login fail");
            }
        }
        catch (ClientProtocolException e) {
            vErrorNetwork = "Сеть ERROR";
            vErrorToken="Аутентификация ERROR";
            btn.setEnabled(false);
            e.printStackTrace();
        }
        catch (IOException e) {
            vErrorNetwork = "Сеть ERROR";
            vErrorToken="Аутентификация ERROR";
            btn.setEnabled(false);
            e.printStackTrace();
        }

        return vErrorNetwork+"; "+vErrorToken;
    }
}
