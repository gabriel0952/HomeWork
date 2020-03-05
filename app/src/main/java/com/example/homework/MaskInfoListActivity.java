package com.example.homework;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaskInfoListActivity extends AppCompatActivity {
    @BindView(R.id.title_textview)
    TextView textView;

    ArrayList maskInfoList = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maskinfolist_activity);
        ButterKnife.bind(this);
        dialogAndDownload();
    }

    private void dialogAndDownload() {
        ProgressDialog dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Wait");
        dialog.setMessage("Downloading...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(()->{
            getMaskInfo();
            formatListData();
            dialog.dismiss();
        }).start();
    }

    private void formatListData() {

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
        StringBuilder total = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            total.append(line).append('\n');
            String[] row = line.split(",");
            maskInfoList.add(row);
        }
        System.out.print(total);
    }
}
