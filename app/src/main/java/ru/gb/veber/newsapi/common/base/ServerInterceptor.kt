package ru.gb.veber.newsapi.common.base

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ServerInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        Log.d("ServerInterceptor", "statusCode" + getResponseCode(response.code).name)

        if (getResponseCode(response.code) == ServerResponseStatusCode.SUCCESS) {
            Log.d("ServerInterceptor", "intercept() SUCCESS: response.body = ${response.body}")
        } else {
            Log.d("ServerInterceptor", "intercept() else: response.body = ${response.body}")
        }
        return response
    }

    fun getResponseCode(code: Int): ServerResponseStatusCode {
        var statusCode = ServerResponseStatusCode.UNKNOWN
        when (code) {
            200 -> statusCode = ServerResponseStatusCode.SUCCESS
            400 -> statusCode = ServerResponseStatusCode.BAD_REQUEST
            401 -> statusCode = ServerResponseStatusCode.UNAUTHORIZED
            429 -> statusCode = ServerResponseStatusCode.TOO_MANY_REQUESTS
            500 -> statusCode = ServerResponseStatusCode.INNER_SERVER_EXCEPTION
        }
        return statusCode
    }

    enum class ServerResponseStatusCode {
        SUCCESS,
        BAD_REQUEST,
        UNAUTHORIZED,
        TOO_MANY_REQUESTS,
        INNER_SERVER_EXCEPTION,
        UNKNOWN
    }

//    200 - OK. The request was executed successfully.
//    400 - Bad Request. The request was unacceptable, often due to a missing or misconfigured parameter.
//    401 - Unauthorized. Your API key was missing from the request, or wasn't correct.
//    429 - Too Many Requests. You made too many requests within a window of time and have been rate limited. Back off for a while.
//    500 - Server Error. Something went wrong on our side.
}