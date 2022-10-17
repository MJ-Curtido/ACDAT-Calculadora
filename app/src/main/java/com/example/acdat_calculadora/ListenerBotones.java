package com.example.acdat_calculadora;

import android.view.View;
import android.widget.Button;

public class ListenerBotones implements View.OnClickListener {
    MainActivity calc;

    public ListenerBotones(MainActivity calc) {
        this.calc = calc;

        for (int i = 0; i < calc.getBinding().bloqueBotones.getChildCount(); i++) {
            calc.getBinding().bloqueBotones.getChildAt(i).setOnClickListener(this);
        }
    }

    public void onClick(View view) {
        Button boton = (Button) view;
        if (boton.getText().toString().matches("[0-9]")) {
            calc.anyadirNum(boton.getText().toString());
        }
        else if (boton.getText().toString().matches("[-+/*]")) {
            calc.anyadirSimbolo(boton.getText().toString());
        }
        else if (view.getId() == R.id.btnC) {
            calc.borrarPantalla();
        }
        else if (view.getId() == R.id.btnComa) {
            calc.anyadirComa();
        }
        else if (view.getId() == R.id.btnIgual) {
            calc.calcularRespuesta();
        }
        else if (view.getId() == R.id.btnMasMenos) {
            calc.cambioSigno();
        }
        else if (view.getId() == R.id.btnMC) {
            calc.borrarMemoria();
        }
        else if (view.getId() == R.id.btnMmas) {
            calc.sumarMemoria();
        }
        else if (view.getId() == R.id.btnMmenos) {
            calc.restarMemoria();
        }
        else {
            calc.guardarMemoria();
        }
    }
}