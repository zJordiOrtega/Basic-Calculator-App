package com.example.basiccalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.basiccalculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun onClickButton(view: View) {
        val valueStr = (view as Button).text.toString()
        when(view.id) {
            R.id.btnDelete -> {
                val length = binding.tvOperation.text.length
                if (length > 0) {
                    val newOperation = binding.tvOperation.text.toString().substring(0, length-1)
                    binding.tvOperation.text = newOperation
                }
            }
            R.id.btnClear -> {
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            R.id.btnResolve -> {
                tryResolve(binding.tvOperation.text.toString(), true)
            }
            R.id.btnTimes,
            R.id.btnPlus,
            R.id.btnRes,
            R.id.btnDivide -> {
                tryResolve(binding.tvOperation.text.toString(), false)
                binding.tvOperation.append(valueStr)
            }
            else -> {
                binding.tvOperation.append(valueStr)
            }
        }
    }

    private fun tryResolve(operationRef: String, isFromResolve: Boolean) {

        if (operationRef.isEmpty()) return

        var operation = operationRef

        if (operation.contains(POINT) && operation.lastIndexOf(POINT) == operation.length - 1) {
            operation = operation.substring(0, operation.length - 1)
        }

        val operator = getOperator(operation)

        var values = arrayOfNulls<String>(0)
        if (operator != OPERATOR_NULL) {
            if (operator == OPERATOR_SUB) {
                val index = operation.lastIndexOf(OPERATOR_SUB)
                if (index < operation.length-1) {
                    values = arrayOfNulls(2)
                    values[0] = operation.substring(0, index)
                    values[1] = operation.substring(index + 1)
                } else {
                    values = arrayOfNulls(1)
                    values[0] = operation.substring(0, index)
                }
            } else {
                values = operation.split(operator).toTypedArray()
            }
        }

        if (values.size > 1) {
            try {
                val numberOne = values[0]!!.toDouble()
                val numberTwo = values[1]!!.toDouble()

                binding.tvResult.text = getResult(numberOne, operator, numberTwo).toString()

                if (binding.tvResult.text.isNotEmpty() && !isFromResolve) {
                    binding.tvOperation.text = binding.tvResult.text
                }
            } catch (e: NumberFormatException) {
                if (isFromResolve) showMessage()
            }
        } else {
            if (isFromResolve && operator != OPERATOR_NULL) showMessage()
        }
    }

    private fun getOperator(operation: String): String {
        var operator = ""

        operator = if (operation.contains(OPERATOR_MULTI)) {
            OPERATOR_MULTI
        } else if (operation.contains(OPERATOR_DIV)) {
            OPERATOR_DIV
        } else if (operation.contains(OPERATOR_SUM)) {
            OPERATOR_SUM
        } else {
            OPERATOR_NULL
        }

        if (operator == OPERATOR_NULL && operation.lastIndexOf(OPERATOR_SUB) > 0) {
            operator = OPERATOR_SUB
        }

        return operator
    }

    private fun getResult(numberOne: Double, operator: String, numberTwo: Double): Double {
        var result = 0.0

        when (operator) {
            OPERATOR_MULTI -> result = numberOne * numberTwo
            OPERATOR_DIV -> result = numberOne / numberTwo
            OPERATOR_SUM -> result = numberOne + numberTwo
            OPERATOR_SUB -> result = numberOne - numberTwo
        }

        return result
    }

    private fun showMessage() {
        Snackbar.make(binding.root, getString(R.string.message_exp_incorrect),
            Snackbar.LENGTH_SHORT).setAnchorView(binding.llTop).show()
    }

    companion object {
        const val OPERATOR_MULTI = "x"
        const val OPERATOR_DIV = "÷"
        const val OPERATOR_SUM = "+"
        const val OPERATOR_SUB = "-"
        const val OPERATOR_NULL = "null"
        const val POINT = "."
    }
}