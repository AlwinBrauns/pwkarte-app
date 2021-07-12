package com.example.cardscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private CodeScanner codeScanner;
    private TextView tvErgebnis;
    private EditText etKey;
    private TextView tvPW;
    private final int CAMERA_REQUEST_CODE = 101;
    private final String[] head = {"ABC", "DEF", "GHI", "JKL", "MNO", "PQR", "STU", "VWX", "YZ1", "234"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission =
                 ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permission!= PackageManager.PERMISSION_GRANTED){
            makeRequest();
        }

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        tvErgebnis = findViewById(R.id.ergebnis);
        tvPW = findViewById(R.id.pw);
        etKey = findViewById(R.id.key);
        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);


        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvErgebnis.setText(result.getText());
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        etKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = etKey.getText().toString().toUpperCase();
                String[] ergebnis = tvErgebnis.getText().toString().split("\\n");

                String pw = "";
                int spalte = 0;
                int keyPos = 0;
                for(int zeile = 0; zeile < key.length(); zeile=(zeile+1), keyPos++){
                    spalte = 0;
                    while (spalte < head.length){
                        if(
                                head[spalte].charAt(0) == key.charAt(keyPos) ||
                                        head[spalte].charAt(1) == key.charAt(keyPos) ||
                                        head[spalte].charAt(2) == key.charAt(keyPos)
                        ){
                            break;
                        }else{
                            spalte++;
                        }
                    }
                    if(spalte==head.length){
                        falscherKeyMelden();
                        return;
                    }
                    pw += ergebnis[zeile%head.length].charAt((spalte+1)*3-3);
                    pw += ergebnis[zeile%head.length].charAt((spalte+1)*3-2);
                    pw += ergebnis[zeile%head.length].charAt((spalte+1)*3-1);
                }
                tvPW.setText(pw);
            }
        });

    }

    private void falscherKeyMelden(){
        Toast.makeText(this, "Falscher Schlüssel", Toast.LENGTH_LONG).show();
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,},CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Nur mit Kamera", Toast.LENGTH_LONG).show();
                } else {
                    //Success
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}