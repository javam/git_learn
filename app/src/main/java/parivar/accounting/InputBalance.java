package parivar.accounting;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputBalance extends Activity implements View.OnClickListener {
    Button btnRealBtnOk;
    EditText etRealBalance;
    DB db;

    int readBalanceValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_real_balance);

        btnRealBtnOk = (Button) findViewById(R.id.btnRealBtnOk);
        btnRealBtnOk.setOnClickListener(this);

        etRealBalance = (EditText) findViewById(R.id.etRealBalance);
    }

    @Override
    public void onClick(View v) {
        db = new DB(this);
        db.open();

        switch (v.getId()) {
            case R.id.btnRealBtnOk:
                if (etRealBalance.getText() != null) {
                    readBalanceValue = Integer.parseInt(etRealBalance.getText().toString());
                    int sub = readBalanceValue - Integer.parseInt(db.getBalance());
                    if (sub > 0) {
                        db.addRec(null, sub, 0, "ручной ввод баланса");
                    } else {
                        db.addRec(null, 0, -1 * sub, "ручной ввод баланса");
                    }

                }
                db.close();
                finish();
        }
    }
}
