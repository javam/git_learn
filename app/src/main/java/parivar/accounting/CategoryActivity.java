package parivar.accounting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = "myLogs";

    LinearLayout categoryEditLayout;

    Button btnAddCategory;

    EditText etAddCategory;

    DB db;

    AlertDialog.Builder ad;
    Context context;

    String idOfCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list_edit);

        //TODO передавать подключени к базе в метод
        db = new DB(this);
        db.open();

        categoryEditLayout = (LinearLayout) findViewById(R.id.categoryEditLayout);

        btnAddCategory = (Button) findViewById(R.id.btnCategoryAdd);
        btnAddCategory.setOnClickListener(this);

        etAddCategory = (EditText) findViewById(R.id.etAddCategory);

        clickToCategory();
        readCategories();
    }

    public void readCategories() {
        categoryEditLayout.removeAllViews();

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.getAllDataCategory();
        if (c.moveToFirst()) {
            int ColId = c.getColumnIndex(db.COLUMN_CATEGORY_ID);
            int ColCategory = c.getColumnIndex(db.COLUMN_CATEGORY_LIST);

            LayoutInflater ltInflater = getLayoutInflater();

            do {
                Log.v(LOG_TAG, " entering to do");
                final View item = ltInflater.inflate(R.layout.category_item, categoryEditLayout, false);
                TextView tvCategoryItem = (TextView) item.findViewById(R.id.tvCategoryItem);
                tvCategoryItem.setText(c.getString(ColCategory));
                final TextView tvCategoryId = (TextView) item.findViewById(R.id.tvCategoryId);
                tvCategoryId.setText(c.getString(ColId));
                //TODO использовать этот метод для отображения левого меню
                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                categoryEditLayout.addView(item, 0);
                item.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TextView tvCategoryId = (TextView) v.findViewById(R.id.tvCategoryId);
                        idOfCategory = tvCategoryId.getText().toString();
                        ad.show();
                    }
                });
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows category");
        c.close();

//TODO понять в каком месте правильно будет закрывать подключение
//        db.close();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCategoryAdd:
                if (!etAddCategory.getText().toString().trim().isEmpty()){
                    db.addCategoryItem(etAddCategory.getText().toString());
                }
                hideKeyboard();
                etAddCategory.setText("");
                readCategories();
                break;
        }
    }

    //TODO не дублировать метод, вынести его в общую логику
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public void clickToCategory() {

        context = CategoryActivity.this;

        String title = "Удаление категории";
        String message = "Удалить?";
        String button1String = "Да";
        String button2String = "Нет";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                db.delCategoryItem(idOfCategory);
                readCategories();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });
    }


}
