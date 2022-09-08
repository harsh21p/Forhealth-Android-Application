package com.example.forhealth.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "forhealth.db";
    private static final int DATABASE_VERSION = 2;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String CAREGIVER_TABLE_NAME= "CAREGIVERS";
    private static final String CAREGIVER_ID = "CAREGIVER_ID";
    private static final String CAREGIVER_NAME = "CAREGIVER_NAME";
    private static final String CAREGIVER_AVATAR = "CAREGIVER_AVATAR";
    private static final String CAREGIVER_EMAIL_ID = "CAREGIVER_EMAIL_ID";
    private static final String CAREGIVER_LOGIN_PIN= "CAREGIVER_LOGIN_PIN";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String SESSIONS_TABLE_NAME= "SESSIONS";
    private static final String SESSIONS_ID = "SESSIONS_ID";
    private static final String SESSIONS_NAME = "SESSIONS_NAME";
    private static final String CAREGIVER_ID_IN_SESSIONS = "CAREGIVER_ID_IN_SESSIONS";
    private static final String PATIENT_ID_IN_SESSIONS = "PATIENT_ID_IN_SESSIONS";
    private static final String SESSIONS_DATE = "SESSIONS_DATE";
    private static final String SESSIONS_TIME = "SESSIONS_TIME";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATIENT_TABLE_NAME= "PATIENTS";
    private static final String PATIENT_ID = "PATIENT_ID";
    private static final String CAREGIVER_ID_IN_PATIENT = "CAREGIVER_ID_IN_PATIENT";
    private static final String PATIENT_AVATAR = "PATIENT_AVATAR";
    private static final String PATIENT_NAME = "PATIENT_NAME";
    private static final String PATIENT_SEX = "PATIENT_SEX";
    private static final String PATIENT_HEIGHT = "PATIENT_HEIGHT";
    private static final String PATIENT_WEIGHT = "PATIENT_WEIGHT";
    private static final String PATIENT_CONTACT = "PATIENT_CONTACT";
    private static final String PATIENT_DATE = "PATIENT_DATE";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String EXERCISE_TABLE_NAME= "EXERCISES";
    private static final String EXERCISE_ID = "EXERCISE_ID";
    private static final String CAREGIVER_ID_IN_EXERCISE = "CAREGIVER_ID_IN_EXERCISE";
    private static final String PATIENT_ID_IN_EXERCISE = "PATIENT_ID_IN_EXERCISE";
    private static final String SESSION_ID_IN_EXERCISE = "SESSION_ID_IN_EXERCISE";
    private static final String EXERCISE_PARAMETERS = "EXERCISE_PARAMETERS";
    private static final String EXERCISE_DATE = "EXERCISE_DATE";
    private static final String EXERCISE_TIME = "EXERCISE_TIME";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String DATA_TABLE_NAME= "DATA";
    private static final String DATA_ID = "DATA_ID";
    private static final String CAREGIVER_ID_IN_DATA = "CAREGIVER_ID_IN_DATA";
    private static final String SESSION_ID_IN_DATA = "SESSION_ID_IN_DATA";
    private static final String EXERCISE_ID_IN_DATA = "EXERCISE_ID_IN_DATA";
    private static final String PATIENT_ID_IN_DATA = "PATIENT_ID_IN_DATA";
    private static final String DATA_TIMESTAMP = "DATA_TIMESTAMP";
    private static final String DATA = "DATA";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String IDENTIFIER = "IDENTIFIER";

    private static SQLiteDatabase sqLiteDatabaseReference;

    public  MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
        sqLiteDatabaseReference = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+CAREGIVER_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+SESSIONS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+PATIENT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+EXERCISE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATA_TABLE_NAME);
        createTables(sqLiteDatabase);
    }

    @Override
    public void onDowngrade (SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+CAREGIVER_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+SESSIONS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+PATIENT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+EXERCISE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATA_TABLE_NAME);
        createTables(sqLiteDatabase);
    }

    private void createTables(SQLiteDatabase sqLiteDatabase){
        String query = "CREATE TABLE "+ CAREGIVER_TABLE_NAME + " ("+CAREGIVER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAREGIVER_NAME+" TEXT, "+CAREGIVER_LOGIN_PIN+" TEXT, "+CAREGIVER_EMAIL_ID+" TEXT, "+CAREGIVER_AVATAR+" TEXT );";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE "+ SESSIONS_TABLE_NAME + " ("+SESSIONS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAREGIVER_ID_IN_SESSIONS+" INTEGER, "+PATIENT_ID_IN_SESSIONS+" INTEGER, "+SESSIONS_NAME+" TEXT, "+SESSIONS_DATE+" TEXT, "+SESSIONS_TIME+" TEXT, "+IDENTIFIER+" TEXT );";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE "+ PATIENT_TABLE_NAME + " ("+PATIENT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAREGIVER_ID_IN_PATIENT+" INTEGER, "+PATIENT_NAME+" TEXT, "+PATIENT_AVATAR+" INTEGER, "+PATIENT_WEIGHT+" INTEGER, "+PATIENT_HEIGHT+" INTEGER, "+PATIENT_SEX+" TEXT, "+PATIENT_CONTACT+" TEXT,"+PATIENT_DATE+" TEXT );";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE "+ EXERCISE_TABLE_NAME + " ("+EXERCISE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAREGIVER_ID_IN_EXERCISE+" INTEGER, "+SESSION_ID_IN_EXERCISE+" INTEGER, "+PATIENT_ID_IN_EXERCISE+" INTEGER, "+EXERCISE_PARAMETERS+" TEXT, "+EXERCISE_DATE+" TEXT, "+EXERCISE_TIME+" TEXT, "+ IDENTIFIER +" TEXT );";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE "+ DATA_TABLE_NAME + " ("+DATA_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAREGIVER_ID_IN_DATA+" INTEGER, "+PATIENT_ID_IN_DATA+" INTEGER, "+SESSION_ID_IN_DATA+" INTEGER, "+EXERCISE_ID_IN_DATA+" INTEGER, "+DATA+" TEXT, "+DATA_TIMESTAMP+" TEXT, "+ IDENTIFIER +" TEXT );";
        sqLiteDatabase.execSQL(query);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String updateTableSessions( String U_SESSIONS_ID, String U_CAREGIVER_ID_IN_SESSIONS,String U_PATIENT_ID_IN_SESSIONS,String U_SESSIONS_NAME,String U_SESSIONS_DATE, String U_SESSIONS_TIME,String U_SESSION_I) {
        return supportAddSessions("u",U_SESSIONS_ID,U_CAREGIVER_ID_IN_SESSIONS,U_PATIENT_ID_IN_SESSIONS,U_SESSIONS_NAME,U_SESSIONS_DATE,U_SESSIONS_TIME,U_SESSION_I);
    }

    public String addDataToSessions(String U_CAREGIVER_ID_IN_SESSIONS,String U_PATIENT_ID_IN_SESSIONS,String U_SESSIONS_NAME,String U_SESSIONS_DATE, String U_SESSIONS_TIME,String U_SESSIONS_IDENTIFIER){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + SESSIONS_TABLE_NAME + " where SESSIONS_NAME ='" + U_SESSIONS_NAME + "' and PATIENT_ID_IN_SESSIONS = "+U_PATIENT_ID_IN_SESSIONS, null);
            boolean exists = cursor.moveToFirst();
            if (exists == true) {
                return "Already exist";
            } else {
               return supportAddSessions("i","0",U_CAREGIVER_ID_IN_SESSIONS,U_PATIENT_ID_IN_SESSIONS,U_SESSIONS_NAME,U_SESSIONS_DATE,U_SESSIONS_TIME,U_SESSIONS_IDENTIFIER);
            }
    }

    public String updateTableCaregivers(String ID, String U_CAREGIVER_NAME, String U_CAREGIVER_EMAIL_ID,String U_CAREGIVER_LOGIN_PIN,String U_CAREGIVER_AVATAR) {
        return supportAddCaregivers("u",ID,U_CAREGIVER_NAME,U_CAREGIVER_EMAIL_ID,U_CAREGIVER_LOGIN_PIN,U_CAREGIVER_AVATAR);
    }

    public String addDataToCaregivers(String U_CAREGIVER_NAME, String U_CAREGIVER_EMAIL_ID,String  U_CAREGIVER_LOGIN_PIN,String  U_CAREGIVER_AVATAR){

        Cursor cursor = readData(CAREGIVER_TABLE_NAME);
        Boolean exist = cursor.moveToFirst();
        if(!exist){
            return supportAddCaregivers("i","0",U_CAREGIVER_NAME,U_CAREGIVER_EMAIL_ID,U_CAREGIVER_LOGIN_PIN,U_CAREGIVER_AVATAR);
        }
        else {
            cursor = readDataByStringID(U_CAREGIVER_EMAIL_ID,CAREGIVER_EMAIL_ID,CAREGIVER_TABLE_NAME);
            boolean exists = cursor.moveToFirst();
            if (exists) {
                return "Already exist";
            } else {
                return supportAddCaregivers("i","0",U_CAREGIVER_NAME,U_CAREGIVER_EMAIL_ID,U_CAREGIVER_LOGIN_PIN,U_CAREGIVER_AVATAR);
            }
        }
    }

    public String updateTablePatients( String U_PATIENT_ID,String U_PATIENT_AVATAR, String U_CAREGIVER_ID_IN_PATIENT,String U_PATIENT_HEIGHT,String U_PATIENT_WEIGHT,String U_PATIENT_NAME,String U_PATIENT_SEX,String U_PATIENT_DATE, String U_PATIENT_CONTACT) {
        return supportAddPatients("u", U_PATIENT_ID,U_PATIENT_AVATAR, U_CAREGIVER_ID_IN_PATIENT,U_PATIENT_HEIGHT,U_PATIENT_WEIGHT,U_PATIENT_NAME,U_PATIENT_SEX,U_PATIENT_DATE, U_PATIENT_CONTACT);
    }

    public String addDataToPatients(String U_PATIENT_AVATAR, String U_CAREGIVER_ID_IN_PATIENT,String U_PATIENT_HEIGHT,String U_PATIENT_WEIGHT,String U_PATIENT_NAME,String U_PATIENT_SEX,String U_PATIENT_DATE, String U_PATIENT_CONTACT){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + PATIENT_TABLE_NAME + " where PATIENT_NAME ='" + U_PATIENT_NAME + "' and PATIENT_CONTACT = "+U_PATIENT_CONTACT, null);
        cursor.moveToFirst();
        boolean exists = cursor.moveToFirst();
        if (exists == true) {
            return "Already exist";
        } else {
            return supportAddPatients("i","0",U_PATIENT_AVATAR, U_CAREGIVER_ID_IN_PATIENT,U_PATIENT_HEIGHT,U_PATIENT_WEIGHT,U_PATIENT_NAME,U_PATIENT_SEX,U_PATIENT_DATE, U_PATIENT_CONTACT);
        }
    }

    public String updateTableExercises( String U_EXERCISE_ID,String U_CAREGIVER_ID_IN_EXERCISE, String U_PATIENT_ID_IN_EXERCISE,String U_SESSION_ID_IN_EXERCISE,String U_EXERCISE_PARAMETERS,String U_EXERCISE_DATE,String U_EXERCISE_TIME,String U_IDENTIFIER) {
        return supportAddExercises("u", U_EXERCISE_ID,U_CAREGIVER_ID_IN_EXERCISE, U_PATIENT_ID_IN_EXERCISE,U_SESSION_ID_IN_EXERCISE,U_EXERCISE_PARAMETERS,U_EXERCISE_DATE,U_EXERCISE_TIME,U_IDENTIFIER);
    }

    public String addDataToExercises(String U_CAREGIVER_ID_IN_EXERCISE, String U_PATIENT_ID_IN_EXERCISE,String U_SESSION_ID_IN_EXERCISE,String U_EXERCISE_PARAMETERS,String U_EXERCISE_DATE,String U_EXERCISE_TIME,String U_IDENTIFIER){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return supportAddExercises("i","0",U_CAREGIVER_ID_IN_EXERCISE, U_PATIENT_ID_IN_EXERCISE,U_SESSION_ID_IN_EXERCISE,U_EXERCISE_PARAMETERS,U_EXERCISE_DATE,U_EXERCISE_TIME,U_IDENTIFIER);
    }

    public String updateTableData( String U_DATA_ID, String U_CAREGIVER_ID_IN_DATA,String U_PATIENT_ID_IN_DATA,String U_EXERCISE_ID_IN_DATA, String U_DATA_TIMESTAMP, String U_DATA, String U_DATA_IDENTIFIER) {
        return supportAddData("u",U_DATA_ID,U_CAREGIVER_ID_IN_DATA,U_PATIENT_ID_IN_DATA,U_EXERCISE_ID_IN_DATA,U_DATA_TIMESTAMP,U_DATA,U_DATA_IDENTIFIER);
    }

    public String addDataToData( String U_CAREGIVER_ID_IN_DATA,String U_PATIENT_ID_IN_DATA,String U_EXERCISE_ID_IN_DATA, String U_DATA_TIMESTAMP, String U_DATA,String U_DATA_IDENTIFIER){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return supportAddData("i","0",U_CAREGIVER_ID_IN_DATA,U_PATIENT_ID_IN_DATA,U_EXERCISE_ID_IN_DATA,U_DATA_TIMESTAMP,U_DATA,U_DATA_IDENTIFIER);
    }


   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String supportAddCaregivers(String cmd,String ID, String U_CAREGIVER_NAME, String U_CAREGIVER_EMAIL_ID,String U_CAREGIVER_LOGIN_PIN,String U_CAREGIVER_AVATAR){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CAREGIVER_NAME,U_CAREGIVER_NAME);
        contentValues.put(CAREGIVER_EMAIL_ID,U_CAREGIVER_EMAIL_ID);
        contentValues.put(CAREGIVER_LOGIN_PIN,U_CAREGIVER_LOGIN_PIN);
        contentValues.put(CAREGIVER_AVATAR,U_CAREGIVER_AVATAR);

        long result;
        if(cmd == "i") {
            result = sqLiteDatabase.insert(CAREGIVER_TABLE_NAME, null, contentValues);
        }else {
            if(cmd == "u") {
                result = sqLiteDatabase.update(CAREGIVER_TABLE_NAME, contentValues, CAREGIVER_ID+" = ?", new String[]{ID});
            }else {
                result = -1;
            }
        }

        if(result == -1){
            return "Failed";
        }else {
            return "Successful";
        }
    }

    String supportAddSessions(String cmd, String U_SESSIONS_ID, String U_CAREGIVER_ID_IN_SESSIONS,String U_PATIENT_ID_IN_SESSIONS,String U_SESSIONS_NAME,String U_SESSIONS_DATE, String U_SESSIONS_TIME,String U_SESSIONS_IDENTIFIER){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CAREGIVER_ID_IN_SESSIONS,U_CAREGIVER_ID_IN_SESSIONS);
        contentValues.put(PATIENT_ID_IN_SESSIONS,U_PATIENT_ID_IN_SESSIONS);
        contentValues.put(SESSIONS_NAME,U_SESSIONS_NAME);
        contentValues.put(SESSIONS_DATE,U_SESSIONS_DATE);
        contentValues.put(SESSIONS_TIME,U_SESSIONS_TIME);
        contentValues.put(IDENTIFIER,U_SESSIONS_IDENTIFIER);

        long result;
        if(cmd == "i") {
            result = sqLiteDatabase.insert(SESSIONS_TABLE_NAME, null, contentValues);
        }else {
            if(cmd == "u") {
                result = sqLiteDatabase.update(SESSIONS_TABLE_NAME, contentValues, SESSIONS_ID+" = ?", new String[]{U_SESSIONS_ID});
            }else {
                result = -1;
            }
        }

        if(result == -1){
            return "Failed";
        }else {
            return "Successful";
        }
    }



    String supportAddPatients(String cmd, String U_PATIENT_ID,String U_PATIENT_AVATAR, String U_CAREGIVER_ID_IN_PATIENT,String U_PATIENT_HEIGHT,String U_PATIENT_WEIGHT,String U_PATIENT_NAME,String U_PATIENT_SEX,String U_PATIENT_DATE, String U_PATIENT_CONTACT){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PATIENT_NAME,U_PATIENT_NAME);
        contentValues.put(CAREGIVER_ID_IN_PATIENT,U_CAREGIVER_ID_IN_PATIENT);
        contentValues.put(PATIENT_SEX,U_PATIENT_SEX);
        contentValues.put(PATIENT_CONTACT,U_PATIENT_CONTACT);
        contentValues.put(PATIENT_DATE,U_PATIENT_DATE);
        contentValues.put(PATIENT_HEIGHT,U_PATIENT_HEIGHT);
        contentValues.put(PATIENT_WEIGHT,U_PATIENT_WEIGHT);
        contentValues.put(PATIENT_AVATAR,U_PATIENT_AVATAR);


        long result;
        if(cmd == "i") {
            result = sqLiteDatabase.insert(PATIENT_TABLE_NAME, null, contentValues);
        }else {
            if(cmd == "u") {
                result = sqLiteDatabase.update(PATIENT_TABLE_NAME, contentValues, SESSIONS_ID+" = ?", new String[]{U_PATIENT_ID});
            }else {
                result = -1;
            }
        }

        if(result == -1){
            return "Failed";
        }else {
            return "Successful";
        }
    }

    String supportAddExercises(String cmd, String U_EXERCISE_ID,String U_CAREGIVER_ID_IN_EXERCISE, String U_PATIENT_ID_IN_EXERCISE,String U_SESSION_ID_IN_EXERCISE,String U_EXERCISE_PARAMETERS,String U_EXERCISE_DATE,String U_EXERCISE_TIME,String U_IDENTIFIER){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CAREGIVER_ID_IN_EXERCISE,U_CAREGIVER_ID_IN_EXERCISE);
        contentValues.put(PATIENT_ID_IN_EXERCISE,U_PATIENT_ID_IN_EXERCISE);
        contentValues.put(SESSION_ID_IN_EXERCISE,U_SESSION_ID_IN_EXERCISE);
        contentValues.put(EXERCISE_PARAMETERS,U_EXERCISE_PARAMETERS);
        contentValues.put(EXERCISE_DATE,U_EXERCISE_DATE);
        contentValues.put(EXERCISE_TIME,U_EXERCISE_TIME);
        contentValues.put(IDENTIFIER,U_IDENTIFIER);

        long result;
        if(cmd == "i") {
            result = sqLiteDatabase.insert(EXERCISE_TABLE_NAME, null, contentValues);
        }else {
            if(cmd == "u") {
                result = sqLiteDatabase.update(EXERCISE_TABLE_NAME, contentValues, EXERCISE_ID+" = ?", new String[]{U_EXERCISE_ID});
            }else {
                result = -1;
            }
        }

        if(result == -1){
            return "Failed";
        }else {
            return "Successful";
        }
    }

    String supportAddData(String cmd, String U_DATA_ID, String U_CAREGIVER_ID_IN_DATA,String U_PATIENT_ID_IN_DATA,String U_EXERCISE_ID_IN_DATA, String U_DATA_TIMESTAMP, String U_DATA, String U_DATA_IDENTIFIER){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CAREGIVER_ID_IN_DATA,U_CAREGIVER_ID_IN_DATA);
        contentValues.put(PATIENT_ID_IN_DATA,U_PATIENT_ID_IN_DATA);
        contentValues.put(EXERCISE_ID_IN_DATA,U_EXERCISE_ID_IN_DATA);
        contentValues.put(DATA_TIMESTAMP,U_DATA_TIMESTAMP);
        contentValues.put(DATA,U_DATA);
        contentValues.put(IDENTIFIER,U_DATA_IDENTIFIER);

        long result;
        if(cmd == "i") {
            result = sqLiteDatabase.insert(DATA_TABLE_NAME, null, contentValues);
        }else {
            if(cmd == "u") {
                result = sqLiteDatabase.update(DATA_TABLE_NAME, contentValues, DATA_ID+" = ?", new String[]{U_DATA_ID});
            }else {
                result = -1;
            }
        }

        if(result == -1){
            return "Failed";
        }else {
            return "Successful";
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Cursor readData(String TABLE_NAME){
        String query = "SELECT * FROM "+ TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqLiteDatabase!=null){
            cursor = sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }

    public Cursor readDataByStringID(String ID,String WHICH_ID,String TABLE_NAME){
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+WHICH_ID+" = '"+ID+"'" ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqLiteDatabase!=null){
            cursor = sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }

    public Cursor readDataByIntID(int ID,String WHICH_ID,String TABLE_NAME){
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+WHICH_ID+" = "+ID+";" ;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqLiteDatabase!=null){
            cursor = sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }

    public String deleteIntEntry(String ID,String WHICH_ID,String TABLE_NAME) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long result = sqLiteDatabase.delete(TABLE_NAME,WHICH_ID+" = ?",new String[]{ID});
        if(result == -1){
            return "Failed";
        }else {
            return "Successful";
        }
    }

    public Cursor readSession(String U_IDENTIFIER,String CAREGIVER_ID,String SESSION_ID){
        String query = "SELECT * FROM "+EXERCISE_TABLE_NAME+" WHERE "+CAREGIVER_ID_IN_EXERCISE+" = "+CAREGIVER_ID+" and "+IDENTIFIER+" = "+U_IDENTIFIER+" and "+SESSION_ID_IN_EXERCISE+" = "+SESSION_ID+";";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqLiteDatabase!=null){
            cursor = sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }



    public Cursor readSessionByPatientId(String U_IDENTIFIER, String CAREGIVER_ID, String PATIENT_ID) {
        String query = "SELECT * FROM "+SESSIONS_TABLE_NAME+" WHERE "+CAREGIVER_ID_IN_SESSIONS+" = "+CAREGIVER_ID+" and "+IDENTIFIER+" = "+U_IDENTIFIER+" and "+PATIENT_ID_IN_SESSIONS+" = "+PATIENT_ID+";";

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqLiteDatabase!=null){
            cursor = sqLiteDatabase.rawQuery(query,null);
        }
        return cursor;
    }
}
