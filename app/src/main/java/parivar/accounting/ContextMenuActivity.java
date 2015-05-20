package parivar.accounting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ContextMenuActivity extends Activity implements View.OnClickListener {

    Button btnEdit, btnCopy, btnDel, btnBack;

    String idItem, moneyItem, categotyItem, typeItem;

    DB db;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.context_menu);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);

        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnCopy.setOnClickListener(this);

        btnDel = (Button) findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        db = new DB(this);
        db.open();

        Intent intent = getIntent();
        idItem = intent.getStringExtra("idItem");

        moneyItem = db.getAddById(idItem);

        if (moneyItem.equals("0")) {
            moneyItem = db.getSubById(idItem);
            typeItem = "0";
        } else {
            typeItem = "1";
        }

        categotyItem = db.getCategoryById(idItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEdit:
                Log.v(LOG_TAG, "Edit");

                Intent editIntent = new Intent(this, InputActivity.class);
                editIntent.putExtra("idItem", idItem);
                editIntent.putExtra("editItem", "edit");
                editIntent.putExtra("moneyItem", moneyItem);
                editIntent.putExtra("categoryItem", categotyItem);
                editIntent.putExtra("typeItem", typeItem);
                startActivity(editIntent);

                finish();
                break;
            case R.id.btnCopy:
                Log.v(LOG_TAG, "Send money = " + moneyItem);
                Log.v(LOG_TAG, "Send category = " + categotyItem);

                Intent copyIntent = new Intent(this, InputActivity.class);
                copyIntent.putExtra("moneyItem", moneyItem);
                copyIntent.putExtra("categoryItem", categotyItem);
                copyIntent.putExtra("typeItem", typeItem);
                startActivity(copyIntent);
                finish();
                break;
            case R.id.btnDel:
                db.delRec(Integer.parseInt(idItem), db.DB_TABLE);
                finish();
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }
}