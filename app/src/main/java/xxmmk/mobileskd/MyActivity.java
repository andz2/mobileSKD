package xxmmk.mobileskd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;




public class MyActivity extends Activity {
    private MobileSKDApp mMobileSKDApp;
    private AlertDialog.Builder ad;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    private UserLoginTask mAuthTask = null;



    Context context;
    ProgressDialog ringProgressDialog;
    private LoadObjects mLoadTask = null;
    MyTask mt;



        //private SaveObjects mSaveTask = null;
        @Override
        protected void  onResume()
        {
            super.onResume();
           // Log.d("Resume","Resume");
            enableForegroundMode();
            ActionBar myAB = getActionBar();
            myAB.setTitle(mMobileSKDApp.SKDOperator);
            myAB.setSubtitle(mMobileSKDApp.SKDKPP);
            myAB.setDisplayShowHomeEnabled(false);
            StartScreen ();

        }
    @Override
    protected void onStart(){
     //   Log.d("start","Start");
        super.onStart();
        Log.d(mMobileSKDApp.SKDOperator, "=mMobileSKDApp.SKDOperator");
        Log.d("1","zzzzzz");
        Log.d(mMobileSKDApp.SKDStep ,"mMobileSKDApp.SKDStep");
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        StartScreen ();



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());

