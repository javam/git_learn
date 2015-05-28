package parivar.accounting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {

    private static final String DB_NAME = "mydb1";
    private static final int DB_VERSION = 1;
    public static final String DB_TABLE = "mytable5";
    public static final String DB_TABLE_CATEGORY = "category_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATA = "_data";
    public static final String COLUMN_ADD = "_add";
    public static final String COLUMN_SUB = "_sub";
    public static final String COLUMN_CATEGORY = "category";

    public static final String COLUMN_CATEGORY_LIST = "category_list";
//    public static final String COLUMN_CATEGORY_ID = "category_id";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATA + " text, " +
                    COLUMN_ADD + " integer, " +
                    COLUMN_SUB + " integer, " +
                    COLUMN_CATEGORY + " text" +
                    ");";

    private static final String DB_CREATE_CATEGORY =
            "create table " + DB_TABLE_CATEGORY + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_CATEGORY_LIST + " text" +
                    ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public Cursor getAllDataCategory() {
        return mDB.query(DB_TABLE_CATEGORY, null, null, null, null, null, null);
    }

    // get rawQuery
    public Cursor getSelect(String sql) {
        return mDB.rawQuery(sql, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String txt, int add, int sub, String category) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATA, txt);
        cv.put(COLUMN_ADD, add);
        cv.put(COLUMN_SUB, sub);
        cv.put(COLUMN_CATEGORY, category);
        mDB.insert(DB_TABLE, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(int id, String tableName) {
        mDB.delete(tableName, COLUMN_ID + " = " + id, null);   }

    // del all of DB_TABLE
        public int delAllRec() {
            int clearCount = mDB.delete(DB_TABLE, null, null);
            return clearCount;
        }

//TODO объеденить эти два метода
    public int getCursor(Cursor cursor) {
        int sum;
        if (cursor.moveToFirst())
            sum = cursor.getInt(0);
        else
            sum = 0;
        cursor.close();
        return sum;
    }

    public String getCursorString(Cursor cursor) {
        String sum;
        if(cursor.moveToFirst())
            sum = cursor.getString(0);
        else
            sum = " ";
        cursor.close();
        return sum;
    }

//TODO причесать этот блок и ещё несколько снизу в один метод
    public String getBalance() {
        String sqlQuerySum1 = "SELECT SUM(_add) FROM mytable5";
        String sqlQuerySum2 = "SELECT SUM(_sub) FROM mytable5";
        Cursor cursor1 = getSelect(sqlQuerySum1);
        Cursor cursor2 = getSelect(sqlQuerySum2);
        return ""+(getCursor(cursor1) - getCursor(cursor2));
    }

    public String getCategoryById(String id) {
        String sqlQuery = "SELECT category FROM mytable5 WHERE _id = "+id;
        Cursor cursor = getSelect(sqlQuery);
        return ""+ getCursorString(cursor);
    }
    public String getAddById(String id) {
        String sqlQuery = "SELECT _add FROM mytable5 WHERE _id = "+id;
        Cursor cursor = getSelect(sqlQuery);
        return ""+ getCursor(cursor);
    }
    public String getSubById(String id) {
        String sqlQuery = "SELECT _sub FROM mytable5 WHERE _id = "+id;
        Cursor cursor = getSelect(sqlQuery);
        return ""+ getCursor(cursor);
    }

    public void updateValue(String id, String moneyAdd, String moneySub, String category) {
        String sqlQuery = "UPDATE mytable5 SET _add = " + moneyAdd
                           + ", _sub = " + moneySub
                           + ", category = " + category
                           + " WHERE _id = " + id;
        Cursor cursor = mDB.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void addCategoryItem(String categoryItem) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY_LIST, categoryItem);
        mDB.insert(DB_TABLE_CATEGORY, null, cv);
    }

//TODO в запросах везде прописать переменные d sqlQuery

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(DB_CREATE_CATEGORY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO оттестить
            db.execSQL("DROP TABLE IF EXISTS "+ DB_NAME);
            onCreate(db);
        }
    }
}