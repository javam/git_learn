package parivar.accounting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InputActivity extends Activity implements View.OnClickListener {

    TextView tvHeadInput;
    EditText etMoney;
    Spinner spinner;
    Button btnInput;

    String category;
    String[] dataSpinner;

    boolean typeInputBool;

    final String LOG_TAG = "myLogs";

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_money);

        tvHeadInput = (TextView) findViewById(R.id.tvHeadInput);
        etMoney = (EditText) findViewById(R.id.etMoney);
        spinner = (Spinner) findViewById(R.id.spinner);

        btnInput = (Button) findViewById(R.id.btnInput);
        btnInput.setOnClickListener(this);

        Intent intent = getIntent();
        tvHeadInput.setText(intent.getStringExtra("typeInput"));
        typeInputBool = Boolean.parseBoolean(intent.getStringExtra("typeInputBool"));
        Log.v(LOG_TAG,intent.getStringExtra("typeInputBool"));
        makeSpinner();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInput:
                db = new DB(this);
                db.open();
                if (returnMoney() == 0) break;
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                if(typeInputBool) {
                    db.addRec(returnDate(), returnMoney(), 0, category);
                }
                else {
                    db.addRec(returnDate(), 0, returnMoney(), category);
                }
                hideKeyboard();
                spinner.setSelection(0);
                db.close();
                finish();
                break;
        }
    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public String returnDate() {
        // получаем данные из полей ввода
        Date d = new Date();
        SimpleDateFormat fromUser = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return (fromUser.format(d)).toString();
    }
//TODO обработать другие случаи с неправильным вводом, заблокировать кнопку ввода
    public int returnMoney() {
        int money;
        if (etMoney.getText().toString().trim().isEmpty()) money = 0;
        else money = Integer.parseInt(etMoney.getText().toString());
        return money;
    }

    private void makeSpinner() {

        // localisation
        dataSpinner = new String[6];
        dataSpinner[0] = getString(R.string.categoty_whithout);
        dataSpinner[1] = getString(R.string.categoty_flat);
        dataSpinner[2] = getString(R.string.categoty_transport);
        dataSpinner[3] = getString(R.string.categoty_fun);
        dataSpinner[4] = getString(R.string.categoty_clothes);
        dataSpinner[5] = getString(R.string.categoty_food);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt(getString(R.string.v_category));
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        category = dataSpinner[0];
                        break;
                    case 1:
                        category = dataSpinner[1];
                        break;
                    case 2:
                        category = dataSpinner[2];
                        break;
                    case 3:
                        category = dataSpinner[3];
                        break;
                    case 4:
                        category = dataSpinner[4];
                        break;
                    case 5:
                        category = dataSpinner[5];
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}
