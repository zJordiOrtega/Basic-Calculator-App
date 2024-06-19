package com.example.basiccalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.basiccalculator.Operations.Companion.getResult
import com.example.basiccalculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvOperation.run {
            addTextChangedListener { charSequence ->
                if (Operations.canReplacedOperator(charSequence.toString())) {
                    val newOperation = "${text.substring(0, text.length - 2)}${
                        text.substring(text.length - 1)
                    }"
                    text = newOperation
                }
            }
        }
    }

    fun onClickButton(view: View) {
        val valueStr = (view as Button).text.toString()
        val operation = binding.tvOperation.text.toString()

        when(view.id) {
            R.id.btnDelete -> {

                binding.tvOperation.run {
                    if (text.length > 0) text = operation.substring(0, text.length-1)
                }
            }
            R.id.btnClear -> {
                binding.tvOperation.text = ""
                binding.tvResult.text = ""
            }
            R.id.btnResolve -> checkOrResolve(operation, true)

            R.id.btnTimes,
            R.id.btnPlus,
            R.id.btnRes,
            R.id.btnDivide -> {
                checkOrResolve(operation, false)

                val operator = valueStr
                addOperator(operator, operation)
            }
            R.id.btnPoint -> addPoint(valueStr, operation)

            else -> binding.tvOperation.append(valueStr)
        }
    }

    private fun addPoint(pointStr: String, operation: String) {
        if (!operation.contains(Constants.POINT)) {
            binding.tvOperation.append(pointStr)
        } else {
            val operator = Operations.getOperator(operation)

            var values = arrayOfNulls<String>(0)
            if (operator != Constants.OPERATOR_NULL) {
                if (operator == Constants.OPERATOR_SUB) {
                    val index = operation.lastIndexOf(Constants.OPERATOR_SUB)
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
            if (values.size > 0) {
                val numberOne = values [0]!!
                if (values.size > 1) {
                    val numberTwo = values[1]!!
                    if (numberOne.contains(Constants.POINT) && !numberTwo.contains(Constants.POINT)) {
                        binding.tvOperation.append(pointStr)
                    }
                } else {
                    if (numberOne.contains(Constants.POINT)) {
                        binding.tvOperation.append(pointStr)
                    }
                }
            }
        }
    }

    private fun addOperator(operator: String, operation: String) {
        val lastElement = if (operation.isEmpty()) ""
        else operation.substring(operation.length - 1)

        if (operator == Constants.OPERATOR_SUB) {
            if (operation.isEmpty() || lastElement != Constants.OPERATOR_SUB && lastElement != Constants.POINT) {
                binding.tvOperation.append(operator)
            }
        } else {
            if (operation.isNotEmpty() && lastElement != Constants.POINT) {
                binding.tvOperation.append(operator)
            }
        }
    }

    private fun checkOrResolve(operation: String, isFromResolve: Boolean) {
        Operations.tryResolve(operation, isFromResolve, object : OnResolveListener {
            override fun onShowMessage(errorRes: Int) {
                showMessage()
            }

            override fun onShowResult(result: Double) {
                binding.tvResult.text = result.toString()

                if (binding.tvResult.text.isNotEmpty() && !isFromResolve) {
                    binding.tvOperation.text = binding.tvResult.text
                }
            }
        })
    }

    private fun showMessage() {
        Snackbar.make(binding.root, getString(R.string.message_exp_incorrect),
            Snackbar.LENGTH_SHORT).setAnchorView(binding.llTop).show()
    }
}