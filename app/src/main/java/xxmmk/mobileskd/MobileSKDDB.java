package xxmmk.mobileskd;


        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.List;


public class MobileSKDDB  extends SQLiteOpenHelper {
    Context mContext;

    public MobileSKDDB(Context context) {
        // конструктор суперкласса
        super(context, "SKDDB", null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(((MobileSKDApp)mContext).getLOG_TAG(), "MobileSKDDB.onCreate");
        // создаем таблицу с полями
        db.execSQL("create table settings ("
                + "id integer primary key autoincrement,"
                + "key text,"
                + "value text" + ");");
        db.execSQL("insert into settings (key) values ('username');");
        db.execSQL("insert into settings (key) values ('password');");
        db.execSQL("insert into settings (key) values ('token');");

        db.execSQL("create table orgs ("
                + "id integer primary key autoincrement,"
                + "org_id text,"
                + "org_code text" + ");");
        db.execSQL("insert into settings (key) values ('orgs_date');");

        db.execSQL("create table skd_people ("
                + "cardholder_id integer primary key ,"
                + "rf_id text,"
                + "full_name text,"
                + "employee_number text,"
                + "spec text,"
                + "org text,"
                + "otdel text,"
                + "code text"
                + ");");


        db.execSQL("create table skd_acc ("
                + "card_id integer,"
                + "name text,"
                + "description text"
                + "rf_id text,"
                + "cardholder_id int"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*
        if (newVersion == 2) {
            Log.d(((MobileSKDApp)mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion="+newVersion);
            db.execSQL("create table new_code ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "code text" + ");");
        }

        if (newVersion == 3) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table orgs ("
                    + "id integer primary key autoincrement,"
                    + "org_id text,"
                    + "org_code text" + ");");
            db.execSQL("insert into settings (key) values ('orgs_date');");
        }
        if (newVersion == 4) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text" + ");");
        }
        if (newVersion == 5) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "org_id text"+ ");");
        }
        if (newVersion == 6) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "code text,"
                    + "child_cnt text,"
                    + "org_id text"+ ");");
        }
        if (newVersion == 7) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "org_id text,"
                    + "code text,"
                    + "child_cnt text);");
        }*/
    }

    public String getSettingValue (String key) {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select value from settings where key = ?", new String[] { key });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("value"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public void setSettingValue (String key, String newValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("update settings set value=? where key = ?", new String[]{newValue, key});
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

     public HashMap<String,String> getSKDpeople(String rfID)
    {
        HashMap<String,String> returnList = new HashMap<String,String>();
        Cursor c=null;
        SQLiteDatabase db = this.getWritableDatabase();
        c = db.rawQuery("select full_name ,employee_number ,spec ,org ,otdel,rf_id from skd_people where rf_id=?", new String[] { rfID });
        Log.d("select full_name ,employee_number ,spec ,org ,otdel from skd_people where rf_id="+rfID,"   select ");
        if (c.moveToFirst()) {
            do {
                //c.moveToFirst();
                returnList.put("full_name", c.getString(0));
                returnList.put("employee_number", c.getString(1));
                returnList.put("spec", c.getString(2));
                returnList.put("org", c.getString(3));
                returnList.put("otdel", c.getString(4));
                returnList.put("rf_id", c.getString(5));
              //  returnList.add(temp);
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return returnList;
            }

    public void refreshOrgs (String jsonOrgs) {

        SQLiteDatabase db = this.getWritableDatabase();
        String vOrgId;
        String vOrgCode;

        try
        {
            db.execSQL("delete from orgs", new String[] {});

            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonOrgs);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    vOrgId = jsonObject.getString("ORG_ID");
                    vOrgCode = jsonObject.getString("ORG_CODE");

                    db.execSQL("insert into orgs (org_id,org_code) values (?,?);",new String[] {vOrgId,vOrgCode});

                }
                //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    public ArrayList<HashMap<String,String>> getListOrgs() {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("orgs Order BY org_code",
                new String[] { "*" }, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Cursor c = null;
                c = db.rawQuery("select value from settings where key = ?", new String[] { "loadOrg"+cursor.getString(1) });
                c.moveToFirst();
                String value = "Нет данных";
                if (c.getCount()!=0) {
                    value = c.getString(c.getColumnIndex("value"));
                }
                c.close();
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("ORG_ID", cursor.getString(1));
                temp.put("ORG_CODE", cursor.getString(2));
                temp.put("ORG_LOAD_TIME", value);
                returnList.add(temp);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnList;
    }

    public ArrayList<HashMap<String,String>> getSKDObjects(String rfId) {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        String selection = null;
        String[] selectionArgs = null;

        SQLiteDatabase db = this.getWritableDatabase();

        selection = "rf_id = ?";
        selectionArgs = new String[] { rfId};

        Cursor cursor = db.query("skd_acc ",
                new String[] { "NAME" }, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("NAME", cursor.getString(0));
                returnList.add(temp);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnList;
    }

    public void replaceSettingValue (String key, String newValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("delete from settings where key = ?", new String[]{key});
            db.execSQL("insert into settings (key,value) values(?,?)", new String[]{key,newValue});
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadSKDPeople (String jsonObjects) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
          /*  db.execSQL("delete from new_code where object_id in (select object_id from hierarchy where org_id=?)", new String[] {orgId});

            db.execSQL("delete from hierarchy where org_id=?", new String[] {orgId});
*/
            db.execSQL("delete from skd_people");
         //   db.execSQL("delete from skd_acc");
            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    db.execSQL("insert into skd_people (cardholder_id,rf_id,full_name,employee_number,spec,org,otdel) values (?,?,?,?,?,?,?);",
                            new String[] {jsonObject.getString("CARDHOLDER_ID")
                                    ,jsonObject.getString("RF_ID")
                                    ,jsonObject.getString("FULL_NAME")
                                    ,jsonObject.getString("EMPLOYEE_NUMBER")
                                    ,jsonObject.getString("SPEC")
                                    ,jsonObject.getString("ORG")
                                    ,jsonObject.getString("OTDEL")
                             //       ,jsonObject.getString("CHILD_CNT")
                            });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            replaceSettingValue("loadPeople" , new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }
    public void loadSKDObj (String jsonObjects) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
          db.execSQL("delete from skd_acc");
            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    db.execSQL("insert into skd_acc (card_id ,name ,description,rf_id,cardholder_id ) values (?,?,?,?,?);",
                            new String[] {jsonObject.getString("CARD_ID")
                                    ,jsonObject.getString("NAME")
                                    ,jsonObject.getString("DESCRIPTION")
                                    ,jsonObject.getString("rf_id")
                                    ,jsonObject.getString("CARDHOLDER_ID")
                                    //       ,jsonObject.getString("CHILD_CNT")
                            });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            replaceSettingValue("loadPeople" , new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    public String getObjectId (String orgId){
        String mReturn ="";
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            Cursor c = null;
            c = db.rawQuery("select OBJECT_ID as OBJECT_ID from hierarchy where UP_FLAG ='' and ORG_ID = ?", new String[] { orgId });
            c.moveToFirst();
            mReturn = c.getString(c.getColumnIndex("OBJECT_ID"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return mReturn;
    }

    public String setNewCode (String objectId,String code){
        String mReturn ="";
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            db.execSQL("delete from new_code where object_id=?", new String[] {objectId});

            db.execSQL("insert into new_code(object_id,code)values (?,?)", new String[] {objectId,code});
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return mReturn;
    }

    public String getCountNewCode (String objectId) {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select count(*) as cc from new_code where object_id=?", new String[] { objectId });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("cc"));
            c.close();
            if (value.equals("1")){
                c = db.rawQuery("select code as cc from new_code where object_id=?", new String[] { objectId });
                c.moveToFirst();
                value = c.getString(c.getColumnIndex("cc"));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public String getCountNewPeople () {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select count(*) as cc from skd_people", null);
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("cc"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }
   /*
    public String getCountNewCodeByOrg (String orgId) {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select count(*) as cc from new_code a,hierarchy b where a.object_id=b.object_id and b.org_id=?", new String[] { orgId });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("cc"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }
    */

    public ArrayList<HashMap<String,String>> getNewCodeByOrg (String orgId) {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor cursor = null;
            cursor = db.rawQuery("select a.object_id,a.code from new_code a,hierarchy b where a.object_id=b.object_id and b.org_id=?", new String[] { orgId });
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> temp = new HashMap<String, String>();
                    temp.put("OBJECT_ID", cursor.getString(0));
                    temp.put("CODE", cursor.getString(1));
                    returnList.add(temp);
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return returnList;
    }

    public void DeleteNewCode (String objectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("delete from new_code  where object_id=?", new String[] { objectId });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void UpdateHierarchy (String objectId, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("update hierarchy set code=?  where object_id=?", new String[] {code, objectId });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
