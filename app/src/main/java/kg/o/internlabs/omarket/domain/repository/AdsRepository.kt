package kg.o.internlabs.omarket.domain.repository

import androidx.paging.PagingData
import kg.o.internlabs.core.common.ApiState
import kg.o.internlabs.omarket.domain.entity.CategoriesEntity
import kg.o.internlabs.omarket.domain.entity.ads.AdsByCategory
import kg.o.internlabs.omarket.domain.entity.ads.MainResult
import kg.o.internlabs.omarket.domain.entity.ads.ResultX
import kotlinx.coroutines.flow.Flow

interface AdsRepository {

    fun getCategories(token: String): Flow<ApiState<CategoriesEntity>>

    fun getAds(token: String): Flow<PagingData<ResultX>>

    fun getAdsByCategory(token: String, ads: AdsByCategory?): Flow<PagingData<MainResult>>

}