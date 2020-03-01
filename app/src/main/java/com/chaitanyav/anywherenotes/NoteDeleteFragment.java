package com.chaitanyav.anywherenotes;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NoteDeleteFragment extends Fragment {
    ListView lst;

    public NoteDeleteFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_note_delete, container, false);
        lst = view.findViewById(R.id.listNotes_del);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        lst.setAdapter(new DeleteListAdapter(getActivity(),Globals.dbHelper.getNoteList()));

        Toolbar toolbar3 = getView().findViewById(R.id.toolbar3);
        toolbar3.inflateMenu(R.menu.delete_multiple_menu);
        toolbar3.setTitle("Delete Notes...");
        toolbar3.setNavigationOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());
        toolbar3.setOnMenuItemClickListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.ac_delete_selected:{
                    //get selected items
                    ArrayList<Note> toDelete = ((DeleteListAdapter)lst.getAdapter()).getSelectedNotes();

                    if(toDelete.size()==0){
                        endFragment();
                        return true;
                    }
                    //otherwise


                    //show alert dialog
                    new AlertDialog.Builder(getActivity()).setTitle("Delete Notes").setMessage("Are you sure to delete "+toDelete.size()+" notes?")
                            .setPositiveButton("Yes",(DialogInterface dialogInterface, int i)->{
                                //delete all
                                for(Note note: toDelete){
                                    Globals.dbHelper.removeNote(note);
                                }
                                endFragment();
                            }).setNegativeButton("No",(DialogInterface dialogInterface, int i)->{dialogInterface.cancel();}).show();

                    return true;
                }
            }
            return false;
        });
        super.onActivityCreated(savedInstanceState);
    }

    public void endFragment(){
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
