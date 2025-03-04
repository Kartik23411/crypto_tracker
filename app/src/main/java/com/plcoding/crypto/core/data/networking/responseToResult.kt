package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

// maps the result returned by the api to the specific type of result and to pass to ui
// reified is used here to make the information of type of response we will get from the api is available at runtime

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, NetworkError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success<T>(response.body())
            }
            catch (e: NoTransformationFoundException) {
                Result.Error(NetworkError.SERIALIZATION)
            }
        }

        408         -> {
            Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        429         -> {
            Result.Error(NetworkError.TOO_MANY_REQUESTS)
        }

        in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
        else        -> Result.Error(NetworkError.UNKNOWN)
    }
}