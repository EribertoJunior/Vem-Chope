package com.example.vemchope.model.interfaces

interface Callback<T> {
    fun onComplete(data: T)
}