package com.richardluo.musicplayer.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.richardluo.musicplayer.R;

import java.util.Objects;

public class EditTextDialogBuilder extends MaterialAlertDialogBuilder {

    TextInputLayout textInputLayout;

    public EditTextDialogBuilder(@NonNull Context context) {
        super(context);
    }

    public interface OnPositiveButtonPressed {
        boolean onPositiveButtonPressed(String s);
    }

    @NonNull
    @Override
    public EditTextDialogBuilder setTitle(int titleId) {
        return (EditTextDialogBuilder) super.setTitle(titleId);
    }

    @SuppressLint("InflateParams")
    public EditTextDialogBuilder setCallBack(LayoutInflater inflater, OnPositiveButtonPressed callBack) {
        View view = inflater.inflate(R.layout.dialog_edit_text, null);
        textInputLayout = view.findViewById(R.id.textField);
        setView(view);
        setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (callBack.onPositiveButtonPressed(Objects.requireNonNull(textInputLayout.getEditText()).getText().toString()))
                dialog.dismiss();
        });
        return this;
    }

    public EditTextDialogBuilder setHint(@StringRes int hint) {
        textInputLayout.setHint(getContext().getString(hint));
        return this;
    }
}
