package kg.o.internlabs.omarket.presentation.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kg.o.internlabs.core.base.BaseFragment
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.databinding.FragmentMainBinding
import kg.o.internlabs.omarket.domain.entity.ResultEntity
import kg.o.internlabs.omarket.domain.entity.ads.ResultX
import kg.o.internlabs.omarket.presentation.ui.fragments.main.adapter.AdClickedInMain
import kg.o.internlabs.omarket.presentation.ui.fragments.main.adapter.PagerAdapterForMain
import kg.o.internlabs.omarket.utils.makeToast
import kg.o.internlabs.omarket.utils.safeFlowGather
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainFragmentViewModel>(),
    CategoryClickHandler, AdClickedInMain {

    private var args: MainFragmentArgs? = null
    private var adapter = PagerAdapterForMain()
    private var list: List<ResultEntity>? = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = MainFragmentArgs.fromBundle(requireArguments())
    }

    override val viewModel: MainFragmentViewModel by lazy {
        ViewModelProvider(this)[MainFragmentViewModel::class.java]
    }


    override fun inflateViewBinding(inflater: LayoutInflater) =
        FragmentMainBinding.inflate(inflater)

    override fun initView() {
        super.initView()
        adapter.setInterface(this@MainFragment, this)
        getCategories()
        getAds()
        initAdapter()
    }

    override fun initListener() = with(binding) {
        super.initListener()
        tbMain.setNavigationOnClickListener {
            findNavController().navigate(MainFragmentDirections.goToProfile(args?.number))
        }
    }

    private fun initAdapter() = with(binding){
        recMain.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading)
                progressBar.isVisible = true
            else {
                progressBar.isVisible = false
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error ->  loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(requireActivity(), it.error.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initRecyclerViewAdapter(list: List<ResultEntity>?) {
        binding.categoryRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecycler.adapter = CategoryRecyclerViewAdapter(list, requireContext(), this)
    }

    private fun getCategories() {
        safeFlowGather {
            viewModel.categories.collectLatest {
                when (it) {
                    is ApiState.Success -> {
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
                        makeToast(it.msg.message.toString())
                    }
                    is ApiState.Loading -> {
                    }
                }
            }
        }
    }

    private fun getAds() {
        safeFlowGather {
            viewModel.ads.collectLatest {
                adapter.submitData(it)
                /*when (it) {
                    is ApiState.Success -> {
                        //

                        Log.d("Ray", "AAAAAAAAAAAAAA" + it.data?.result?.results.toString())

                        val mainAdapter =
                            AdsListAdapter(
                                it.data?.result?.results!!,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                this@MainFragment
                            )
                        binding.mainViewHolder.addItemDecoration(
                            MarginItemDecoration(
                                resources.getDimensionPixelSize(
                                    R.dimen.item_margin_7dp
                                )
                            )
                        )
                        binding.mainViewHolder.adapter = mainAdapter
                    }
                    is ApiState.Failure -> {
                        Log.e("gaypop", it.msg.localizedMessage.toString())
                        Log.d("Ray", it.msg.message.toString())
                    }
                    is ApiState.Loading -> {

                    }
                }*/
            }
        }
    }

    override fun clickedCategory(item: ResultEntity) {
        makeToast("${item.name} with id ${item.id} was clicked")
    }

    override fun adClicked(ad: ResultX) {
        println("click---------")
    }
}