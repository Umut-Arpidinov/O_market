package kg.o.internlabs.core.base

import kg.o.internlabs.core.common.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {

    fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>?
    ): Flow<ApiState<T>> = flow {
        emit(ApiState.Loading)

        val response = apiCall()
        if (response != null) {
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(ApiState.Success(data))
                } else {
                    val error = response.errorBody()
                    if (error != null) {
                        emit(ApiState.Failure(IOException(error.toString())))
                    } else {
                        emit(ApiState.Failure(IOException("Something went wrong")))
                    }
                }
            } else {
                val raw = response.errorBody()?.string()
                val jObject = raw?.let { org.json.JSONObject(it) }
                val str: String = jObject?.get("message").toString()
                emit(ApiState.Failure(Throwable(str)))
            }
        }
    }.catch { e ->
        e.printStackTrace()
        if(e is java.net.SocketTimeoutException) {
            emit(ApiState.Failure(Exception(e)))
            return@catch
        }
        emit(ApiState.Failure(Exception("Something went wrong")))
    }.flowOn(Dispatchers.IO)
}

