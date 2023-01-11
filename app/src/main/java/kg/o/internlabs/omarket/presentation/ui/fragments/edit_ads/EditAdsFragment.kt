package kg.o.internlabs.omarket.presentation.ui.fragments.edit_ads

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kg.o.internlabs.core.base.BaseFragment
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.core.custom_views.cells.cells_utils.CustomWithToggleCellViewClick
import kg.o.internlabs.omarket.R
import kg.o.internlabs.omarket.databinding.FragmentEditAdsBinding
import kg.o.internlabs.omarket.domain.entity.*
import kg.o.internlabs.omarket.domain.entity.ads.ResultX
import kg.o.internlabs.omarket.presentation.ui.fragments.edit_ads.helpers.AddImageHelper
import kg.o.internlabs.omarket.presentation.ui.fragments.edit_ads.helpers.DeleteImageHelper
import kg.o.internlabs.omarket.presentation.ui.fragments.edit_ads.helpers.MainImageSelectHelper
import kg.o.internlabs.omarket.utils.checkPermission
import kg.o.internlabs.omarket.utils.getFile
import kg.o.internlabs.omarket.utils.makeToast
import kg.o.internlabs.omarket.utils.safeFlowGather
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class EditAdsFragment : BaseFragment<FragmentEditAdsBinding, EditAdsViewModel>() ,
    CustomWithToggleCellViewClick, MainImageSelectHelper, DeleteImageHelper, AddImageHelper {

    private val selectedImages = mutableListOf(UploadImageResultEntity())
    private val selected = mutableListOf(UploadImageResultEntity())
    private val selectedPath = mutableListOf(UploadImageResultEntity())
    private val args: EditAdsFragmentArgs by lazy(::initArgs)
    private var imageListAdapter: ImageListAdapter? = null
    private var adDetails = ResultX()
    private var subCategoriesEntity = SubCategoriesEntity()

    companion object {
        var mainImageIndex = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArgs()
        imageListAdapter = ImageListAdapter(this@EditAdsFragment)
    }

    private fun initArgs() = EditAdsFragmentArgs.fromBundle(requireArguments())

    override fun initViewModel() {
        super.initViewModel()
        viewModel.initViewModel()
        setField()
    }


    private fun setField() = with(binding){
        adDetails.title?.let { cusTitle.setText(it) }
        adDetails.category?.parent?.name?.let { cusCategory.setText(it) }
        adDetails.category?.name?.let { cusSubCategory.setText(it) }
        adDetails.description?.let { cusDescription.setText(it) }
        adDetails.adType?.let { cusAdType.setText(it) }
        adDetails.contractPrice?.let { cusPriceIsNegotiable.isChecked(it) }
        /*adDetails.?.let { .isChecked(it) }
        adDetails.?.let { .isChecked(it) }
        adDetails.?.let { .isChecked(it) }
        adDetails.?.let { .isChecked(it) }
        adDetails.?.let { .isChecked(it) }*/
    }

    override val viewModel: EditAdsViewModel by lazy {
        ViewModelProvider(this)[EditAdsViewModel::class.java]
    }

    override fun inflateViewBinding(inflater: LayoutInflater) =
        FragmentEditAdsBinding.inflate(inflater)

    override fun initView() = with(binding) {
        super.initView()

        with(btnCreateAd) {
            isCheckable = !isButtonClickable()
            isEnabled = isCheckable
        }

        cusDelivery.setInterface(this@EditAdsFragment)
        cusPriceIsNegotiable.setInterface(this@EditAdsFragment, 1)
        cusOMoneyAccept.setInterface(this@EditAdsFragment, 2)
        cusWhatsApp.setInterface(this@EditAdsFragment, 3)
        cusTelegram.setInterface(this@EditAdsFragment, 4)
    }

    override fun initListener() = with(binding) {
        super.initListener()

        cusCategory.setOnClickListener {
            val category = ResultEntity()
            cusCategory.setText(category.name.toString())
            cusSubCategory.isVisible = category.subCategories?.isNotEmpty() == true
        }

        cusSubCategory.setOnClickListener {
            val category = ResultEntity()
            cusCategory.setText(category.subCategories!![0].name.toString())
        }

        cusAdType.setOnClickListener {
            //cusAdType.setText()
        }

        cusLocation.setOnClickListener {
            //cusLocation.setText()
        }

        flAddImage.isVisible = selectedPath.size < 2

        ivAddImage.setOnClickListener {
            pickImages()
        }

        btnCreateAd.setOnClickListener {
            prepareValuesForAd()
        }
    }

    private fun pickImages() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(Intent.createChooser(intent, "image"))
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts
        .StartActivityForResult(), fun(result: ActivityResult) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val intent: Intent? = result.data
            if (intent?.clipData != null) {
                val count = intent.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = intent.clipData!!.getItemAt(i).uri
                    this addImage imageUri
                }
            } else if (intent?.data != null) {
                val imageUri: Uri = intent.data!!
                this addImage imageUri
            }
        }
    })

    private infix fun addImage(uri: Uri) {
        if (selectedImages.size >= 11) {
            makeToast("Нельзя загружать больше 10 изображение")
            return
        }
        uploadImage(uri)
    }

    private fun uploadImage(imageUri: Uri) {
        checkPermission()
        viewModel.uploadImage(getFile(imageUri))

        val model = UploadImageResultEntity(path = imageUri)
        selectedImages.add(1, model)
        binding.flAddImage.isVisible = false
        imageListAdapter?.initAdapter(selectedImages.toList())
        binding.rwToUploadImages.adapter = imageListAdapter

        getUploadedImage(model, imageUri)
    }

    private fun getUploadedImage(path: UploadImageResultEntity, imageUri: Uri) {
        safeFlowGather {
            viewModel.uploadImage.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        it.data.result?.let { it1 -> addIfNotContains(it1, path, imageUri) }
                    }
                    is ApiState.Failure -> {
                        println("--....1.." + it.msg.message)
                    }
                    is ApiState.Loading -> {
                    }
                }
            }
        }
    }

    private fun addIfNotContains(
        uri: UploadImageResultEntity,
        path: UploadImageResultEntity, imageUri: Uri
    ) {
        if (selected.contains(uri)) return
        if (containsModel(path)) return
        val itemIndex = selectedImages.indexOf(path)
        if (itemIndex < 0) return
        selectedPath.add(1, path)
        selected.add(1, uri)
        selectedImages[itemIndex] = UploadImageResultEntity(uri.url, path = imageUri)
        imageListAdapter?.imageLoaded(selectedImages)
    }

    private fun containsModel(m: UploadImageResultEntity) =
        selectedPath.any {
            (it === m)
        }

    private fun deleteModelFromLists(index: Int) {
        val uriOfDeletedImage = selectedImages[index].url
        selectedImages.removeAt(index)
        selected.removeAt(index)
        selectedPath.removeAt(index)

        viewModel.deleteImageFromAd(DeletedImageUrlEntity(url = uriOfDeletedImage))
    }

    override fun toggleClicked(positionOfCell: Int) = with(binding) {
        when (positionOfCell) {
            0 -> cusDelivery.isVisible = subCategoriesEntity.delivery ?: false
            1 -> {
                with(cusPriceIsNegotiable.isChecked()) {
                    cusCurrency.isVisible = this.not()
                    cusPrice.isVisible = this.not()
                }
            }
            2 -> tvOMoneyPayAgreement.isVisible = cusOMoneyAccept.isChecked()
            3 -> {
                cusWhatsAppNumber.isVisible = cusWhatsApp.isChecked()
                if (cusWhatsAppNumber.isVisible) {
                    cusWhatsAppNumber.setText(args.uuid)
                }
            }
            4 -> cusTelegramNick.isVisible = cusTelegram.isChecked()
        }
    }

    override fun deleteImage(index: Int) {
        deleteModelFromLists(index)
        selectMainImage(mainImageIndex)
    }

    override fun addImage() {
        pickImages()
    }

    override fun selectMainImage(index: Int, uri: String?) {
        mainImageIndex = index
        imageListAdapter?.selectMainImage(index)
    }

    private fun prepareValuesForAd() = with(binding) {
        val editAds = EditAds(
            adType = "",
            category = 17,
            contractPrice = true,
            delivery = true,
            description = "cusDescription",
            images = prepareUrlForAd(),
            location = 1,
            oMoneyPay = cusOMoneyAccept.isChecked(),
            promotionType = null,
            telegramProfile = if (cusTelegram.isChecked()){cusTelegramNick.getText().drop(1)} else null,
            title = cusTitle.getText(),
            whatsappNum = if (cusWhatsApp.isChecked()){cusWhatsAppNumber.getValue()} else null,
        )
        /*val editAds = EditAds(
            adType = cusAdType.getText(),
            category = cusCategory.getItemId(),
            contractPrice = cusPriceIsNegotiable.isChecked(),
            currency = if (!cusPriceIsNegotiable.isChecked()){cusCurrency.getText()} else null,
            delivery = cusDelivery.isChecked(),
            description = cusDescription.getText(),
            images = prepareUrlForAd(),
            location = cusLocation.getItemId(),
            oMoneyPay = cusOMoneyAccept.isChecked(),
            price = if (!cusPriceIsNegotiable.isChecked()){cusPrice.getText()} else null,
            promotionType = null,
            telegramProfile = if (cusTelegram.isChecked()){cusTelegramNick.getText()} else null,
            title = cusTitle.getText(),
            whatsappNum = if (cusWhatsApp.isChecked()){cusWhatsAppNumber.getValue()} else null,
        )*/
        viewModel.createAd(editAds)

        createAd()
    }

    private fun prepareUrlForAd(): List<String> {
        val mainImageModel = selectedImages.getOrNull(mainImageIndex)
        if (mainImageModel != null) {
            selectedImages.removeAt(mainImageIndex)
            selectedImages.add(1, mainImageModel)
            return getList()
        }
        return if (selectedImages.size > 1) getList() else emptyList()
    }

    private fun getList() =
        selectedImages.filter { it.url != null }.map { it.url!! }


    private fun isButtonClickable(): Boolean {
        with(binding) {
            if (cusTitle.getText().isEmpty() ||
                cusCategory.getText().isEmpty() ||
                cusDescription.getText().isEmpty() ||
                cusAdType.getText().isEmpty() ||
                cusPriceIsNegotiable.isChecked().not() &&
                (cusCurrency.getText().isEmpty() ||
                        cusPrice.getText().isEmpty()) ||
                isAllImagesHasUrl()
            ) return false
        }
        return true
    }

    private fun isAllImagesHasUrl(): Boolean {
        if (selectedImages.size > 1) return false
        return selectedImages.filterNot { selectedImages.indexOf(it) != 0 }
            .all { it.url != null }
    }

    private fun createAd() {
        safeFlowGather {
            viewModel.editedAd.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        makeToast("Объявление создано")
                        findNavController().navigate(R.id.mainFragment)
                    }
                    is ApiState.Failure -> {
                        println("--....1.." + it.msg.message)
                    }
                    is ApiState.Loading -> {
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        println("fOnDest------")
        super.onDestroy()
    }
}