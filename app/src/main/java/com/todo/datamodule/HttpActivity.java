package com.todo.datamodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataService;

public class HttpActivity extends AppCompatActivity {

    DataService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        service = DataService.obtain();
    }

    public void request(View view) {
        EditText urlV = findViewById(R.id.url);
        String url = urlV.getText().toString();
        service.http(HttpApi.USER_TEST,Action.HTTP_GET)
                .exec(new DataCallback() {
                    @Override
                    public void onResult(int state, DataService.Clause clause, Object data) {
                        TextView view1 = findViewById(R.id.result);
                        view1.setText(String.valueOf(data));
                    }

                    @Override
                    public void onError(int state, DataService.Clause clause) {
                        super.onError(state, clause);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        service.cancel();
        service = null;
    }

    public void postRequest(View view) {
        service.http(HttpApi.USER_POST_TEST,Action.HTTP_POST)
                .where("aaa ").is("biubiubiu")
                .exec(new DataCallback() {
                    @Override
                    public void onResult(int state, DataService.Clause clause, Object data) {
                        TextView view1 = findViewById(R.id.result);
                        view1.setText(String.valueOf(data));
                    }

                    @Override
                    public void onError(int state, DataService.Clause clause) {
                        super.onError(state, clause);
                    }
                });
    }

    public void post1Request(View view) {
        service.http(HttpApi.USER_POST1_TEST,Action.HTTP_POST)
                .exec(new DataCallback() {
                    @Override
                    public void onResult(int state, DataService.Clause clause, Object data) {
                        TextView view1 = findViewById(R.id.result);
                        view1.setText(String.valueOf(data));
                    }

                    @Override
                    public void onError(int state, DataService.Clause clause) {
                        super.onError(state, clause);
                    }
                });
    }

    public void getRequest(View view) {
        service.http(HttpApi.USER_GET_TEST,Action.HTTP_GET)
                .where("path").is("    ---- aaaaaa")
                .exec(new DataCallback() {
                    @Override
                    public void onResult(int state, DataService.Clause clause, Object data) {
                        TextView view1 = findViewById(R.id.result);
                        view1.setText(String.valueOf(data));
                    }

                    @Override
                    public void onError(int state, DataService.Clause clause) {
                        super.onError(state, clause);
                    }
                });
    }

    public void postUserRequest(View view) {
        service.http(HttpApi.USER_POST_USER_TEST,Action.HTTP_POST)
                .where("userId").is("12")
                .where("userName").is("postUserRequest")
                .exec(new DataCallback() {
                    @Override
                    public void onResult(int state, DataService.Clause clause, Object data) {
                        TextView view1 = findViewById(R.id.result);
                        view1.setText(String.valueOf(data));
                    }

                    @Override
                    public void onError(int state, DataService.Clause clause) {
                        super.onError(state, clause);
                    }
                });
    }
}
