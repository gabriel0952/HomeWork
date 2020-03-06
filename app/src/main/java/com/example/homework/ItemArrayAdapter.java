package com.example.homework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemArrayAdapter extends ArrayAdapter {
    private List<String[]> maskInfoList = new ArrayList<String[]>();

    static class ItemViewHolder {
        TextView pharmacyname;
        TextView pharmacyaddress;
        TextView telephone;
        TextView maskamount;
        TextView updatetime;
    }

    public ItemArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(String[] object) {
        maskInfoList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.maskInfoList.size();
    }

    @Override
    public String[] getItem(int index) {
        return this.maskInfoList.get(index+1);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.pharmacyname = (TextView) row.findViewById(R.id.pharmacy_name_textView);
            viewHolder.pharmacyaddress = (TextView) row.findViewById(R.id.pharmacy_address_textView);
            viewHolder.telephone = (TextView) row.findViewById(R.id.telephone_textView);
            viewHolder.maskamount = (TextView) row.findViewById(R.id.amount_textView);
            viewHolder.updatetime = (TextView) row.findViewById(R.id.update_time_textView);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)row.getTag();
        }

        String[] stat = getItem(position);
        viewHolder.pharmacyname.setText(stat[1]);
        viewHolder.pharmacyaddress.setText(stat[2]);
        viewHolder.telephone.setText(stat[3]);
        viewHolder.maskamount.setText(stat[4]+"/"+stat[5]);
        viewHolder.updatetime.setText(stat[6]);

        return row;
    }
}
