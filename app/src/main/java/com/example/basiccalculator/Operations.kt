package com.example.basiccalculator

class Operations {
    companion object {
        fun getOperator(operation: String): String {
            var operator = ""

            operator = if (operation.contains(Constants.OPERATOR_MULTI)) {
                Constants.OPERATOR_MULTI
            } else if (operation.contains(Constants.OPERATOR_DIV)) {
                Constants.OPERATOR_DIV
            } else if (operation.contains(Constants.OPERATOR_SUM)) {
                Constants.OPERATOR_SUM
            } else {
                Constants.OPERATOR_NULL
            }

            if (operator == Constants.OPERATOR_NULL && operation.lastIndexOf(Constants.OPERATOR_SUB) > 0) {
                operator = Constants.OPERATOR_SUB
            }

            return operator
        }

         fun canReplacedOperator(charSequence: CharSequence): Boolean {
            if (charSequence.length < 2) return false

            val lastElement = charSequence[charSequence.length - 1].toString()
            val penultimateElement = charSequence[charSequence.length - 2].toString()

            return (lastElement == Constants.OPERATOR_MULTI ||
                    lastElement == Constants.OPERATOR_SUM ||
                    lastElement == Constants.OPERATOR_DIV) &&
                    (penultimateElement == Constants.OPERATOR_MULTI ||
                            penultimateElement == Constants.OPERATOR_SUM ||
                            penultimateElement == Constants.OPERATOR_DIV ||
                            penultimateElement == Constants.OPERATOR_SUB)
        }

        fun tryResolve(operationRef: String, isFromResolve: Boolean, listener: OnResolveListener) {
            if (operationRef.isEmpty()) return

            var operation = operationRef

            if (operation.contains(Constants.POINT) && operation.lastIndexOf(Constants.POINT) == operation.length - 1) {
                operation = operation.substring(0, operation.length - 1)
            }

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

            if (values.size > 1) {
                try {
                    val numberOne = values[0]!!.toDouble()
                    val numberTwo = values[1]!!.toDouble()

                    listener.onShowResult(getResult(numberOne, operator, numberTwo))

                } catch (e: NumberFormatException) {
                    if (isFromResolve) listener.onShowMessage(R.string.verificate_your_numbers)
                }
            } else {
                if (isFromResolve && operator != Constants.OPERATOR_NULL)
                    listener.onShowMessage(R.string.message_exp_incorrect)
            }
        }

        fun getResult(numberOne: Double, operator: String, numberTwo: Double): Double {
            var result = 0.0

            when (operator) {
                Constants.OPERATOR_MULTI -> result = numberOne * numberTwo
                Constants.OPERATOR_DIV -> result = numberOne / numberTwo
                Constants.OPERATOR_SUM -> result = numberOne + numberTwo
                Constants.OPERATOR_SUB -> result = numberOne - numberTwo
            }

            return result
        }
    }
}