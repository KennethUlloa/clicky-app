package com.lunnaris.clicky

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.serialization.Serializable
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

class ApiException(val code: Int, val appCode: Int) : Exception("Api Error $code ($appCode)")

suspend fun handleHttpException(response: HttpResponse): ApiException {
    val errorBody = response.bodyAsText()
    val json = JSONObject(errorBody)
    return try {
        ApiException(json.getInt("code"), json.getInt("app_code"))
    } catch (e: Exception) {
        ApiException(response.status.value, -100)
    }
}

fun getAppErrorMessage(context: Context, code: Int) : String {
    return when(code) {
        -110 -> context.getString(R.string.error_110)
        -120 -> context.getString(R.string.error_120)
        -130 -> context.getString(R.string.error_130)
        -140 -> context.getString(R.string.error_140)
        -150 -> context.getString(R.string.error_150)
        else -> context.getString(R.string.error_100)
    }
}

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
                return Result.failure(Exception("Socket error: ${e.message}"))
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
            } catch (e: ResponseException) {
                Result.failure(handleHttpException(e.response))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}