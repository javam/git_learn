package parivar.accounting;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements OnClickListener {

    final int COLOR_GREEN = Color.parseColor("#55336699");
    final int COLOR_RED = Color.parseColor("#559966CC");
    final String LOG_TAG = "myLogs";

    int money;
    String category = "";

    Button btnAdd, btnSub, btnShow, btnHide, btnClear;
    EditText etMoney;
    TextView tvSum;
    LinearLayout linLayout;
    Spinner spinner;

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

        etMoney = (EditText) findViewById(R.id.etMoney);

        tvSum = (TextView) findViewById(R.id.sumView);

        spinner = (Spinner) findViewById(R.id.spinner);

        linLayout = (LinearLayout) findViewById(R.id.linLayout);

        // create object of class db
        db = new DB(this);
        db.open();


        readValues();


//        ---------------------------

        String[] dataSpinner = {"","food", "transport", "fun"};
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Категория");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0: category = "без категории"; break;
                    case 1: category = "food"; break;
                    case 2: category = "transport"; break;
                    case 3: category = "fun"; break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

//TODO Create listview of table


    @Override
    public void onClick(View v) {

        // получаем данные из полей ввода
        Date d = new Date();
        SimpleDateFormat fromUser = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String date = (fromUser.format(d)).toString();

        if (etMoney.getText().toString().trim().isEmpty()) money = 0;
        else money = Integer.parseInt(etMoney.getText().toString());

        switch (v.getId()) {
            case R.id.btnAdd:
                if (money == 0) break;
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                db.addRec(date, money, 0, category);
                readValues();
                hideKeyboard();
                spinner.setSelection(0);
                break;
            case R.id.btnSub:
                if (money == 0) break;
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                db.addRec(date, 0, money, category);
                readValues();
                hideKeyboard();
                spinner.setSelection(0);
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

//        db.close();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}