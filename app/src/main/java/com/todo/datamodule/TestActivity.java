package com.todo.datamodule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @author TCG
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void dbTest(View view) {
    }

    public void httpTest(View view) {
        startActivity(new Intent(this,HttpActivity.class));
    }

    public void cacheTest(View view) {
    }
}
