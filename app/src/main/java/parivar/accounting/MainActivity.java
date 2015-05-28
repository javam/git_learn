package parivar.accounting;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    final String LOG_TAG = "myLogs";

    Button btnAdd, btnSub, btnShow, btnClear, btnMenu, btnCategory, btnMain, btnRealBalance;
    TextView tvSum;
    LinearLayout linLayout, layoutMenu;
    RelativeLayout mainLayout;

    boolean flagHide;
    String moneyItem;

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

        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        btnCategory = (Button) findViewById(R.id.btnCategory);
        btnCategory.setOnClickListener(this);

        btnMain = (Button) findViewById(R.id.btnMain);
        btnMain.setOnClickListener(this);

        btnRealBalance = (Button) findViewById(R.id.btnRealBalance);
        btnRealBalance.setOnClickListener(this);

        tvSum = (TextView) findViewById(R.id.sumView);

        linLayout = (LinearLayout) findViewById(R.id.linLayout);
        layoutMenu = (LinearLayout) findViewById(R.id.layoutMenu);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

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
            case R.id.btnMenu:
                RelativeLayout.LayoutParams layoutMenuParams = (RelativeLayout.LayoutParams) layoutMenu.getLayoutParams();

                if (layoutMenuParams.width != 0) {
                    layoutMenuParams.width = 0;
                } else {
                    layoutMenuParams.width = (int) getResources().getDimension(R.dimen.menu_size);
                }
                layoutMenu.setLayoutParams(layoutMenuParams);
                break;
            case R.id.btnCategory:
//                mainLayout.removeAllViews();
//                LayoutInflater ltInflater = getLayoutInflater();
//                final View item = ltInflater.inflate(R.layout.category_list_edit, mainLayout, false);
//                mainLayout.addView(item);
                Intent categoryIntent = new Intent(this, CategoryActivity.class);
                startActivity(categoryIntent);
                break;
            case R.id.btnMain:
//                mainLayout.removeAllViews();
//                readValues();
                break;
            case R.id.btnRealBalance:
                Intent intentRealBalance = new Intent(this, InputBalance.class);
                startActivity(intentRealBalance);

        }

    }

    public void readValues() {
        Log.d(LOG_TAG, "--- Rows in mytable: ---");

        db = new DB(this);
        db.open();

        linLayout.removeAllViews();
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
                TextView tvMoney = (TextView) item.findViewById(R.id.tvMoney);
                TextView tvCategory = (TextView) item.findViewById(R.id.tvCategory);
                TextView tvId = (TextView) item.findViewById(R.id.tvId);

                tvDate.setText(getString(R.string.v_date) + ": " + c.getString(dateColIndex));
                if (c.getInt(addColIndex) != 0) {
                    tvMoney.setText(getString(R.string.v_add) + ": " + c.getInt(addColIndex));
                    item.setBackgroundColor(getResources().getColor(R.color.add));
                    moneyItem = "" + c.getInt(addColIndex);
                } else {
                    tvMoney.setText(getString(R.string.v_sub) + ": " + c.getInt(subColIndex));
                    item.setBackgroundColor(getResources().getColor(R.color.sub));
                    moneyItem = "" + c.getInt(subColIndex);
                }
                tvCategory.setText(getString(R.string.v_category) + ": " + c.getString(categoryColIndex));
                tvId.setText(c.getString(dateColId));
                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item, 0);
                item.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        TextView tvId = (TextView) v.findViewById(R.id.tvId);
                        Intent intentContext = new Intent(MainActivity.this, ContextMenuActivity.class);
                        intentContext.putExtra("idItem", "" + tvId.getText());
                        startActivity(intentContext);
                    }
                });
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();

        tvSum.setText(db.getBalance());

        db.close();

        flagHide = true;
    }
}