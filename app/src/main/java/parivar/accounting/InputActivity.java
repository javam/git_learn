package parivar.accounting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputActivity extends Activity implements View.OnClickListener {

    TextView tvHeadInput, tvEnterCategory;
    EditText etMoney;
    Spinner spinner;
    Button btnInput;

    String category, categoryItem, moneyItem, typeItem, idItem, editItem;

    int ColId;

    boolean typeInputBool;

    final String LOG_TAG = "myLogs";

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_money);

        tvHeadInput = (TextView) findViewById(R.id.tvHeadInput);
        tvEnterCategory = (TextView) findViewById(R.id.tvEnterCategory);
        etMoney = (EditText) findViewById(R.id.etMoney);
        spinner = (Spinner) findViewById(R.id.spinner);

        btnInput = (Button) findViewById(R.id.btnInput);
        btnInput.setOnClickListener(this);
//TODO переписать на передачу одного параметра интенту
        Intent intent = getIntent();
        tvHeadInput.setText(intent.getStringExtra("typeInput"));
        moneyItem = intent.getStringExtra("moneyItem");
        categoryItem = intent.getStringExtra("categoryItem");
        typeItem = intent.getStringExtra("typeItem");
        idItem = intent.getStringExtra("idItem");
        editItem = intent.getStringExtra("editItem");

        if (moneyItem != null) {
            etMoney.setText(moneyItem);
        }

        if (editItem != null) {
            btnInput.setText(getString(R.string.edit_btn));
        }

        if (categoryItem != null) {
            makeSpinner(categoryItem);
        } else {
            makeSpinner("0");
        }

        if (typeItem != null && typeItem.equals("1")) {
            typeInputBool = true;
        } else if (typeItem != null && typeItem.equals("0")) {
            typeInputBool = false;
        } else {
            typeInputBool = Boolean.parseBoolean(intent.getStringExtra("typeInputBool"));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInput:
                db = new DB(this);
                db.open();
                if (returnMoney() == 0) break;

                if (editItem != null && typeInputBool) {
                    db.updateValue(idItem, "" + returnMoney(), "0", " \" " + category + " \" ");
                } else if (editItem != null && !typeInputBool) {
                    db.updateValue(idItem, "0", "" + returnMoney(), " \" " + category + " \" ");
                } else if (typeInputBool) {
                    db.addRec(returnDate(), returnMoney(), 0, category);
                } else {
                    db.addRec(returnDate(), 0, returnMoney(), category);
                }
                spinner.setSelection(0); //TODO попробовать убрать ???
                db.close();
                hideKeyboard();
                finish();
                break;
        }
    }

    public String returnDate() {
        // получаем данные из полей ввода
        Date d = new Date();
        SimpleDateFormat fromUser = new SimpleDateFormat("dd.MM.yyyy");
        return (fromUser.format(d)).toString();
    }

    //TODO обработать другие случаи с неправильным вводом, заблокировать кнопку ввода
    public int returnMoney() {
        int money;
        if (etMoney.getText().toString().trim().isEmpty()) money = 0;
        else money = (int) Float.parseFloat(etMoney.getText().toString());
        return money;
    }

    private void makeSpinner(String copyPos) {
        db = new DB(this);
        db.open();
        //read from db to collection
        Log.d(LOG_TAG, "make spinner");

        final Map<Integer, String> categoryMapCollection = new HashMap();

        Cursor c = db.getAllDataCategory();
        ColId = 0;
        if (c.moveToFirst()) {
            int ColCategory = c.getColumnIndex(db.COLUMN_CATEGORY_LIST);

            do {
                categoryMapCollection.put(ColId++, c.getString(ColCategory));

            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows category");
        c.close();

        final List<String> categoryList = new ArrayList<>(categoryMapCollection.values());

        if (categoryList.isEmpty()) {
            spinner.setVisibility(View.INVISIBLE);
            tvEnterCategory.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt(getString(R.string.v_category));
        // выделяем элемент
        if (!copyPos.equals("0")) {
            Log.v(LOG_TAG, "if (!copyPos.equals(\"0\")) {");
            for (Map.Entry<Integer, String> entry : categoryMapCollection.entrySet()) {
                        if(copyPos.equals(entry.getValue())) spinner.setSelection(entry.getKey());
                Log.v(LOG_TAG, "set selection "+entry.getKey());

            }

        } else {
            spinner.setSelection(0);
            Log.v(LOG_TAG, "tutu");
        }

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categoryMapCollection.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
