package com.chaitanyav.anywherenotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;


public class NoteFragment extends Fragment {
    Note curNote;
    TextView noteTitle;
    EditText noteText;
    Toolbar toolbar2;
    public boolean textChanged = false;
    ShareActionProvider shareActionProvider = null;

    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        view.findViewById(R.id.scroll_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = view.findViewById(R.id.noteText);
                et.requestFocus();
                InputMethodManager lManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(et, 0);
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        curNote = ((MainActivity) getActivity()).getCurrentNote();

        init();

        initToolbar();
    }

    public void deleteNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm").setMessage("Are you sure to delete note '" + (curNote.getName() + "'?"))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Globals.dbHelper.removeNote(curNote);
                        getActivity().getSupportFragmentManager().popBackStack();
                        Globals.noteFragment = null;
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();
    }

    public void saveNote() {
        Globals.dbHelper.setNoteText(curNote, noteText.getText().toString());
        Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
        Globals.noteFragment = null;
    }

    public void init(){
        noteTitle = getActivity().findViewById(R.id.noteTitle);
        noteTitle.setText(curNote.getName());
        noteText = getActivity().findViewById(R.id.noteText);
        noteText.setText(Globals.dbHelper.getNoteText(curNote));
        noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textChanged = true;
                if(shareActionProvider!=null){
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String extraText = noteTitle.getText().toString() + "\n\n" + noteText.getText().toString();
                    shareIntent.putExtra(Intent.EXTRA_TEXT,extraText);
                    shareIntent.setType("text/plain");
                    shareActionProvider.setShareIntent(shareIntent);
                }
            }
        });
    }


    public void initToolbar(){

        toolbar2 = getView().findViewById(R.id.toolbar2);
        toolbar2.inflateMenu(R.menu.note_menu);
        toolbar2.setNavigationOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());
        toolbar2.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.ac_save_note: {
                    saveNote();
                    return true;
                }

                case R.id.ac_del_note: {
                    deleteNote();
                    return true;
                }


            }
            return false;
        });

        //set share action
        MenuItem mic = toolbar2.getMenu().findItem(R.id.ac_share);
        shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(mic);
        Intent defaultIntent = new Intent(Intent.ACTION_SEND);


        String extraText = noteTitle.getText().toString() + "\n\n" + noteText.getText().toString();
        defaultIntent.putExtra(Intent.EXTRA_TEXT,extraText);

        defaultIntent.setType("text/plain");
        shareActionProvider.setShareIntent(defaultIntent);
    }
}
