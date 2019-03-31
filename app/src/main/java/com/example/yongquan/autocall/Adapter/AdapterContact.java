package com.example.yongquan.autocall.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.yongquan.autocall.Global.Global_Function;
import com.example.yongquan.autocall.Global.Gv;
import com.example.yongquan.autocall.Model.Contact;
import com.example.yongquan.autocall.R;

import java.util.ArrayList;

/**
 * Created by DELL on 7/29/2018.
 */
public class AdapterContact extends ArrayAdapter<Contact> {

    private Context context;
    private ArrayList<Contact> lstData;

    public AdapterContact(Context context, ArrayList<Contact> objects) {
        super(context, R.layout.item_row, objects);
        this.context = context;
        this.lstData = objects;
    }

    @Override
    public int getCount() {
        return lstData.size();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Contact contactModel = lstData.get(position);
        TextView tvContactPhone, tvContactName;
        Button btnDelete;
        ViewContactHelper vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
            vh = new ViewContactHelper(convertView);
            convertView.setTag(vh);
        }

        vh = (ViewContactHelper) convertView.getTag();
        tvContactPhone = vh.getTvContactPhone();
        tvContactName = vh.getTvContactName();
        btnDelete = vh.getBtnDelete();
        tvContactName.setText(contactModel.getName());
        tvContactPhone.setText(contactModel.getPhone());
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gv.listContact.remove(position);
                notifyDataSetChanged();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("contact");
                editor.putString("contact", Global_Function.converStringFromArray(Gv.listContact));
                editor.apply();
            }
        });
        return convertView;
    }
    public class ViewContactHelper {
        private View v;
        private TextView tvContactPhone;
        private TextView tvContactName;
        private Button btnDelete;
        public ViewContactHelper(View v) {
            this.v = v;
        }

        public TextView getTvContactPhone() {
            if (tvContactPhone == null)
                tvContactPhone = (TextView) v.findViewById(R.id.tvContactName);
            return tvContactPhone;
        }

        public TextView getTvContactName() {
            if (tvContactName == null)
                tvContactName = (TextView) v.findViewById(R.id.tvContactPhone);
            return tvContactName;
        }
        public Button getBtnDelete() {
            if (btnDelete == null)
                btnDelete = (Button) v.findViewById(R.id.btn_delete);
            return btnDelete;
        }
    }
}