        context = MyActivity.this;
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);

        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextSize(18);
                title.setTextColor(Color.BLACK);
            }
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(R.layout.activity_my);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //политика сетевого доступа
        StrictMode.setThreadPolicy(policy); //применяем политику

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        StartScreen ();
  }
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true); *************************************************************************подменить на показ окна
            mAuthTask = new UserLoginTask(mMobileSKDApp.SKDRfId);
            mAuthTask.execute((Void) null);
        }
    }

    @Override //убираем меню
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }
    public void enableForegroundMode() {
        //Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        //Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }





    @Override
    protected void onPause() {
        //Log.d(TAG, "onPause");

        super.onPause();

        disableForegroundMode();
    }

    private void vibrate() {
        //Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }
 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //убрали меню
       if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
 public void loadSKDPeople(){
     ringProgressDialog = ProgressDialog.show(MyActivity.this, "Подождите ...", "Загружаются владельцы карт с точечным уровнем доступа ...", true);
     ringProgressDialog.setCancelable(false);

     //showProgress(true);
     mLoadTask = new LoadObjects();
     mLoadTask.execute((Void) null);
     Log.d("7","7 ");
     Toast.makeText(this, "Finish.", Toast.LENGTH_SHORT).show();



   //  ringProgressDialog.dismiss(); //потом убрать
 }
    public class LoadObjects extends AsyncTask<Void, Void, Boolean> {

        //private final String mOrgId;
        private String mToken = "null";

        LoadObjects() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean vStatus = false;
            try {
                /*StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                //MobileTOiRApp app = MobileTOiRApp.getInstance();
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects ");
                HttpGet httpGet = new HttpGet(mMobileSKDApp.getSKDDataURL("602"));
                Log.d("1","http");
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.d("2","statusCode!!!!="+statusCode);
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {

                            builder.append(line);
                       //     Log.d("1",line);
                        }
                        Log.d("4","dead?= "+line);
//                        mMobileSKDApp.getmDbHelper().loadObjects(builder.toString(),mOrgId);

                    }
                    else {
                        //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(10);*/
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            Log.d("5","return true ");

            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mLoadTask = null;
            ringProgressDialog.dismiss();
            //showProgress(false);

            if (success) {
                //Toast.makeText(getParent(), "Finish.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getParent(), "Error.", Toast.LENGTH_SHORT).show();
            }
            Log.d("6","6 ");
        }

        @Override
        protected void onCancelled() {
            mLoadTask = null;
           // showProgress(false); --я
        }


    }

/*
    public String statusConnect() {
        MobileSKDApp app = ((MobileSKDApp) this.getApplication());

        Log.d(app.getLOG_TAG(), "StartActivity.statusConnect");
        StringBuilder builder = new StringBuilder();
        Button btn=(Button) findViewById(R.id.ScanBtn);
        Button btnOff=(Button) findViewById(R.id.OffBt);

        HttpClient client = app.getNewHttpClient(); //createHttpClient(); // new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(app.getDataURL("400"));
        Log.d(app.getLOG_TAG(), "StartActivity.statusConnect " + app.getDataURL("400"));
        String vErrorToken="Аутентификация OK";
        btn.setEnabled(true);
        btnOff.setEnabled(false);
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
                        btnOff.setEnabled(true);
                    }
                    //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    vErrorToken="Аутентификация OK";
                 //   vErrorToken="Аутентификация ERROR";
                //    app.getmDbHelper().refreshOrgs(builder.toString());
                    btn.setEnabled(true);
                    btnOff.setEnabled(false);
                    //e.printStackTrace();
                }
            }
            else {
                vErrorNetwork = "Сеть ERROR";

                vErrorToken="Аутентификация ERROR";
                btn.setEnabled(false);
                btnOff.setEnabled(true);
                //Log.e("Login fail", "Login fail");
            }
        }
        catch (ClientProtocolException e) {
            vErrorNetwork = "Сеть ERROR";
            vErrorToken="Аутентификация ERROR";
            btn.setEnabled(false);
            btnOff.setEnabled(true);
            e.printStackTrace();
        }
        catch (IOException e) {
            vErrorNetwork = "Сеть ERROR";
            vErrorToken="Аутентификация ERROR";
            btn.setEnabled(false);
            btnOff.setEnabled(true);
            e.printStackTrace();
        }



        if (app.getToken()==null)
        {
            vErrorToken="Аутентификация  ERROR";
            if (vErrorNetwork=="Сеть OK") //Если без авторизации сеть ок то блокируем кнопки
            {
                btn.setEnabled(false);
            }
        }

 btnOff.setEnabled(true); //потом убрать
        return vErrorNetwork+"; "+vErrorToken;
    }*/


    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(10); //  TimeUnit.SECONDS.sleep(2);
         //       Log.d("1","!!!!!!!!!!!!!!1");
                //Загрузка данных

                Boolean vStatus = false;
                try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                //MobileTOiRApp app = MobileTOiRApp.getInstance();
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadPeople ");
                HttpGet httpGet = new HttpGet(mMobileSKDApp.getSKDDataURL("602"));
                //Log.d("1","http");
                //Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.d("2","statusCode!!!!="+statusCode);
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {

                            builder.append(line);
                              }
                       // Log.d("4","dead?= "+line);
                        mMobileSKDApp.getmDbHelper().loadSKDPeople(builder.toString());

                    }
                    else {
                        //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //***************************
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadPeople ");
                HttpGet httpGet =  new HttpGet(mMobileSKDApp.getSKDDataURL("601"));
                //Log.d("1","http");
                //Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.d("2","statusCode!!!!="+statusCode);
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {

                            builder.append(line);
                        }
                        // Log.d("4","dead?= "+line);
                        mMobileSKDApp.getmDbHelper().loadSKDObj(builder.toString());

                    }
                    else {
                        //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("2","End");
            Toast.makeText(context, "Загрузка завершена", Toast.LENGTH_LONG).show();

        }
    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private String mToken = "null";

        UserLoginTask(String rfId) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean vStatus = false;
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client =mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfId));

                Log.d(mMobileSKDApp.getLOG_TAG(), "OperLogin.UserLoginTask " +mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfId));

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200 )
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

                                mToken = jsonObject.getString("token");
                                mMobileSKDApp.SKDOperator = jsonObject.getString("oper");
                                vStatus = true;
                                //   Log.d(jsonObject.getString("oper"),"Tst");

                            }
                            //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    else {
                        //Log.e("Login fail", "Login fail");
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(10);
                vStatus = vStatus && !mToken.equals("null");
                if (vStatus) {

                    mMobileSKDApp.setmHASH(mToken);
                    //  mMobileSKDApp.getmDbHelper().refreshOrgs(builder.toString());
                    return true;
                } else {
                    return false;
                }

            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            // showProgress(false); ******************************************************заменим потом

            if (success) {
                Log.d("Is OK","Is OK");
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, AccLogin.class);

                startActivity(intent);

                setContentView(R.layout.activity_my);
                //LinearLayout mainLayout=(LinearLayout)findViewById(R.id.M);
                Button KPPbutton=(Button)findViewById(R.id.SetKPP);
                KPPbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btm_aut)); //setBackgroundResource
                KPPbutton.setTextColor(Color.rgb(0,0,0));
                Button Logbutton=(Button)findViewById(R.id.Loginbutton);
                Logbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact)); //setBackgroundResource
                Logbutton.setTextColor(Color.rgb(65,169,4));
                mMobileSKDApp.SKDStep = "2";
                //finish();
            } else {
                //   super.onCreate(savedInstanceState);
                Log.d("go error page","way");
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, ErrorLogin.class);

                startActivity(intent);

                //  setContentView(R.layout.error_l);

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //   showProgress(false);****************************************************тоже
        }


    }
    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mMobileSKDApp.SKDRfId=bytesToHex(myTag.getId());
            Log.d( mMobileSKDApp.SKDRfId, "=mCode");
            attemptLogin();
            Log.d(mMobileSKDApp.SKDOperator,"operator !!!!!!");
            vibrate();
            Log.d("Поехали","action !!!!!!");
            //    Intent intentSt = new Intent(this, MyActivity.class);
            //    startActivityForResult(intentSt, 1);

        }
    }

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        byte[] nb ={45,-93, 102, -3};
        char[] hexChars = new char[bytes.length * 2];
        // char[] hexChars = new char[nb.length * 2];
        int v;
       /* for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }*/
      /*  for ( int j = nb.length-1; j >=0; j-- ) {
            v = nb[j] & 0xFF;
            hexChars[(nb.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(nb.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }*/
        for ( int j = bytes.length-1; j >=0; j-- ) {
            v = bytes[j] & 0xFF;
            hexChars[(bytes.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(bytes.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public void StartScreen ()
    {
        Button KPPbutton=(Button)findViewById(R.id.SetKPP);
        Button Logbutton=(Button)findViewById(R.id.Loginbutton);
        Button Scanbutton=(Button)findViewById(R.id.ScanBtn);
        Button Exitbutton=(Button)findViewById(R.id.ExitBtn);

        Exitbutton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              finish();
                                              if (mMobileSKDApp.SKDStep=="3")
                                              {
                                                  Log.d("exit","exit");
                                                  finish();
                                              }
                                          }
                                      }
        );

        KPPbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Go KPP","Go KPP");
                if (mMobileSKDApp.SKDStep == "2") {
                    Intent intent = new Intent(view.getContext(), SetKPPAct.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);
                }
            }
        });



        Log.d(mMobileSKDApp.SKDStep,"zzzzzzzzzzzzzzzzmMobileSKDApp.SKDStep");

        // Button loginButton = (Button) findViewById(R.id.Loginbutton);
        Scanbutton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent = new Intent(view.getContext(),OperLogin.class);
                                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                              view.getContext().startActivity(intent);
                                          }
                                      }
        );

     if (mMobileSKDApp.SKDStep=="3")
        {
        //    setContentView(R.layout.activity_my);
            //LinearLayout mainLayout=(LinearLayout)findViewById(R.id.M);
            KPPbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact)); //setBackgroundResource
            KPPbutton.setTextColor(Color.rgb(65,169,4));

            Logbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact)); //setBackgroundResource
            Logbutton.setTextColor(Color.rgb(65,169,4));

            Scanbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnscan)); //setBackgroundResource
            Scanbutton.setTextColor(Color.rgb(0,0,0));

            Exitbutton.setTextColor(Color.rgb(0,0,0));
        }
Log.d(mMobileSKDApp.SKDStep,"zzzzzzzzzzzzzzzzmMobileSKDApp.SKDStep");

    }
}
