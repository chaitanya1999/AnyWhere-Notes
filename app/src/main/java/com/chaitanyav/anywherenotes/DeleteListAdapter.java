package com.chaitanyav.anywherenotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class DeleteListAdapter extends ArrayAdapter<Note> {
    ArrayList<Note> backingList;
    ArrayList<CheckBox> checkboxes = new ArrayList<>();
    Context context;
    TextView title;
    int selectedCount=0;

    class CheckBoxListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            CheckBox chk = (CheckBox) view;
            if(chk.isChecked()){
                DeleteListAdapter.this.selectedCount++;
            } else {
                DeleteListAdapter.this.selectedCount--;
            }
            title.setText(String.valueOf(selectedCount+" Notes selected"));
        }
    }

    public DeleteListAdapter(@NonNull Context context, ArrayList<Note> list) {
        super(context, R.layout.del_list_item, list);
        backingList = list;
        this.context = context;
        title=Globals.noteDeleteFragment.getView().findViewById(R.id.title);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Note data = backingList.get(position);
        CheckBox chk;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.del_list_item, parent,false);
            chk=(CheckBox) convertView.findViewById(R.id.checkBox);
            chk.setText(data.getName());
            chk.setTag(data);
            chk.setOnClickListener(new CheckBoxListener());
            checkboxes.add(chk);
        }
        return convertView;
    }




    public ArrayList<Note> getSelectedNotes() {
        ArrayList<Note> ret = new ArrayList<>();
        for(CheckBox chk: checkboxes){
            if(chk.isChecked())ret.add((Note) chk.getTag());
        }
        return ret;
    }
}
