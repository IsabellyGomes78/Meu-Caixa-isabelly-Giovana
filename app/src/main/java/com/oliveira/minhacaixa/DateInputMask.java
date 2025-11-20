package com.oliveira.minhacaixa;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class DateInputMask implements TextWatcher {

    private String current = "";
    private final EditText input;

    public DateInputMask(EditText input) {
        this.input = input;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals(current)) {
            return;
        }

        String clean = s.toString().replaceAll("[^\\d]", "");

        if (clean.length() < 8) {
            // Formata enquanto digita
            String formatted = "";
            if (clean.length() >= 1) {
                formatted += clean.substring(0, Math.min(2, clean.length()));
            }
            if (clean.length() > 2) {
                formatted += "/" + clean.substring(2, Math.min(4, clean.length()));
            }
            if (clean.length() > 4) {
                formatted += "/" + clean.substring(4, Math.min(8, clean.length()));
            }
            current = formatted;
        } else {
            // Formata quando o texto já está completo (ex: colado)
            clean = clean.substring(0, 8);
            current = clean.substring(0, 2) + "/" + clean.substring(2, 4) + "/" + clean.substring(4, 8);
        }

        input.setText(current);
        input.setSelection(current.length());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}
}
