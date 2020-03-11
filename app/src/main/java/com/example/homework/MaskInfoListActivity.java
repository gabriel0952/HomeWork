package com.example.homework;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaskInfoListActivity extends AppCompatActivity {
    @BindView(R.id.title_textview)
    TextView textView;
    @BindView(R.id.reload_imageButton)
    ImageButton imageButton;

    private ListView listView;
    private Spinner citySpinner;
    private String[] cityArray;
    private int selectedCity = -1;
    private ItemArrayAdapter itemArrayAdapter;

    protected static final String KEY2 = "LOGOUT";

    List<String[]> maskInfoList = new ArrayList<String[]>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maskinfolist_activity);
        ButterKnife.bind(this);

        cityArray = getResources().getStringArray(R.array.city);
        citySpinner = (Spinner) findViewById(R.id.spinner);
        citySpinner.setOnItemSelectedListener(spnOnItemSelected);

        imageButton.setOnClickListener(v -> dialogAndDownload());

        dialogAndDownload();
    }

    // the selected listener for spinner
    private AdapterView.OnItemSelectedListener spnOnItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCity = (int) id;
            dialogAndDownload();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // no
        }
    };

    // initial list and adapter
    private void initialListAndAdapter() {
        maskInfoList.clear();
        listView = (ListView) findViewById(R.id.maskListView);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.list_item);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);
    }

    // setup the dialog and use the new thread to download mask information
    private void dialogAndDownload() {
        ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("請稍後");
        dialog.setMessage("取得資料中...");
        dialog.setCancelable(false);
        dialog.show();
        initialListAndAdapter();

        new Thread(() -> {
            getMaskInfo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (String[] maskData : maskInfoList) {
                        itemArrayAdapter.add(maskData);
                    }
                }
            });
            dialog.dismiss();
        }).start();
    }

    // download the mask information from URL
    private void getMaskInfo() {
        try {
            URL url = new URL("https://data.nhi.gov.tw/Datasets/Download.ashx?rid=A21030000I-D50001-001&l=https://data.nhi.gov.tw/resource/mask/maskdata.csv");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            try {
                csvDownloadAndtoString(httpURLConnection);
            } finally {
                httpURLConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // convert the csv file to string and check the city with spinner selected
    private void csvDownloadAndtoString(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                if (selectedCity >= 0 && row[2].substring(0, 3).equals(cityArray[selectedCity])) {
                    maskInfoList.add(row);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
    }

    // setup for toolbar item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                ConfirmExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // listen the back key code
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ask the exit
    private void ConfirmExit() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("離開");
        ad.setMessage("是否確定要登出");

        ad.setPositiveButton("YES", (d, w) -> {
            Intent intent = new Intent();
            intent.putExtra(KEY2, "something in return");
            setResult(RESULT_OK, intent);
            finish();
        });

        ad.setNegativeButton("NO", (d, w) -> {
        });
        ad.show();
    }
}
