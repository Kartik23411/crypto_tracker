package com.plcoding.cryptotracker.core.data.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.ANDROID
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}

//engine is a mechanism that handles the transport layer for http requests and responses
// netty engine is used for high performance and scalability needed applications, it is made to handle thousands requests at a time
// cio engine is coroutines input output engine which is for lightweight task and heavily used with coroutines to do tasks with very low resource consumption
// jetty engine is like a luxury car, used when there is a need of enterprise grade reliability it is very good for long running task
// tomcat engine, if your application needs the features or compatibility provided by the Apache Tomcat servlet container, such as servlet support. It's a great choice for traditional, Java-based applications
// windows engine is used when we are using ktor for windows applications, it is an type of
// machine specific engine