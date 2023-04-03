package com.ajayvamsee.wifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements FragmentA.TextListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    @Override
    public void sendData(@NonNull String text) {
        FragmentB fragmentB = (FragmentB) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        assert fragmentB != null;
        fragmentB.updateData(text);
    }
}