package com.mod.sunjae.assist;

        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;

        import java.util.ArrayList;

/**
 * Created by 김상현 on 2016-03-18.
 */
public class DbHelper {

    SQLiteDatabase database;    //SQLite database 생성하는 객체명
    String dbName;  //DB 명
    String createTable1;
    String createTable2;
    String createTable3;
    String tableName1;
    String tableName2;
    String tableName3;
    int num, checknum;

    ArrayList<String> arrname;
    ArrayList<String> arrssid;
    ArrayList<String> arrid_bssid;
    public DbHelper() {
        arrname = new ArrayList<String>();
        arrssid = new ArrayList<String>();
        arrid_bssid = new ArrayList<String>();

        tableName1 = null;
        tableName2 = null;
        tableName3 = null;
        dbName = "assist_db";
        createTable1 = "CREATE TABLE 'set_table' ('id' integer primary key autoincrement, 'name' text, 'ssid' text, 'volume' integer, 'light' integer, 'vibe' integer, 'connect' integer);";
        //createTable1: set_table 테이블 생성 sql
        createTable2 = "CREATE TABLE 'bssid_table' ('id' integer primary key autoincrement, 'id_name' text, 'id_ssid' text, 'id_bssid' text);";
        //createTable2: bssid_table 테이블 생성 sql
        createTable3 = "CREATE TABLE 'setting_table' ('id' integer primary key autoincrement,  'save' integer, 'alarm' integer);";
        //createTable3: setting_table 테이블 생성 sql
    }

