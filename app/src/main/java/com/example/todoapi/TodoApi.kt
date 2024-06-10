package com.example.todoapi

import retrofit2.Response
import retrofit2.http.GET

interface TodoApi {

    @GET("/todos")
    fun getResponse():Response<List<TodoPlace>>


}