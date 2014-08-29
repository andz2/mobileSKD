package xxmmk.mobileskd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    Context context;
    ProgressDialog ringProgressDialog;
    private LoadObjects mLoadTask = null;
    MyTask mt;
        //private SaveObjects mSaveTask = null;

    @Override
    protected void onStart(){

        super.onStart();


        TextView loginInfo = (TextView) findViewById(R.id.LognText);
        loginInfo.setText(statusConnect());

        Button syncButton = (Button) findViewById(R.id.SyncBt);
        syncButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              Log.d("1", "1mCode=");
                                              String countNewCode = mMobileSKDApp.getmDbHelper().getCountNewPeople();
                                              ad = new AlertDialog.Builder(MyActivity.this);
                                              ad.setTitle("Загрузка");  // заголовок
                                              ad.setMessage("Загружено "+countNewCode+" владельцев карт"); // сообщение
                                              String button1String = "Да";
                                              String button2String = "Отмена";
                                              ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                                                  public void onClick(DialogInterface dialog, int arg1) {
                                                      //String countNewCode = mMobileTOiRApp.getmDbHelper().getCountNewCodeByOrg(mOrgId);
                                                      Toast.makeText(context, "Начало загрузки", Toast.LENGTH_LONG).show();
                                                      mt = new MyTask();
                                                      mt.execute();

                                                  }
                                              });
                                              ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                                                  public void onClick(DialogInterface dialog, int arg1) {
                                                      Toast.makeText(context, "Отмена", Toast.LENGTH_LONG).show();
                                                      //saveHierarchy(mOrgId);
                                                  }
                                              });
                                              ad.setCancelable(false);
                                              ad.show();
                                             /* mt = new MyTask();
                                              mt.execute();*/
                                             // loadSKDPeople();
                                              Log.d("2", "!!!!2mCode=");
                                          }
                                      }
        );

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

        Button scanOffButton = (Button) findViewById(R.id.OffBt);
        scanOffButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Intent intent = new Intent(view.getContext(),activity_scan_off.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               view.getContext().startActivity(intent);
                                           }
                                       }
        );


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        context = MyActivity.this;


//---------------------------------------------------------



     //---------------------------------------------------
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(R.layout.activity_my);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //политика сетевого доступа
        StrictMode.setThreadPolicy(policy); //применяем политику





    }


 /*   @Override --убираем меню
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

 //btnOff.setEnabled(true); //потом убрать
        return vErrorNetwork+"; "+vErrorToken;
    }

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
}
