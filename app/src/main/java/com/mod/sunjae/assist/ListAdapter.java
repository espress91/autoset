package com.mod.sunjae.assist;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListAdapter {
    ListView listView = null;
    ArrayAdapter<String> adapter= null;

    public void ListSetB(Context context) {
        adapter = new ArrayAdapter<String>(context,R.layout.list_item_1);
        listView.setAdapter(adapter);
    }
    public void ListSetW(Context context) {
        adapter = new ArrayAdapter<String>(context,R.layout.simple_list_chois);
        listView.setAdapter(adapter);
    }
    public void ListSetA(Context context) {
        adapter = new ArrayAdapter<String>(context,R.layout.list_item_2);
    }
}
