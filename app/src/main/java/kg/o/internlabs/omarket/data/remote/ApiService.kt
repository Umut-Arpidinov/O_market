package kg.o.internlabs.omarket.data.remote


import kg.o.internlabs.omarket.data.remote.model.RegisterDto
import kg.o.internlabs.omarket.domain.entity.RegisterEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/market-auth/register/")
    suspend fun registerUser(
        @Body reg: RegisterDto?
    ): Response<RegisterDto>

    @POST("/api/market-auth/check-otp/")
    suspend fun checkOtp(
        @Body otp: RegisterDto?
    ): Response<RegisterDto>

    @POST("api/market-auth/refresh-token/")
    suspend fun refreshToken(
        @Body reg: RegisterDto?
    ): Response<RegisterDto>

    @POST("api/market-auth/auth/msisdn-password/")
    suspend fun loginUser(
        @Body reg: RegisterDto?
    ): Response<RegisterDto>
}