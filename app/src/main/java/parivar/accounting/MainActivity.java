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

import org.w3c.dom.Text;

public class MainActivity extends Activity implements OnClickListener {

    final int COLOR_GREEN = Color.parseColor("#55336699");
    final int COLOR_RED = Color.parseColor("#559966CC");
    final String LOG_TAG = "myLogs";

    Button btnAdd, btnSub, btnShow, btnClear;
    TextView tvSum;
    LinearLayout linLayout;

    boolean flagHide;
    String idItem, moneyItem, categotyItem;

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
                //TODO delete tybyInputBool
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
                db.delAllRec();
                readValues();
                break;
            case R.id.btnShow:
                if (!flagHide) {
                    readValues();
                } else {
                    linLayout.removeAllViews();
                    flagHide = false;
                }
                break;
        }
    }

    public void readValues() {
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        linLayout.removeAllViews();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.getAllData();
        if (c.moveToFirst()) {
            int dateColId = c.getColumnIndex(db.COLUMN_ID);
            int dateColIndex = c.getColumnIndex(db.COLUMN_DATA);
            int addColIndex = c.getColumnIndex(db.COLUMN_ADD);
            int subColIndex = c.getColumnIndex(db.COLUMN_SUB);
            int categoryColIndex = c.getColumnIndex(db.COLUMN_CATEGORY);

            LayoutInflater ltInflater = getLayoutInflater();

            do {
                final View item = ltInflater.inflate(R.layout.item, linLayout, false);
                TextView tvDate = (TextView) item.findViewById(R.id.tvDate);
                tvDate.setText(getString(R.string.v_date) + ": " + c.getString(dateColIndex));
                if (c.getInt(addColIndex) != 0) {
                    TextView tvMoney = (TextView) item.findViewById(R.id.tvMoney);
                    tvMoney.setText(getString(R.string.v_add)+ ": " + c.getInt(addColIndex));
                    item.setBackgroundColor(COLOR_GREEN);
                    moneyItem = "" + c.getInt(addColIndex);
                } else {
                    TextView tvMoney = (TextView) item.findViewById(R.id.tvMoney);
                    tvMoney.setText(getString(R.string.v_sub) + ": " + c.getInt(subColIndex));
                            item.setBackgroundColor(COLOR_RED);
                    moneyItem = "" + c.getInt(subColIndex);
                }
                TextView tvCategory = (TextView) item.findViewById(R.id.tvCategory);
                tvCategory.setText(getString(R.string.v_category)+": " + c.getString(categoryColIndex));
                TextView tvId = (TextView) item.findViewById(R.id.tvId);
                tvId.setText(c.getString(dateColId));
                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item, 0);
                item.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
//                        TextView textMoney = (TextView) v.findViewById(R.id.tvMoney);
//                        TextView textCategory = (TextView) v.findViewById(R.id.tvCategory);

                        TextView tvId = (TextView) v.findViewById(R.id.tvId);
                        Intent intentContext = new Intent(MainActivity.this, ContextMenuActivity.class);
                        intentContext.putExtra("idItem", "" + tvId.getText());

//                        intentContext.putExtra("moneyItem", "" + textMoney.getText().toString().split(" ")[1]);
//                        intentContext.putExtra("categoryItem", "" + textCategory.getText().toString().split(" ")[1]);
//                        intentContext.putExtra("typeItem", "" + textMoney.getText().toString().split(" ")[0]
//                                .substring(0, textMoney.getText().toString().split(" ")[0].length()-2));

                        startActivity(intentContext);
                    }
                });
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();

//TODO понять в каком месте правильно будет закрывать подключение
//        db.close();

        tvSum.setText(db.getBalance());
        flagHide = true;
    }
}