    public void closeDatabase(){
        database.close();
    }
    public void createTable(){
        String checksql = "select * from sqlite_master Where Name = 'set_table' or Name = 'bssid_table' or Name = 'setting_table';";
        //sqlite_master 테이블을 통해 앱 구동에 필요한 테이블이 생성되었는지 확인
        Cursor check = database.rawQuery(checksql, null);
        check.moveToFirst();
        while(!check.isAfterLast()){
            if(check.getString(1).equals("set_table")){tableName1 = "set_table";}
            if(check.getString(1).equals("bssid_table")){tableName2 = "bssid_table";}
            if(check.getString(1).equals("setting_table")){tableName3 = "setting_table";}
            check.moveToNext();
        }
        checknum = check.getCount();
        check.close();
        if(checknum == 0) {
            //앱 구동에 필요한 테이블이 하나도 생성 안된 경우
            try {
                database.execSQL(createTable1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                database.execSQL(createTable2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                database.execSQL(createTable3);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(checknum == 1) {
            //앱 구동에 필요한 테이블이 1개만 생성된 경우
            if(tableName1.equals("set_table")){
                try {
                    database.execSQL(createTable2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(tableName2.equals("bssid_table")){
                try {
                    database.execSQL(createTable1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(tableName3.equals("setting_table")){
                try {
                    database.execSQL(createTable1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(checknum == 2) {
            //앱 구동에 필요한 테이블이 2개만 생성된 경우
            if(tableName1 == null){
                try {
                    database.execSQL(createTable1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(tableName2 == null){
                try {
                    database.execSQL(createTable2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(tableName3 == null){
                try {
                    database.execSQL(createTable3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void insertData(String name, String ssid, int volume, int light, int vibe, int connect){
        database.beginTransaction();
        try{
            String sql1 = "INSERT INTO set_table ('name', 'ssid', 'volume', 'light', 'vibe', 'connect') VALUES ('"+ name +"', '"+ ssid +"', '"+ volume +"', '"+ light +"', '"+ vibe +"', '"+ connect +"');";
            database.execSQL(sql1); //sql1 실행
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public void insertData2(String id_name, String id_ssid, String id_bssid){
        database.beginTransaction();
        try{
            String sql2 = "INSERT INTO bssid_table ('id_name', 'id_ssid', 'id_bssid') VALUES ('"+ id_name +"', '"+ id_ssid +"', '"+ id_bssid +"');";
            database.execSQL(sql2); //sql2 실행
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public void insertData3(int save, int alarm)
    {
        database.beginTransaction();
        try{
            String sql3 = "INSERT INTO setting_table ('save', 'alarm') VALUES ('"+ save +"', '"+ alarm +"');";
            database.execSQL(sql3); //sql3 실행
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public void showallData(){
        String sql2 = "select \"name\", \"ssid\" from set_table";
        Cursor result = database.rawQuery(sql2, null);  //sql2 실행 후 커서로 받아서 값을 뽑아냄
        result.moveToFirst();
        while(!result.isAfterLast()){
            arrname.add(result.getString(0));
            arrssid.add(result.getString(1));
            result.moveToNext();
        }
        num = result.getCount();
        result.close();

    }
    public void update_set(int save, int alarm){
        database.beginTransaction();
        try{
            String up_sql2 = "update setting_table set \"save\" = '"+ save +"', \"alarm\" = '"+ alarm +"' where \"id\" = '"+ 1+"';";
            // setting_page에서 받은 값 수정(스캔 주기 값, noti 띄울 것인지에 대한 여부
            database.execSQL(up_sql2);
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public void update_ssid(String name, String ssid, String up_name, String up_ssid, int up_volume, int up_light, int up_vibe, int up_connect){
        database.beginTransaction();
        try{
            String up_sql1 = "update set_table set \"name\" = '"+ up_name +"', \"ssid\" = '"+ up_ssid +"', \"volume\" = '"+ up_volume +"', \"light\" = '"+ up_light +"', \"vibe\" = '"+ up_vibe +"', \"connect\" = '"+ up_connect +"' where \"name\" = '"+ name +"'and \"ssid\" = '"+ ssid +"';";
            //설정 값 수정하는 sql문
            database.execSQL(up_sql1);
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public void delete_ssid(String name, String ssid){
        database.beginTransaction();
        try{
            String de_sql1 = "Delete from set_table where \"name\" = '"+ name +"' and \"ssid\" = '"+ ssid +"';";
            database.execSQL(de_sql1);
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public ArrayList<String> select_ssid(String name, String ssid){
        ArrayList<String> resArr2 = new ArrayList<String>();

        String sel_sql1 = "select \"volume\", \"light\", \"vibe\", \"connect\" from set_table where \"name\" = '"+ name +"' and \"ssid\" = '"+ ssid +"';";
        //name과 ssid을 이용해 DB에서 설정값을 뽑아오는 sql문
        Cursor selCur = database.rawQuery(sel_sql1, null);
        selCur.moveToFirst();
        while(!selCur.isAfterLast()){
            resArr2.add(selCur.getString(0));
            resArr2.add(selCur.getString(1));
            resArr2.add(selCur.getString(2));
            resArr2.add(selCur.getString(3));
            selCur.moveToNext();
        }
        selCur.close();
        return resArr2;
    }
    public void delete_bssid(String id_name, String id_ssid){
        database.beginTransaction();
        try{
            String de_sql2 = "Delete from bssid_table where \"id_name\" = '"+ id_name +"' and \"id_ssid\" = '"+ id_ssid +"';";
            //bssid는 여러개이므로 목록 수정 시 기존의 bssid를 모두 삭제 후 새로 bssid를 저장
            //같은 Wifi Zone이어도 위치에 따라 인식하는 bssid의 종류와 개수는 다를 수 있다.
            database.execSQL(de_sql2);
            database.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            database.endTransaction();
        }
    }
    public int backset(){
        int cnt=0;
        ArrayList<String> resArr = new ArrayList<String>();

        String comp1 = "select \"*\" from setting_table;";
        Cursor compare1 = database.rawQuery(comp1, null);
        compare1.moveToFirst();
        while(!compare1.isAfterLast()){
            cnt++;
            compare1.moveToNext();
        }
        compare1.close();
        return cnt;
    }
    public ArrayList<String> backsetint(){
        ArrayList<String> resArr = new ArrayList<String>();

        String comp1 = "select \"save\", \"alarm\" from setting_table;";
        Cursor compare1 = database.rawQuery(comp1, null);
        compare1.moveToFirst();
        while(!compare1.isAfterLast()){
            resArr.add(compare1.getString(0));
            resArr.add(compare1.getString(1));
            compare1.moveToNext();
        }
        compare1.close();
        return resArr;
    }
    public ArrayList<String> compareSSID(String ssid){
        ArrayList<String> resArr = new ArrayList<String>();

        String comp1 = "select \"id_bssid\" from bssid_table where id_ssid = '"+ ssid +"';";
        //ssid를 비교하여 ssid와 매칭되는 bssid를 뽑아냄
        Cursor compare1 = database.rawQuery(comp1, null);
        compare1.moveToFirst();
        while(!compare1.isAfterLast()){
            resArr.add(compare1.getString(0));
            compare1.moveToNext();
        }
        compare1.close();
        return resArr;
    }
    public ArrayList<String> select_set(String id_bssid){
        ArrayList<String> setArr = new ArrayList<String>();
        String id_name= null;

        String set1 = "select \"id_name\" from bssid_table where id_bssid = '"+ id_bssid +"';";
        //설정값을 가져오기 위해 bssid에 해당되는 name을 뽑아냄
        Cursor set_Cursor1 = database.rawQuery(set1, null);
        set_Cursor1.moveToFirst();
        while(!set_Cursor1.isAfterLast()){
            id_name = set_Cursor1.getString(0);
            set_Cursor1.moveToNext();
        }
        set_Cursor1.close();
        String set2 = "select \"name\", \"ssid\", \"volume\", \"light\", \"vibe\", \"connect\" from set_table where name = '"+ id_name +"';";
        //name에 해당하는 설정값을 뽑아냄
        Cursor set_Cursor2 = database.rawQuery(set2, null);
        set_Cursor2.moveToFirst();
        while(!set_Cursor2.isAfterLast()){
            setArr.add(set_Cursor2.getString(0));
            setArr.add(set_Cursor2.getString(1));
            setArr.add(set_Cursor2.getString(2));
            setArr.add(set_Cursor2.getString(3));
            setArr.add(set_Cursor2.getString(4));
            setArr.add(set_Cursor2.getString(5));
            set_Cursor2.moveToNext();
        }
        set_Cursor2.close();
        return setArr;
    }
}