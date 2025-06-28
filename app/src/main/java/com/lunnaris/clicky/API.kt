package com.lunnaris.clicky

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

// Main clients/configs
val client = HttpClient(OkHttp) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

val opts: IO.Options = IO.Options.builder().setForceNew(true).setReconnection(true).build()


// Data classes
@Serializable
data class TokenResponse(val token: String)

@Serializable
data class LoginRequest(val secret: String)

@Serializable
data class DeviceResponse(val deviceName: String)

@Serializable
data class CodeRequest(val code: String)

class ApiException(val title: String, message: String) : Exception(message)

class API {
    companion object {
        private var socket: Socket? = null
        private var token: String = ""

        private fun getServerAddress(): String {
            return "http://${Global.serverAddress}"
        }

        fun setToken(value: String): Companion {
            this.token = value
            return this
        }

        fun isReady(): Boolean {
            return this.token.isNotEmpty() && Global.serverAddress.isNotEmpty()
        }

        fun initSocket(): Companion {
            if (this.socket != null) {
                this.socket!!.disconnect()
            }
            val socket = IO.socket(this.getServerAddress(), opts)
            socket.on(Socket.EVENT_CONNECT) {
                Log.i("Clicky", "Connected to server")
            }
            socket.connect()
            this.socket = socket
            return this
        }

        fun quitSocket() {
            if (this.socket != null) {
                this.socket!!.disconnect()
                this.socket = null
            }
        }

        fun emit(event: String, vararg data: Any): Result<Unit> {
            try {
                this.socket!!.emit(event, token, *data)
                return Result.success(Unit)
            } catch (e: Exception) {
                return Result.failure(ApiException("WebSocket Error", "${e.message}"))
            }
        }

        suspend fun login(secret: String): Result<TokenResponse> {
            return safeRequest {
                client.post("${this.getServerAddress()}/api/control") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(secret))
                }.body()
            }
        }

        suspend fun deviceData(): Result<DeviceResponse> {
            val token = this.token
            return safeRequest {
                client.get("${this.getServerAddress()}/api/device") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                }.body()
            }
        }

        suspend fun health(): Result<Unit> {
            return safeRequest {
                client.get("${this.getServerAddress()}/api/health")
            }
        }

        suspend fun qrLogin(code: String): Result<TokenResponse> {
            return safeRequest {
                client.post("${this.getServerAddress()}/api/control/qr_code") {
                    contentType(ContentType.Application.Json)
                    setBody(CodeRequest(code))
                }.body()
            }
        }

        suspend fun quit(): Result<Unit> {
            return safeRequest {
                client.delete("${this.getServerAddress()}/api/control") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                }
                this.token = ""
            }
        }

        private suspend inline fun <reified T> safeRequest(
            crossinline block: suspend () -> T
        ): Result<T> {
            return try {
                Result.success(block())
            } catch (e: ClientRequestException) {
                val errorBody = e.response.bodyAsText()
                val json = JSONObject(errorBody)
                val title = if (json.has("error")) {
                    json["error"].toString()
                } else {
                    "Error"
                }
                val msg = if (json.has("reason")) {
                    json["reason"].toString()
                } else {
                    "Unknown error"
                }
                Result.failure(
                    ApiException(title, msg)
                )
            } catch (e: ServerResponseException) {
                val errorBody = e.response.bodyAsText()
                val json = JSONObject(errorBody)
                val title = if (json.has("error")) {
                    json["error"].toString()
                } else {
                    "Error"
                }
                val msg = if (json.has("reason")) {
                    json["reason"].toString()
                } else {
                    "Unknown error"
                }
                Result.failure(
                    ApiException(title, msg)
                )
            } catch (e: ResponseException) {
                Result.failure(ApiException("HTTP error", "${e.response.status}"))
            } catch (e: SerializationException) {
                Result.failure(ApiException("App Error", "Error deserializing the response"))
            } catch (e: IOException) {
                Result.failure(ApiException("Network Error", "${e.message}"))
            } catch (e: Exception) {
                Result.failure(ApiException("App Error", "${e.message}"))
            }
        }
    }
}