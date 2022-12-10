package kg.o.internlabs.omarket.presentation.ui.activities.activities.main_activity

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.o.internlabs.core.base.BaseViewModel
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.domain.entity.RegisterEntity
import kg.o.internlabs.omarket.domain.usecases.RefreshTokenUseCase
import kg.o.internlabs.omarket.domain.usecases.shared_prefs_use_cases.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getRefreshTokenFromPrefsUseCase: GetRefreshTokenFromPrefsUseCase,
    private val saveAccessTokenToPrefsUseCase: SaveAccessTokenToPrefsUseCase,
    private val saveRefreshTokenToPrefsUseCase: SaveRefreshTokenToPrefsUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val checkLoginStatusFromPrefsUseCase: CheckLoginStatusFromPrefsUseCase,
    private val saveLoginStatusToPrefsUseCase: SaveLoginStatusToPrefsUseCase
) :
    BaseViewModel() {

    private var myJob: Job? = null
    private var t = 0

    fun statusListener() {
        viewModelScope.launch {
            var status = false
            while (!status) {
                println("here --------")
                checkLoginStatusFromPrefs().take(1).collect {
                    if (it != null) {
                        status = it
                        if (it) {
                            myJob = startRepeatingJob()
                        }
                    }
                }
                delay(1_000)
            }
        }
    }

    private fun startRepeatingJob(timeInterval: Long = 20_000L): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(timeInterval)
                getRefreshTokenFromPrefs().collectLatest {
                    refreshTokenRoute(it).collectLatest { response ->
                        when (response) {
                            is ApiState.Success -> {
                                println("main         good")
                                response.data.refreshToken?.let { it1 ->
                                    saveRefreshTokenToPrefsUseCase.invoke(it1)
                                }
                                response.data.accessToken?.let { it1 ->
                                    saveAccessTokenToPrefsUseCase.invoke(it1)
                                }
                                myJob = startRepeatingJob(30_000) // 27 minutes
                            }
                            is ApiState.Failure -> {
                                println("main          fail")
                                myJob?.cancel()
                            }
                            ApiState.Loading -> {
                                println("main   ${t++}   loading")
                            }
                            else -> {println("main          failError")
                                myJob?.cancel()}
                        }
                    }
                }
            }
        }
    }

    fun saveLoginStatusToPrefs(isLogged: Boolean) {
        saveLoginStatusToPrefsUseCase.invoke(isLogged)
    }

    private fun checkLoginStatusFromPrefs() = checkLoginStatusFromPrefsUseCase.invoke()

    private fun refreshTokenRoute(it: String?) =
        refreshTokenUseCase(RegisterEntity(refreshToken = it))

    private fun getRefreshTokenFromPrefs() = getRefreshTokenFromPrefsUseCase.invoke()

    override fun onCleared() {
        println("onCleared ======")
        saveLoginStatusToPrefsUseCase.invoke(false)
        myJob?.cancel()
        myJob = null
        super.onCleared()
    }
}