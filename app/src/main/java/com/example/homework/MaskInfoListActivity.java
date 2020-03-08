package com.example.homework;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaskInfoListActivity extends AppCompatActivity {
    @BindView(R.id.title_textview)
    TextView textView;
    @BindView(R.id.reloadButton)
    Button reloadButton;

    private ListView listView;
    private Spinner citySpinner;
    private ItemArrayAdapter itemArrayAdapter;

    List<String[]> maskInfoList = new ArrayList<String[]>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maskinfolist_activity);
        ButterKnife.bind(this);

        citySpinner = (Spinner) findViewById(R.id.spinner);
        citySpinner.setOnItemSelectedListener(spnOnItemSelected);

        reloadButton.setOnClickListener(v -> dialogAndDownload());

        dialogAndDownload();
    }

    private AdapterView.OnItemSelectedListener spnOnItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("I select the " + id);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void initailListAndAdapter() {
        maskInfoList.clear();
        listView = (ListView)findViewById(R.id.maskListView);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.list_item);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);
    }

    private void dialogAndDownload() {
        ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Wait");
        dialog.setMessage("Downloading...");
        dialog.setCancelable(false);
        dialog.show();

        initailListAndAdapter();

        new Thread(() -> {
            getMaskInfo();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (String[] maskData:maskInfoList) {
                        itemArrayAdapter.add(maskData);
                    }
                }
            });
            dialog.dismiss();
        }).start();
    }

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

    private void csvDownloadAndtoString(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                maskInfoList.add(row);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        } finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }
}
