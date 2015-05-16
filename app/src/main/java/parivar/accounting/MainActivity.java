package parivar.accounting;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    final int COLOR_GREEN = Color.parseColor("#55336699");
    final int COLOR_RED = Color.parseColor("#559966CC");
    final String LOG_TAG = "myLogs";

    int money;

    Button btnAdd, btnSub, btnShow, btnHide, btnClear;
    TextView tvSum;
    LinearLayout linLayout;

    DB db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnSub = (Button) findViewById(R.id.btnSub);
        btnSub.setOnClickListener(this);

        btnShow = (Button) findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);

        btnHide = (Button) findViewById(R.id.btnHide);
        btnHide.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        tvSum = (TextView) findViewById(R.id.sumView);

        linLayout = (LinearLayout) findViewById(R.id.linLayout);

        db = new DB(this);
        db.open();

        readValues();
    }

    @Override
    protected void onResume() {
        super.onResume();

        readValues();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAdd:
            Intent intentAdd = new Intent(this, InputActivity.class);
            intentAdd.putExtra("typeInput", getString(R.string.input_add));
            intentAdd.putExtra("typeInputBool", "true");
            startActivity(intentAdd);
                break;
            case R.id.btnSub:
                Intent intentSub = new Intent(this, InputActivity.class);
                intentSub.putExtra("typeInput", getString(R.string.input_sub));
                intentSub.putExtra("typeInputBool", "false");
                startActivity(intentSub);
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // удаляем все записи
                int clearCount = db.delAllRec();
                readValues();
                break;
            case R.id.btnShow:
                Log.d(LOG_TAG, "--- Show mytable: ---");
                readValues();
                break;
            case R.id.btnHide:
                Log.d(LOG_TAG, "--- Hide mytable: ---");
                linLayout.removeAllViews();
                break;
        }
    }

    public void readValues() {
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        linLayout.removeAllViews();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.getAllData();

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex(db.COLUMN_ID);
            int dateColIndex = c.getColumnIndex(db.COLUMN_DATA);
            int addColIndex = c.getColumnIndex(db.COLUMN_ADD);
            int subColIndex = c.getColumnIndex(db.COLUMN_SUB);
            int categoryColIndex = c.getColumnIndex(db.COLUMN_CATEGORY);

            LayoutInflater ltInflater = getLayoutInflater();

            do {
                View item = ltInflater.inflate(R.layout.item, linLayout, false);
                TextView tvDate = (TextView) item.findViewById(R.id.tvDate);
                tvDate.setText("Дaта: " + c.getString(dateColIndex));
                if (c.getInt(addColIndex) != 0) {
                    TextView tvMoney = (TextView) item.findViewById(R.id.tvMoney);
                    tvMoney.setText("Доход: " + c.getInt(addColIndex));
                    item.setBackgroundColor(COLOR_GREEN);
                } else {
                    TextView tvMoney = (TextView) item.findViewById(R.id.tvMoney);
                    tvMoney.setText("Расход: " + c.getInt(subColIndex));
                    item.setBackgroundColor(COLOR_RED);
                }
                TextView tvCategory = (TextView) item.findViewById(R.id.tvCategory);
                tvCategory.setText("Категория: " + c.getString(categoryColIndex));
                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item, 0);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();

        String sqlQuerySum1 = "SELECT SUM(_add) FROM mytable5";
        String sqlQuerySum2 = "SELECT SUM(_sub) FROM mytable5";
        Cursor cursor1 = db.getSelect(sqlQuerySum1);
        Cursor cursor2 = db.getSelect(sqlQuerySum2);
        int sum = db.getSum(cursor1) - db.getSum(cursor2);

        tvSum.setText("" + sum);
//TODO понять в каком месте правильно будет закрывать подключение
//        db.close();
    }

}