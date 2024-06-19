package com.example.basiccalculator

interface OnResolveListener {
    fun onShowResult(result: Double)
    fun onShowMessage(errorRes: Int)
}