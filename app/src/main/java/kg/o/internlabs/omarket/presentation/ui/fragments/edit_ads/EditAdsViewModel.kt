package kg.o.internlabs.omarket.presentation.ui.fragments.edit_ads

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.o.internlabs.core.base.BaseViewModel
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.domain.entity.*
import kg.o.internlabs.omarket.domain.usecases.crud_ad_usecases.*
import kg.o.internlabs.omarket.domain.usecases.detailAd_use_case.GetDetailAdUseCase
import kg.o.internlabs.omarket.domain.usecases.shared_prefs_use_cases.GetAccessTokenFromPrefsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditAdsViewModel@Inject constructor(
    private val getAccessTokenFromPrefsUseCase: GetAccessTokenFromPrefsUseCase,
    private val initiateAd: InitiateAdUseCase,
    private val uploadImageToAdUseCase: UploadImageToAdUseCase,
    private val deleteImageFromAdUseCase: DeleteImageFromAdUseCase,
    private val editAnAdUseCase: EditAnAdUseCase,
    private val deleteAdUseCase: DeleteAdUseCase,
    private val getDetailAdUseCase: GetDetailAdUseCase
) : BaseViewModel() {

    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    private val _uuid = MutableStateFlow("")
    val uuid = _uuid.asStateFlow()

    private val _detailAd = MutableSharedFlow<ApiState<DetailsAd>>()
    val detailAd = _detailAd.asSharedFlow()

    private val _uploadImage = MutableSharedFlow<ApiState<UploadImageEntity>>()
    val uploadImage = _uploadImage.asSharedFlow()

    private val _editedAd = MutableSharedFlow<ApiState<EditAds>>()
    val editedAd = _editedAd.asSharedFlow()

    private val _deleteAd = MutableSharedFlow<ApiState<DeleteEntity>>()
    val deleteAd = _deleteAd.asSharedFlow()

    private val _deleteImage = MutableSharedFlow<ApiState<DeleteEntity>>()

    init {
        getAccessTokenFromPrefs()
    }

    fun initViewModel() {}

    private fun getAccessTokenFromPrefs() {
        viewModelScope.launch {
            getAccessTokenFromPrefsUseCase().collectLatest {
                if (it != null) {
                    _token.emit(it)
                    getInitiatedAd()
                }
            }
        }
    }

    fun uploadImage(uri: File?) {
        viewModelScope.launch {
            uploadImageToAdUseCase(getAccessToken(), uri, getUuid())
                .collectLatest {
                    when (it) {
                        is ApiState.Success -> {
                            _uploadImage.emit(it)
                        }
                        is ApiState.Failure -> {
                            _uploadImage.emit(it)
                        }
                        ApiState.Loading -> {
                            _uploadImage.emit(it)
                        }
                    }
                }
        }
    }

    fun createAd(editAds: EditAds) {
        viewModelScope.launch {
            editAnAdUseCase(getAccessToken(), editAds, getUuid())
                .collectLatest {
                    when (it) {
                        is ApiState.Success -> {
                            _editedAd.emit(it)
                        }
                        is ApiState.Failure -> {
                            _editedAd.emit(it)
                        }
                        ApiState.Loading -> {
                            _editedAd.emit(it)
                        }
                    }
                }
        }
    }

    fun getDetailAd(uuid: String) {
        viewModelScope.launch {
            getDetailAdUseCase(getAccessToken(),uuid).collectLatest {
                when(it) {
                    is ApiState.Success ->{
                        _detailAd.emit(it)
                    }
                    is ApiState.Failure ->{
                        _detailAd.emit(it)
                    }
                    is ApiState.Loading ->{
                        _detailAd.emit(it)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        println("vOnDest------")
        super.onCleared()
    }

    fun deleteImageFromAd(body: DeletedImageUrlEntity) {
        viewModelScope.launch {
            deleteImageFromAdUseCase(getAccessToken(), body, getUuid()).collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        println("----Del is ok")
                        _deleteImage.emit(it)
                    }
                    is ApiState.Failure -> {
                        println("-----Del is fail    ${it.msg.message}")
                        _deleteImage.emit(it)
                    }
                    ApiState.Loading -> {
                        _deleteImage.emit(it)
                    }
                }
            }
        }
    }

    fun deleteAd() {
        viewModelScope.launch {
            deleteAdUseCase(getAccessToken(), getUuid()).collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        println("----Del is ok")
                        _deleteAd.emit(it)
                    }
                    is ApiState.Failure -> {
                        println("-----Del is fail    ${it.msg.message}")
                        _deleteAd.emit(it)
                    }
                    ApiState.Loading -> {
                        _deleteAd.emit(it)
                    }
                }
            }
        }
    }

    private fun getInitiatedAd() {
        viewModelScope.launch {
            initiateAd(getAccessToken()).collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        _uuid.value = it.data.result.toString()
                    }
                    is ApiState.Failure -> {
                        _uuid.value = ""
                    }
                    ApiState.Loading -> {
                    }
                }
            }
        }
    }

    private fun getAccessToken() = token.value

    private fun getUuid() = uuid.value
}