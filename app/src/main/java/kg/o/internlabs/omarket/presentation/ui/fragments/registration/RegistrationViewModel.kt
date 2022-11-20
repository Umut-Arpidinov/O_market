package kg.o.internlabs.omarket.presentation.ui.fragments.registration

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.o.internlabs.core.base.BaseViewModel
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.data.remote.model.RegisterDto
import kg.o.internlabs.omarket.domain.entity.RegisterEntity
import kg.o.internlabs.omarket.domain.usecases.CheckOtpUseCase
import kg.o.internlabs.omarket.domain.usecases.RegisterUserUseCase
import kg.o.internlabs.omarket.domain.usecases.shared_prefs_use_cases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val checkOtpUseCase: CheckOtpUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val putNumberPrefsUseCase: PutNumberPrefsUseCase,
    private val putRefreshTokenPrefsUseCase: PutRefreshTokenPrefsUseCase,
    private val putPasswordPrefsUseCase: PutPasswordPrefsUseCase,
    private val putAccessTokenPrefsUseCase: PutAccessTokenPrefsUseCase,
    private val setLoginStatusUseCase: SetLoginStatusUseCase
) : BaseViewModel() {

    private val _checkOtp = MutableStateFlow<ApiState<RegisterDto>>(ApiState.Loading)
    private val _registerUser = MutableStateFlow<ApiState<RegisterDto>>(ApiState.Loading)
    val checkOtp = _checkOtp.asStateFlow()
    val registerUser = _registerUser.asStateFlow()

    fun checkOtp(reg: RegisterEntity) {
        viewModelScope.launch {
            checkOtpUseCase(reg).take(1).collect {
                when (it) {
                    is ApiState.Success -> {
                        _checkOtp.value = it
                        it.data.refreshToken?.let { it1 ->
                            putRefreshTokenPrefsUseCase.invoke(it1)
                        }
                        it.data.accessToken?.let { it1 ->
                            putAccessTokenPrefsUseCase.invoke(it1)
                        }
                        setLoginStatusUseCase.invoke(true)
                    }
                    is ApiState.Failure -> {
                        setLoginStatusUseCase.invoke(false)
                        _checkOtp.value = it
                    }
                    ApiState.Loading -> {
                        setLoginStatusUseCase.invoke(false)
                    }
                }
            }
        }
    }

    fun registerUser(reg: RegisterEntity) {
        viewModelScope.launch {
            registerUserUseCase(reg).take(1).collect {
                when (it) {
                    is ApiState.Success -> _registerUser.value = it
                    is ApiState.Failure -> _registerUser.value = it
                    ApiState.Loading -> {
                    }
                }
            }
        }
    }

    fun putNumber(number: String) {
        putNumberPrefsUseCase.invoke(number)
    }

    fun putPwd(pwd: String) {
        putPasswordPrefsUseCase.invoke(pwd)
    }

    fun formattedValues(number: String): String {
        return number.filter {
            !it.isWhitespace() && it.isDigit()
        }
    }
}