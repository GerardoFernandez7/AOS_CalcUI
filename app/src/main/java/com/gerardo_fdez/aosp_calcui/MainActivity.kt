package com.gerardo_fdez.aosp_calcui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Stack
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    // Componentes
    private lateinit var txtResult: TextView
    private lateinit var btnDelete: TextView
    private lateinit var btnOpen: TextView
    private lateinit var btnClose: TextView
    private lateinit var btnExponential: TextView
    private lateinit var btnPlus: TextView
    private lateinit var btnSustraction: TextView
    private lateinit var btnEqual: TextView
    private lateinit var btnMult: TextView
    private lateinit var btnDiv: TextView
    private lateinit var btn0: TextView
    private lateinit var btn1: TextView
    private lateinit var btn2: TextView
    private lateinit var btn3: TextView
    private lateinit var btn4: TextView
    private lateinit var btn5: TextView
    private lateinit var btn6: TextView
    private lateinit var btn7: TextView
    private lateinit var btn8: TextView
    private lateinit var btn9: TextView
    private lateinit var btnDot: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Obtener elementos de la UI por medio de su ID
        txtResult = findViewById(R.id.txtResult)
        btnDelete = findViewById(R.id.btnDelete)
        btnOpen = findViewById(R.id.btnOpen)
        btnClose = findViewById(R.id.btnClose)
        btnExponential = findViewById(R.id.btnExponential)
        btnPlus = findViewById(R.id.btnPlus)
        btnSustraction = findViewById(R.id.btnSustraction)
        btnEqual = findViewById(R.id.btnEqual)
        btnMult = findViewById(R.id.btnMult)
        btnDiv = findViewById(R.id.btnDiv)
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnDot = findViewById(R.id.btnDot)

        // Método que establece las acciones de los botones
        actionsButtons()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Metodo que realiza las acciones de los métodos
     */
    private fun actionsButtons(){
        // Se establecen las acciones de cada botón
        btn0.setOnClickListener{
            appendToResult("0")
        }

        btn1.setOnClickListener{
            appendToResult("1")
        }

        btn2.setOnClickListener{
            appendToResult("2")
        }

        btn3.setOnClickListener{
            appendToResult("3")
        }

        btn4.setOnClickListener{
            appendToResult("4")
        }

        btn5.setOnClickListener{
            appendToResult("5")
        }

        btn6.setOnClickListener{
            appendToResult("6")
        }

        btn7.setOnClickListener{
            appendToResult("7")
        }

        btn8.setOnClickListener{
            appendToResult("8")
        }

        btn9.setOnClickListener{
            appendToResult("9")
        }

        btnDot.setOnClickListener{
            // Verificar si ya hay un punto decimal en la última parte del número
            val currentText = txtResult.text.toString()
            if (currentText.isNotEmpty() && !currentText.last().isDigit()) {
                // No permitir un punto decimal justo después de un operador
                return@setOnClickListener
            }
            if (currentText.contains(".") && !currentText.contains(Regex("[+\\-*/()]"))) {
                // No permitir múltiples puntos decimales en la misma parte del número
                return@setOnClickListener
            }
            appendToResult(".")
        }

        btnDelete.setOnClickListener{
            txtResult.text = ""
        }

        btnOpen.setOnClickListener{
            appendToResult("(")
        }

        btnClose.setOnClickListener{
            appendToResult(")")
        }

        btnExponential.setOnClickListener{
            appendToResult("^")
        }

        btnMult.setOnClickListener{
            appendToResult("*")
        }

        btnDiv.setOnClickListener{
            appendToResult("/")
        }

        btnPlus.setOnClickListener{
            appendToResult("+")
        }

        btnSustraction.setOnClickListener{
            appendToResult("-")
        }

        btnEqual.setOnClickListener{
            val currentText = txtResult.text.toString()
            txtResult.text = evaluate(currentText)
        }
    }

    private fun appendToResult(value: String) {
        val currentText = txtResult.text.toString()
        txtResult.text = currentText + value
    }

    /**
     * Método para determinar la precedencia de un operador.
     *
     * @param c El operador cuya precedencia se desea conocer.
     * @return El nivel de precedencia del operador, o -1 si el operador no es reconocido.
     */
    private fun precedence(c: Char): Int {
        return when (c) {
            '+', '-' -> 1
            '*', '/' -> 2
            '^' -> 3
            else -> -1
        }
    }

    /**
     * Convierte una expresión infix a postfix.
     *
     * @param expresion La expresión en formato infix.
     * @return La expresión convertida en formato postfix.
     */
    private fun infixToPostfix(expresion: String): String {
        val result = StringBuilder()
        val stack = Stack<Char>()

        try {
            val cleanedExpression = expresion.replace("\\s+".toRegex(), "")

            var i = 0
            while (i < cleanedExpression.length) {
                val c = cleanedExpression[i]

                if (c.isLetterOrDigit() || c == '.') {
                    while (i < cleanedExpression.length && (cleanedExpression[i].isLetterOrDigit() || cleanedExpression[i] == '.')) {
                        result.append(cleanedExpression[i])
                        i++
                    }
                    result.append(' ')
                    i--
                } else if (c == '(') {
                    stack.push(c)
                } else if (c == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(')
                        result.append(stack.pop()).append(' ')
                    if (!stack.isEmpty() && stack.peek() != '(') {
                        return "Expresión inválida"
                    } else {
                        stack.pop()
                    }
                } else {
                    while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek()))
                        result.append(stack.pop()).append(' ')
                    stack.push(c)
                }
                i++
            }

            while (!stack.isEmpty()) {
                if (stack.peek() == '(')
                    return "Expresión inválida"
                result.append(stack.pop()).append(' ')
            }

            return result.toString().trim()
        } catch (e: Exception) {
            return "Expresión inválida"
        }
    }

    /**
     * Método para convertir de string a lista
     */
    private fun stringToList(expression: String): List<String> {
        return expression.split(" ").filter { it.isNotEmpty() }
    }

    /**
     * Método para evaluar la operación postfix
     */
    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = Stack<Double>()

        for (token in postfix) {
            when {
                token.toDoubleOrNull() != null -> stack.push(token.toDouble())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        "^" -> a.pow(b)
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    })
                }
            }
        }
        return stack.pop()
    }

    /**
     * Método para validar la expresión Infix
     */
    private fun isValidInfixExpression(expression: String): Boolean {
        // Eliminamos espacios para simplificar la validación
        val cleanedExpression = expression.replace("\\s+".toRegex(), "")

        // Expresión regular para verificar números y operadores
        val singleOperatorRegex = Regex("^[+\\-*/^]$")
        val doubleSignRegex = Regex("([+\\-*/^]{2,}|\\d+[+\\-*/^]{2,}|[+\\-*/^]{2,}\\d+)")

        // Verificamos si la expresión contiene solo un símbolo matemático
        if (singleOperatorRegex.matches(cleanedExpression)) {
            return false // Solo un símbolo matemático no es una expresión válida
        }

        // Verificamos si la expresión contiene un número con dos signos consecutivos
        if (doubleSignRegex.containsMatchIn(cleanedExpression)) {
            return false // Contiene números con dos signos consecutivos
        }

        // Si no se encontró ninguna de las condiciones inválidas, la expresión es válida
        return true
    }

    /**
     * Método para evaluar la operación Infix
     */
    private fun evaluate(expresion: String): String{
        if(!isValidInfixExpression(expresion)) return "Invalido!"
        val modifiedContent = StringBuilder()
        val postfixExpression: String = infixToPostfix(expresion)
        if(postfixExpression.equals("Expresión inválida")) return "Invalido!"
        val result = evaluatePostfix(stringToList(postfixExpression))
        modifiedContent.append(postfixExpression).append("\n")
        return result.toString()
    }
}
