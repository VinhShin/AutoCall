package com.example.yongquan.autocall;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.yongquan.autocall.Adapter.AdapterContact;
import com.example.yongquan.autocall.Global.Global_Function;
import com.example.yongquan.autocall.Global.Gv;
import com.example.yongquan.autocall.Model.Contact;

import java.util.ArrayList;

public class AddPhone extends AppCompatActivity {

    private ListView lsContact;
    Button themContact;
    EditText edtNameContact,edtNumContact;
    SharedPreferences sharedPreferences;
    public static AdapterContact adapterContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        adapterContact = new AdapterContact(this.getApplicationContext(), Gv.listContact);
        sharedPreferences = getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
        themContact = (Button)findViewById(R.id.btnThemContact);
        edtNameContact = (EditText)findViewById(R.id.edt_name);
        edtNumContact = (EditText)findViewById(R.id.edt_phone);

        lsContact = (ListView)findViewById(R.id.listView);

        setListView();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        themContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtNameContact.getText().toString().length()>0 && edtNumContact.getText().toString().length()>0) {
                    Gv.listContact.add(new Contact(edtNameContact.getText().toString(), edtNumContact.getText().toString()));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("contact");
                    editor.putString("contact", Global_Function.converStringFromArray(Gv.listContact));
                    editor.apply();
                    setListView();
                    edtNameContact.setText("");
                    edtNumContact.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(),"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void setListView(){

        if(Gv.listContact.size()>0) {

            lsContact.setAdapter(adapterContact);
        }
    }
    public static void setListView(ArrayList<Contact> list) {
        try {
            if (list != null) {
                Gv.listContact.clear();
                Gv.listContact.addAll(list);
            }
            if (adapterContact != null) {
                adapterContact.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e("YongQuan",e.toString());
        }

    }

}
