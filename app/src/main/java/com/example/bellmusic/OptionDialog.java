package com.example.bellmusic;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bellmusic.Callback;
import com.example.bellmusic.R;

import java.util.Objects;

public class OptionDialog extends AppCompatDialogFragment {

    private int neg_tag = R.string.question_no;
    private int pos_tag = R.string.question_yes;
    private int title;
    private int layout;
    private Callback<Void, Void> on_neg;
    private Callback<Void, Void> on_pos;
    private Callback<View, View> on_create;

    public OptionDialog(Callback<View, View> on_create,
                        Callback<Void, Void> on_neg,
                        Callback<Void, Void> on_pos,
                        int res_title, int layout){
        this.on_create = on_create;
        this.on_neg = on_neg;
        this.on_pos = on_pos;
        this.title = res_title;
        this.layout = layout;
    }

    public OptionDialog(Callback<View, View> on_create,
                        Callback<Void, Void> on_neg,
                        Callback<Void, Void> on_pos,
                        int res_title, int layout,
                        int res_pos_tag, int res_neg_tag){
        this(on_create, on_neg, on_pos, res_title, layout);
        this.neg_tag = res_neg_tag;
        this.pos_tag = res_pos_tag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(this.layout, null);

        view = this.on_create.call(view);

        builder = builder.setView(view)
                .setTitle(this.title)
                .setPositiveButton(this.pos_tag, (d, b) -> {
                    new Thread(() -> this.on_pos.call(null)).start();
                    dismiss();
                });
        if (this.on_neg != null && this.neg_tag != -1){
            builder = builder.setNegativeButton(this.neg_tag, (d, b) -> {
                new Thread(() -> this.on_neg.call(null)).start();
                dismiss();
            });
        }
        builder = builder.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK){
                this.on_neg.call(null);
                dismiss();
                return true;
            }
            return false;
        });
        return builder.create();
    }

    public void show_dialog(AppCompatActivity parent){
        this.show(parent.getSupportFragmentManager(), "dialog");
    }
}
