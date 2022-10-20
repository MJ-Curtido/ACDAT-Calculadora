package com.example.acdat_calculadora;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.acdat_calculadora.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Double memoria;
    private String ultimoNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        new ListenerBotones(this);

        this.memoria = 0.0;
        this.ultimoNum = "";

        binding.btnIgual.setEnabled(false);
        binding.btnMasMenos.setEnabled(false);
        binding.btnMR.setEnabled(false);
        alternarSimbolos(false);
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public void alternarSimbolos(Boolean estado) {
        if (estado) {
            binding.btnMas.setEnabled(true);
            binding.btnMenos.setEnabled(true);
            binding.btnDiv.setEnabled(true);
            binding.btnMult.setEnabled(true);
        }
        else {
            binding.btnMas.setEnabled(false);
            binding.btnMenos.setEnabled(false);
            binding.btnDiv.setEnabled(false);
            binding.btnMult.setEnabled(false);
        }
    }

    public void anyadirNum(String num) {
        binding.btnMmenos.setEnabled(true);
        binding.btnMmas.setEnabled(true);
        binding.btnIgual.setEnabled(true);
        binding.btnMR.setEnabled(true);
        alternarSimbolos(true);

        if (!binding.txtResultado.getText().toString().equals("")) {
            binding.txtCalculadora.setText(num);
            this.ultimoNum = num;
        }
        else {
            this.ultimoNum += num;
            binding.txtCalculadora.append(num);
        }

        binding.txtResultado.setText("");
    }

    public void anyadirSimbolo(String simbolo) {
        binding.btnMmenos.setEnabled(false);
        binding.btnMmas.setEnabled(false);
        binding.btnIgual.setEnabled(false);
        binding.btnMR.setEnabled(false);
        alternarSimbolos(false);
        binding.btnComa.setEnabled(true);

        this.ultimoNum = "";

        if (binding.txtResultado.getText().toString() != "") {
            binding.txtCalculadora.setText(binding.txtResultado.getText().toString() + simbolo);
        }
        else {
            binding.txtCalculadora.append(simbolo);
        }

        if (simbolo.equals("+") || simbolo.equals("-")) {
            binding.btnMasMenos.setEnabled(true);
        }
        else {
            binding.btnMasMenos.setEnabled(false);
        }

        binding.txtResultado.setText("");
    }

    public void borrarPantalla() {
        binding.txtCalculadora.setText("");
        binding.txtResultado.setText("");

        binding.btnMR.setEnabled(false);

        alternarSimbolos(false);
    }

    public void anyadirComa() {
        binding.btnIgual.setEnabled(false);
        binding.btnMR.setEnabled(false);
        binding.btnComa.setEnabled(false);

        binding.txtCalculadora.append(".");
    }

    public void calcularRespuesta() {
        binding.txtResultado.setText(eval(binding.txtCalculadora.getText().toString()) + "");

        if (binding.txtResultado.getText().toString().contains("Infinity")) {
            alternarSimbolos(false);
        }
    }

    public void cambioSigno() {
        char signo = binding.txtCalculadora.getText().toString().charAt((binding.txtCalculadora.getText().toString().length() - ultimoNum.length() - 1));

        if (signo == '+') {
            binding.txtCalculadora.setText(binding.txtCalculadora.getText().toString().substring(0, binding.txtCalculadora.getText().toString().length() - ultimoNum.length() - 1) + "-" + ultimoNum);
        }
        else {
            binding.txtCalculadora.setText(binding.txtCalculadora.getText().toString().substring(0, binding.txtCalculadora.getText().toString().length() - ultimoNum.length() - 1) + "+" + ultimoNum);
        }
    }

    public void borrarMemoria() {
        this.memoria = 0.0;
    }

    public void sumarMemoria() {
        binding.txtCalculadora.append("+" + this.memoria);
    }

    public void restarMemoria() {
        binding.txtCalculadora.append("-" + this.memoria);
    }

    public void guardarMemoria() {
        calcularRespuesta();
        this.memoria = Double.parseDouble(binding.txtResultado.getText().toString());
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')'))
                            throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}