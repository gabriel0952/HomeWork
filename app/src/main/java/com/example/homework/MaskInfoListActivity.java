package com.example.homework;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ListView;
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

    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;

    private static final String TAG = "CSVTOSTRING";

    List<String[]> maskInfoList = new ArrayList<String[]>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maskinfolist_activity);
        ButterKnife.bind(this);

        listView = (ListView)findViewById(R.id.maskListView);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.list_item);

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);

        dialogAndDownload();
    }

    private void dialogAndDownload() {
        ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Wait");
        dialog.setMessage("Downloading...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(() -> {
            getMaskInfo();
            Log.v(TAG, String.format("I am here"));

//            runOnUiThread(v->{
//                for (String[] maskData:maskInfoList) {
//                    itemArrayAdapter.add(maskData);
//                }
//            });


            dialog.dismiss();
        }).start();
    }

    private void formatListData() {
        String message = "CHECKSYRING";
        System.out.println(maskInfoList.size());
//        for (int i = 0; i < 11; i++) {
//            Log.v(TAG, String.format("[%s] string is: %d %s", message, maskInfoList.size(), maskInfoList.get(i)));
//        }
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
        String message = "CHECKSYRING";
        InputStream inputStream = httpURLConnection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();

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

//
//        for (String line; (line = reader.readLine()) != null; ) {
//            total.append(line).append('\n');
//            String[] row = line.split(",");
//            System.out.print(line);
//            Log.v(TAG, String.format("[%s] string is: %s", message, row));
//            maskInfoList.add(row);
//        }
        // System.out.print(total);
    }
}
