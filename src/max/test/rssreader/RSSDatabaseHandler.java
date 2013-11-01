package max.test.rssreader;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RSSDatabaseHandler extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "rssreader.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "rss_table";
	private static final String UID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PUBDATE = "pubDate";
	
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_TITLE + " TEXT," + KEY_LINK + " TEXT," + KEY_DESCRIPTION + " TEXT,"
			+ KEY_PUBDATE + " TEXT);";
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;
	
	public RSSDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);		
	}
	
	// Добавить строчку с новостью
    public void addFeed(RssItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();  
        values.put(KEY_TITLE, item.getTitle()); 
        values.put(KEY_LINK, item.getLink()); 
        values.put(KEY_PUBDATE, item.getPubDate()); 
        values.put(KEY_DESCRIPTION, item.getDescription());
      //  Log.d("LOG_TAG", "--- KEY_DESCRIPTION database ---" + item.getDescription());
        // проверяем существование строчки
        if (!isSiteExists(db, item.getLink())) {
        	db.insert(TABLE_NAME, null, values);
        	db.close();
        } else {
        	//Log.d("LOG_TAG", "--- Feed is !!!!! ---");
        
        	/*updateFeed(item);
            db.close();*/
        }
        // Log.d("LOG_TAG", "--- addFeed database ---");
    }
    /**
     * Обновить одну строчку
     * */
    public int updateFeed(RssItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle()); 
        values.put(KEY_LINK, item.getLink()); 
        values.put(KEY_PUBDATE, item.getPubDate());
        values.put(KEY_DESCRIPTION, item.getDescription());
 
        int update = db.update(TABLE_NAME, values, KEY_PUBDATE + " = ?",
                new String[] { String.valueOf(item.getPubDate()) });
        db.close();
        //Log.d("TAG!!", "-------------updateFeed");
        return update;
 
    }
    
    /**
	 * Чиатем все строки
	 * */
	public ArrayList<RssItem> getAllRSS() {
		ArrayList<RssItem> rssList = new ArrayList<RssItem>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME
				+ " ORDER BY _id DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				RssItem item = new RssItem();
				item.setId(Integer.parseInt(cursor.getString(0)));
				item.setTitle(cursor.getString(1));
				item.setLink(cursor.getString(2));				
				item.setDescription(cursor.getString(3));
				item.setPubDate(cursor.getString(4));
				// Adding contact to list
				rssList.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		return rssList;
	}
	
	/*Геттер строки*/
	public RssItem getItem(int id){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, new String[] { UID, KEY_TITLE,
				KEY_LINK, KEY_DESCRIPTION, KEY_PUBDATE }, UID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		RssItem item = new RssItem(cursor.getString(1), cursor.getString(2),
				cursor.getString(3), cursor.getString(4));

		item.setId(Integer.parseInt(cursor.getString(0)));
		item.setTitle(cursor.getString(1));
		item.setLink(cursor.getString(2));
		item.setDescription(cursor.getString(3));
		item.setPubDate(cursor.getString(4));
		cursor.close();
		db.close();
		return item;
		
	}
    
	/*Роверяет существование строки*/
    public boolean isSiteExists(SQLiteDatabase db, String link) {
    	 
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME
                + " WHERE link = '"+ link + "'", new String[] {});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }
    
	/*Показать все поля*/
    public void showFields(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String query = "SELECT " + UID + ", "
    			+ KEY_TITLE + " FROM " + TABLE_NAME;
    	Cursor cursor2 = db.rawQuery(query, null);
    	while (cursor2.moveToNext()) {
    		int id = cursor2.getInt(cursor2
    				.getColumnIndex(UID));
    		String name = cursor2.getString(cursor2
    				.getColumnIndex(KEY_TITLE));
    		Log.d("LOG_TAG", "ROW " + id + " HAS NAME " + name);
    	}
    	cursor2.close();
    	
    }
    
    /**
     * Удалятор
     * */
    public void deleteItem(RssItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_TITLE + " = ?",
                new String[] { String.valueOf(item.getTitle())});
        db.close(); 
       // Log.d("TAG!!", "-------------DELETED");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w("LOG_TAG", "Обновление базы данных с версии " + oldVersion
				+ " до версии " + newVersion + ", которое удалит все старые данные");
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

}
