package com.chaitanyav.anywherenotes;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.w3c.dom.Node;

import java.util.ArrayList;

public class NoteListFragment extends Fragment {

    ListView lst = null;
    int itemIndex=-1;
    public NoteListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        lst = view.findViewById(R.id.listNotes);
        registerForContextMenu(lst);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListView();
        setupToolBar();

    }

    @Override
    public void onStart() {
        super.onStart();
        loadListViewNotes();
    }

    public void setupListView(){
        loadListViewNotes();
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewNote(i);
            }
        });
        lst.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE,R.id.ctxt_view_note,Menu.NONE, "View Note");
                contextMenu.add(Menu.NONE,R.id.ctxt_edit_name,Menu.NONE, "Edit Note Name");
                contextMenu.add(Menu.NONE,R.id.ctxt_del_note,Menu.NONE,"Delete Note");
                AdapterView.AdapterContextMenuInfo info;
                try {
                    info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                } catch (ClassCastException e) {
                    // If the menu object can't be cast, logs an error.
                    Log.e("MENU", " bad menuInfo ", e);
                    return;
                }
                itemIndex=info.position;
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.ctxt_view_note){  //View note
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    viewNote(itemIndex);
                }
            });
            return true;
        } else if(item.getItemId()==R.id.ctxt_del_note){    //delete note
            deleteNote((Note)lst.getAdapter().getItem(itemIndex));

            return true;
        } else if(item.getItemId()==R.id.ctxt_edit_name){   //edit note name
            editNoteName();
            return true;
        }
        return false;
    }

    public void loadListViewNotes(){
        ArrayList<Note> listDataSource = Globals.dbHelper.getNoteList();
        ArrayAdapter<Note> listAdapter = new ArrayAdapter<Note>(getActivity(),R.layout.list_item,listDataSource);
        lst.setAdapter(listAdapter);
    }

    public void setupToolBar(){
        Toolbar toolbar1 = getView().findViewById(R.id.toolbar1);
        toolbar1.setTitle("AnyWhere Notes");
        toolbar1.inflateMenu(R.menu.note_list_menu);
        toolbar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.ac_create_note){
                    createNote();
                    return true;
                } else if(menuItem.getItemId()==R.id.ac_del_multiple){
                    deleteMultipleNotes();
                    return true;
                }
                return false;
            }
        });
        MenuItem search_action = toolbar1.getMenu().findItem(R.id.ac_search_note);
        SearchView searchView = (SearchView)search_action.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ((ArrayAdapter<Note>)lst.getAdapter()).getFilter().filter(s);
                return false;
            }
        });
    }

    private void deleteMultipleNotes() {
        Globals.noteDeleteFragment = new NoteDeleteFragment();
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                .replace(R.id.fragmentContainer, Globals.noteDeleteFragment).addToBackStack("NoteListFragment").commit();
    }

    private void viewNote(int index){
        ((MainActivity) getActivity()).setCurrentNote((Note) lst.getAdapter().getItem(index));
        Globals.noteFragment = new NoteFragment();
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("NoteListFragment")
                .replace(R.id.fragmentContainer,Globals.noteFragment)
                .commit();
    }

    private void deleteNote(Note note){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm").setMessage("Are you sure to delete note '"+(note.getName()+"'?"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Globals.dbHelper.removeNote((Note) lst.getAdapter().getItem(itemIndex));
                        loadListViewNotes();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();
    }

    private void createNote(){

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setHint("Note name");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        int px = Globals.dpToPx(20,getActivity());
        lp.setMargins(px,0,px,0);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.addView(input);
        input.setLayoutParams(lp);
        ll.setLayoutParams(lp);
        builder.setTitle("Create a note").setMessage("Enter note name:").setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String noteName = input.getText().toString();
                if(noteName==null || noteName.trim().equals("")){
                    Toast.makeText(getActivity(),"Note name cannot be empty",Toast.LENGTH_SHORT).show();
                } else {
                    Globals.dbHelper.addNote(noteName);
                    loadListViewNotes();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setView(ll).show();
    }

    private void editNoteName(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setHint("New Name");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        int px = Globals.dpToPx(20,getActivity());
        lp.setMargins(px,0,px,0);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.addView(input);
        input.setLayoutParams(lp);
        ll.setLayoutParams(lp);
        builder.setTitle("Modify note name").setMessage("Enter new note name:").setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String noteName = input.getText().toString();
                if(noteName==null || noteName.trim().equals("")){
                    Toast.makeText(getActivity(),"Note name cannot be empty",Toast.LENGTH_SHORT).show();
                } else {
                    Globals.dbHelper.modifyNoteName((Note) lst.getAdapter().getItem(itemIndex),noteName);
                    loadListViewNotes();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setView(ll).show();
    }
}
