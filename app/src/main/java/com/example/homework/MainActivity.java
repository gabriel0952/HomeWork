package com.example.homework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.login_button)
    Button login_button;
    @BindView(R.id.user_account_editText)
    EditText user_account_editText;
    @BindView(R.id.user_password_editText)
    EditText user_password_editText;

    private static final String FILE_NAME = "user_profile";
    private static final String KEY1 = "user_account";
    private static final String KEY2 = "user_password";

    private static final String ACCOUNT = "Ray001";
    private static final String PASSWORD = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        preCheckLogin();

        login_button.setOnClickListener(v -> loginCheckUp());
    }

    private void preCheckLogin() {
        String[] defaulterUser = readPreference();

        if (defaulterUser[0].equals(ACCOUNT) && defaulterUser[1].equals(PASSWORD)) {
            // Change Activity
            ProgressDialog dialog = ProgressDialog.show(this, "Wait", "Login...", true, false);
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                doMaskInfoListActivity(defaulterUser[0]);
            }).start();
        }
    }

    private void loginCheckUp() {
        // compare editText text value and ACCOUNT&PASSWORD
        String inputUserAccount = user_account_editText.getText().toString();
        String inputUserPassword = user_password_editText.getText().toString();

        if (inputUserAccount.equals(ACCOUNT) && inputUserPassword.equals(PASSWORD)) {
            // Store the login info and change activity
            writePreferences(inputUserAccount, inputUserPassword);
            doMaskInfoListActivity(inputUserAccount);
        } else {
            Toast.makeText(this, "Login Unsuccessfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void doMaskInfoListActivity(String u_account) {
        Intent intent = new Intent();
        intent.setClass(this, MaskInfoListActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(KEY1, u_account);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private String[] readPreference() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, 0);
        String defaultUserAccount = preferences.getString(KEY1, "");
        String defaultUserPassword = preferences.getString(KEY2, "");

        return new String[]{defaultUserAccount, defaultUserPassword};
    }

    private void writePreferences(String u_account, String u_password) {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(KEY1, u_account);
        editor.putString(KEY2, u_password);
        editor.apply();

        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
    }
}
