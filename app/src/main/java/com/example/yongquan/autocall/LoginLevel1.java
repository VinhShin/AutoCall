package com.example.yongquan.autocall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginLevel1 extends Activity {

    private EditText edtPass;
    private Button btnLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_level1);
        sharedPreferences = getSharedPreferences("YongQuan", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("PASS_LEVEL1", false)) {
            intentPassScreen();
        }

        edtPass = (EditText) findViewById(R.id.edt_pass);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPass.getText().toString().equals("vg@6297")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("PASS_LEVEL1",true);
                    editor.commit();
                    intentPassScreen();
                } else {
                    Toast.makeText(LoginLevel1.this, "Mật khẩu cấp 1 không hợp lễ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void intentPassScreen() {
        startActivity(new Intent(LoginLevel1.this, PassScreen.class));
        finish();
    }

}
