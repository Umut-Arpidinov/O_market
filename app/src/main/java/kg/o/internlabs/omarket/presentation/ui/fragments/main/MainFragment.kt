package kg.o.internlabs.omarket.presentation.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kg.o.internlabs.core.base.BaseFragment
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.R
import kg.o.internlabs.omarket.data.remote.model.ResultX
import kg.o.internlabs.omarket.databinding.FragmentMainBinding
import kg.o.internlabs.omarket.domain.entity.ResultEntity
import kg.o.internlabs.omarket.utils.makeToast
import kg.o.internlabs.omarket.utils.safeFlowGather
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainFragmentViewModel>(),CategoryClickHandler{

    private var args: MainFragmentArgs? = null
    private var list: List<ResultEntity>? = listOf()
    private var adsList: List<ResultX>? = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = MainFragmentArgs.fromBundle(requireArguments())

    }


    override val viewModel: MainFragmentViewModel by lazy {
        ViewModelProvider(this)[MainFragmentViewModel::class.java]
    }


    override fun inflateViewBinding(inflater: LayoutInflater) =
        FragmentMainBinding.inflate(inflater)

    override fun initListener() = with(binding) {
        super.initListener()
        tbMain.setNavigationOnClickListener {
            findNavController().navigate(MainFragmentDirections.goToProfile(args?.number))
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel.getAccessTokenFromPrefs()
        viewModel.getCategories()
    }

    override fun initView() {
        super.initView()
        getCategories()
        getAds()
    }


    private fun initRecyclerViewAdapter(list: List<ResultEntity>?) {
        binding.categoryRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecycler.adapter = CategoryRecyclerViewAdapter(list, requireContext(),this)
    }

    private fun initAdsAdapter(list: MutableList<ResultX>?){
        val mainAdapter = AdsListAdapter(list, ViewGroup.LayoutParams.MATCH_PARENT, this@MainFragment)

        binding.mainViewHolder.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(
            R.dimen.item_margin_7dp)))
        binding.mainViewHolder.adapter = mainAdapter
    }


    private fun getCategories() {
        this@MainFragment.safeFlowGather {
            viewModel.categories.collectLatest {
                when (it) {
                    is ApiState.Success -> {
                        //TODO it.date.result вернет List<ResultEntity> в нем хранятся
                        //TODO it.data.result?.get(0)?.name  categoryName
                        //TODO it.data.result?.get(0)?.iconImg  icon
                        //TODO it.data.result   это для категорий все
                        list = it.data.result

                        val arr = list?.toMutableList()

                        arr?.add(
                            0,
                            ResultEntity(
                                iconImg = kg.o.internlabs.core.R.drawable.category_all_union.toString(),
                                name = "Все"
                            )
                        )
                        initRecyclerViewAdapter(arr)

                    }
                    is ApiState.Failure -> {
                        // если что то пошло ни так
                        requireActivity().makeToast(it.msg.message.toString())
                    }
                    is ApiState.Loading -> {
                        // запрос обрабатывается сервером
                    }
                }
            }
        }
    }

    override fun clickedCategory(item: ResultEntity) {
        Toast.makeText(requireActivity(), "${item.name} with id ${item.id} was clicked", Toast.LENGTH_SHORT).show()
    }

    private fun getAds() {
        this@MainFragment.safeFlowGather {
            viewModel.ads.collectLatest {
                when(it) {
                    is ApiState.Success -> {
                       Log.d("Ray"," getAds" + it.data?.result?.results.toString())
                        adsList = it.data?.result?.results
                        val arr = adsList?.toMutableList()
                        //#TODO(Something got wrong)
//                        initAdsAdapter(arr)
                    }
                    is ApiState.Failure -> {
                        // если что то пошло ни так
                        requireActivity().makeToast(it.msg.message.toString())
                    }
                    is ApiState.Loading -> {
                        // запрос обрабатывается сервером
                    }
                }
            }
        }
    }
}