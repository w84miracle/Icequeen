package com.mis.icequeen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.content.ContentProvider;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TestProvider extends ContentProvider {
	public final static String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()	+ "/icequeen";
	public final static String PATH = "/db";
	public static String DATABASE_FILENAME[] = new String[]{"Economics.db","Marketing.db","index2","index3","index4","index5"};
	static SQLiteDatabase db ;
	private int[] rawDB=new int[]{R.raw.economic,R.raw.iceq,0,0,0,0};
	
    //實作Content Providers的onCreate()
    @Override
    public boolean onCreate() {
    	db=openDatabase(getContext());
    	db.close();
        return true;
    }
    public interface UserSchema {
		String TABLE_NAME = "VOCABULARY";          //Table Name
		String ID = "v_id";                    //ID
		String USER_NAME = "voc";       //User Name
		}
    //實作Content Providers的insert()
    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	db.insert(UserSchema.TABLE_NAME, null, values);
		db.close();
		return null;
	}
    //實作Content Providers的query()
    @Override
    public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
    	db.update(UserSchema.TABLE_NAME, values, selection ,null);
		db.close();
		return 0;
	}
    //實作Content Providers的delete()
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
    	db.delete(UserSchema.TABLE_NAME, selection ,null);
		db.close();
		return 0;
	}
   
    //實作Content Providers的update()
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(!db.isOpen())
    		db=openDatabase(getContext());
    	Cursor c=null;
    	if (uri.equals(Uri.parse("content://com.mis.icequeen.testprovider/getall")))
    		return getAllVoc();
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/BookSet")){
    		String[] t= uri.toString().split(":");
    		db.close();
    		db=openDatabase(getContext(),Integer.valueOf(t[2]));
    		changeDB(Integer.valueOf(t[2]));
    		return null;
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/getLastTest")){
    		String[] t= uri.toString().split(":");
    		return TestGrade(t[2]);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/Newtest")){
    		String[] t= uri.toString().split(":");
    		return InsertTest(t[2],t[3]);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/UpdateRating")){
    		String[] t= uri.toString().split(":");
    		return UpdateRating(t[2],t[3]);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/getVOCbyRC")){
    		String[] t= uri.toString().split(":");
    		System.out.println(t[2]);
    		return getVOCbyRC(t[2],t[3]);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/getVOCbyRating")){
    		String[] t= uri.toString().split(":");
    		System.out.println(t[2]);
    		return getVOCbyRating(t[2]);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/getAllChapter")){
    		return getAllCpt();
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/getVOCbyChapter")){
    		String[] t= uri.toString().split(":");
    		System.out.println(t[2]);
    		return getVOCbyChapter(t[2]);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/gbid")){
    		String[] t= uri.toString().split(":");
    		int i =Integer.valueOf(t[2]);
    		System.out.println(i);
    		return getVOCbyID(i);
    		}
    	else if (uri.toString().startsWith("content://com.mis.icequeen.testprovider/getsetbyid")){
    		String[] t= uri.toString().split(":");
    		System.out.println(t[2]);
    		return getSETbyID(t[2]);
    		}
    	  	c = db.query("VOCABULARY", projection, selection, selectionArgs, null, null, null);
    	
    	if (db != null )
    		db.close();
    	
        return c;	
    }
    
    private void changeDB(Integer rawDB) {
    	
		
	}
	private Cursor TestGrade(String cr) {
    	Cursor c = db.rawQuery("SELECT grade from test where cr_id=? order by t_id desc limit 1",new String[]{cr});
    	return c;
	}
	private Cursor InsertTest(String cr, String grade) {
    	SimpleDateFormat formatter = new SimpleDateFormat("MM-dd hh:mm");
        String now = formatter.format(new Date());
    	ContentValues cv=new ContentValues();
        cv.put("time", now);
        cv.put("cr_id", cr);
        cv.put("grade", grade);
		db.insert("TEST", null, cv);
		Cursor c = db.query("TEST", new String[] {"time","grade"}, null, null, null,null,null);
    	return c;
	}
    
    private Cursor UpdateRating(String r,String id) {
    	ContentValues cv=new ContentValues();
        cv.put("ls_id", r);
    	db.update("VOCABULARY_SET", cv, "`v_id` ='" + id +"'",null);
    	//Cursor c=db.rawQuery("UPDATE VOCABULARY_SET SET ls_id=? where v_id=?",new String[]{r,id});
    	Cursor c = db.rawQuery("SELECT a.v_id,voc,m_text,class_cht,class_eng,s_text,s_explain,a.ls_id FROM VOCABULARY v JOIN VOCABULARY_SET a on a.v_id=v.v_id JOIN CLASS c on a.c_id=c.c_id JOIN SENTENCE s on a.s_id=s.s_id JOIN MEANING m on s.s_id=m.m_id where a.v_id=?",new String[]{id});
    	return c;
	}
	private Cursor getVOCbyRC(String r, String cr) {
    	Cursor c = db.rawQuery("SELECT a.v_id,voc FROM VOCABULARY v JOIN VOCABULARY_SET a on a.v_id=v.v_id where a.ls_id=? and a.cr_id=?",new String[]{r,cr});
    	return c;
	}
	private Cursor getVOCbyRating(String rating) {
    	Cursor c = db.rawQuery("SELECT a.v_id,voc FROM VOCABULARY v JOIN VOCABULARY_SET a on a.v_id=v.v_id where a.ls_id=?",new String[]{rating});
    	return c;
    }
	private Cursor getVOCbyChapter(String cr) {
    	Cursor c = db.rawQuery("SELECT a.v_id,voc FROM VOCABULARY v JOIN VOCABULARY_SET a on a.v_id=v.v_id where a.cr_id=?",new String[]{cr});
    	return c;
	}
	private Cursor getSETbyID(String t) {
    	Cursor c = db.rawQuery("SELECT a.v_id,voc,m_text,class_cht,class_eng,s_text,s_explain,a.ls_id FROM VOCABULARY v JOIN VOCABULARY_SET a on a.v_id=v.v_id JOIN CLASS c on a.c_id=c.c_id JOIN SENTENCE s on a.s_id=s.s_id JOIN MEANING m on s.s_id=m.m_id where a.v_id=?",new String[]{t});
    	return c;
	}
    private Cursor getAllCpt() {
    	Cursor c = db.query("CHAPTER_RANGE", new String[] {"chapter_text"}, null, null, null,null,null);
    	return c;
	}
    //實作Content Providers的getType()
    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }
    //return all vocabularies in string[], start with 0
    public Cursor getAllVoc(){
    	Cursor c = db.rawQuery("SELECT a.v_id,voc,m_text,class_cht,class_eng,s_text,s_explain,a.ls_id FROM VOCABULARY v JOIN VOCABULARY_SET a on a.v_id=v.v_id JOIN CLASS c on a.c_id=c.c_id JOIN SENTENCE s on a.s_id=s.s_id JOIN MEANING m on s.s_id=m.m_id",null);
    	return c;
    }
    public Cursor getVOCbyID(int id){
    	Cursor c = db.query("VOCABULARY", new String[] {"voc"}, "v_id='"+ id +"'", null, null,null,null);
    	return c;
    
    }
    
    private void resetDB() {
    	try {
			File myDataPath = new File(TestProvider.DATABASE_PATH+TestProvider.PATH);
			System.out.println(TestProvider.DATABASE_PATH+TestProvider.PATH);
			if (myDataPath.exists()) {	
				if (myDataPath.isDirectory()) {
			        String[] children = myDataPath.list();
			        for (int i = 0; i < children.length; i++) {
			            new File(myDataPath, children[i]).delete();
			        }
			        Toast.makeText(getContext(), "資料庫已經重設", Toast.LENGTH_LONG).show();
			        // 關掉程式
			        
			    }
			}
		}
		catch (Exception e)
		{
			Log.i("DB","DB_DIR_Exception: ");
			e.printStackTrace();
		}
		
	}
    private SQLiteDatabase openDatabase(Context context, int index) {
    	try {
			File myDataPath = new File(DATABASE_PATH+PATH);
			System.out.println(DATABASE_PATH+PATH);
			String databaseFilename = myDataPath+ "/" + DATABASE_FILENAME[index];
			if (!myDataPath.exists()) {
				myDataPath.mkdirs();	
			}
			if (!(new File(databaseFilename)).exists())
			{				
				InputStream is = context.getResources().openRawResource(rawDB[index]);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFilename, null, -1);
			return database;
		}
		catch (Exception e)
		{
			Log.i("DB","DB_DIR_Exception: ");
			e.printStackTrace();
		}
		return null;
    }
    
	private SQLiteDatabase openDatabase(Context context) {
    	try {
			File myDataPath = new File(DATABASE_PATH+PATH);
			System.out.println(DATABASE_PATH+PATH);
			String databaseFilename = myDataPath+ "/" + DATABASE_FILENAME[2];
			if (!myDataPath.exists()) {
				myDataPath.mkdirs();	
			}
			if (!(new File(databaseFilename)).exists())
			{				
				InputStream is = context.getResources().openRawResource(R.raw.iceq);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFilename, null, -1);
			return database;
		}
		catch (Exception e)
		{
			Log.i("DB","DB_DIR_Exception: ");
			e.printStackTrace();
		}
		return null;
    }
}
