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
    private static final String DB_TABLE = "mytable5";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATA = "_data";
    public static final String COLUMN_ADD = "_add";
    public static final String COLUMN_SUB = "_sub";
    public static final String COLUMN_CATEGORY = "category";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATA + " text, " +
                    COLUMN_ADD + " integer, " +
                    COLUMN_SUB + " integer, " +
                    COLUMN_CATEGORY + " text" +
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
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);   }

    // del all of DB_TABLE
        public int delAllRec() {
            int clearCount = mDB.delete(DB_TABLE, null, null);
            return clearCount;
        }

    public int getSum(Cursor cursor) {
        int sum;
        if(cursor.moveToFirst())
            sum = cursor.getInt(0);
        else
            sum = 0;
        cursor.close();
        return sum;
    }

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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}