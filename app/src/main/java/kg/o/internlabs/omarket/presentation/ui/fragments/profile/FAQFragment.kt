package kg.o.internlabs.omarket.presentation.ui.fragments.profile

import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kg.o.internlabs.core.base.BaseFragment
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.data.remote.model.FAQDto
import kg.o.internlabs.omarket.databinding.FragmentFAQBinding
import kg.o.internlabs.omarket.domain.entity.FAQEntity
import kg.o.internlabs.omarket.domain.entity.ResultsEntity
import kg.o.internlabs.omarket.utils.makeToast
import kg.o.internlabs.omarket.utils.safeFlowGather
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FAQFragment : BaseFragment<FragmentFAQBinding, ProfileViewModel>() {
    private lateinit var faqEntity: ArrayList<ResultsEntity>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FaqAdapter

    override val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    override fun inflateViewBinding(inflater: LayoutInflater) =
        FragmentFAQBinding.inflate(inflater)

    override fun initViewModel() {
        super.initViewModel()
        viewModel.getAccessTokenFromPrefs()
        viewModel.getFaq()

    }

    override fun initView() {
        super.initView()
        getCategories()

//        recyclerView = requireView().findViewById(R.id.recycler_faq)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        adapter = FaqAdapter(this.faqEntity)
//        recyclerView.adapter = adapter
        val list = ArrayList<ResultsEntity>()
        binding.recyclerFaq.layoutManager = LinearLayoutManager(context)
        binding.recyclerFaq.adapter = FaqAdapter(list)
    }


    private fun getCategories() {
        this@FAQFragment.safeFlowGather {
            viewModel.faqs.collectLatest {
                when(it) {
                    is ApiState.Success -> {

                        it.data.results
                        it.data.results?.get(0)?.title
                        it.data.results?.get(0)?.id
                        it.data.results?.get(0)?.content
                        it.data.results
                        println(it.data.results!! + "=============")
                        //TODO it.date.result вернет List<ResultsEntity> в нем хранятся
                        //TODO it.data.result?.get(0)?.title  questions
                        //TODO it.data.result?.get(0)?.id  id
                        //TODO it.data.result?.get(0)?.content  answers
                        //TODO it.data.results   это для получение всего списка вопрос-ответов все
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