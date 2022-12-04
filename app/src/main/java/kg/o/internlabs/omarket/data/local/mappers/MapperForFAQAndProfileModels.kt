package kg.o.internlabs.omarket.data.local.mappers

import kg.o.internlabs.omarket.data.remote.model.*
import kg.o.internlabs.omarket.domain.entity.*
import retrofit2.Response

class MapperForFAQAndProfileModels {

    //region Faq
    private fun mapEntityToDbModel(res: ResultsEntity?) = ResultsDto(
        id = res?.id,
        title = res?.title,
        content = res?.content
    )

    private fun mapDbModelToEntity(faq: FAQDto?) = FAQEntity(
        count = faq?.count,
        next = faq?.next,
        previous = faq?.previous,
        results = faq?.results?.map { mapDbModelToEntity(it) })

    private fun mapDbModelToEntity(res: ResultsDto?) = ResultsEntity(
        id = res?.id,
        title = res?.title,
        content = res?.content
    )

    fun mapRespDbModelToRespEntityForFaq(resp: Response<FAQDto?>) = if (resp.isSuccessful) {
        Response.success(mapDbModelToEntity(resp.body()))
    } else {
        resp.errorBody()?.let { Response.error(resp.code(), it) }
    }
    //endregion

    //region toDto
    fun mapEntityToDbModel(faq: MyAdsEntity?) = MyAdsDto(
        count = faq?.count,
        next = faq?.next,
        previous = faq?.previous,
        result = mapEntityToDbModel(faq?.result),
        statuses = faq?.statuses
    )

    fun mapEntityToDbModel(res: MyAdsResultEntity?) = MyAdsResultDto(
        next = res?.next,
        previous = res?.previous,
        count = res?.count,
        results = res?.results?.map { mapEntityToDbModel(it) }
    )

    fun mapEntityToDbModel(res: MyAdsResultsEntity?) = MyAdsResultsDto(
        promotionType = mapEntityToDbModel(res?.promotionType),
        address = res?.address,
        author = mapEntityToDbModel(res?.author),
        latitude = res?.latitude,
        currencyUsd = res?.currencyUsd,
        description = res?.description,
        minifyImages = res?.minifyImages,
        title = res?.title,
        contractPrice = res?.contractPrice,
        uuid = res?.uuid,
        oMoneyPay = res?.oMoneyPay,
        price = res?.price,
        currency = res?.currency,
        location = mapEntityToDbModel(res?.location),
        id = res?.id,
        modifiedAt = res?.modifiedAt,
        category = mapEntityToDbModel(res?.category),
        favorite = res?.favorite,
        viewCount = res?.viewCount,
        longitude = res?.longitude,
        status = res?.status,
        isOwn = res?.isOwn,
    )

    private fun mapEntityToDbModel(promotionType: PromotionTypeEntity?) =
        PromotionTypeDto(type = promotionType?.type)

    private fun mapEntityToDbModel(authorDto: AuthorEntity?) =
        AuthorDto(verified = authorDto?.verified)

    private fun mapEntityToDbModel(locationEntity: LocationEntity?) =
        LocationDto(name = locationEntity?.name)

    private fun mapEntityToDbModel(categoryEntity: CategoryEntity?) =
        CategoryDto(name = categoryEntity?.name)
    // endregion


    private fun mapDbModelToEntity(faq: MyAdsDto?) = MyAdsEntity(
        count = faq?.count,
        next = faq?.next,
        previous = faq?.previous,
        results = faq?.results?.map { mapDbModelToEntity(it) },
        statuses = faq?.statuses
    )

    private fun mapDbModelToEntity(res: MyAdsResultsDto?) = MyAdsResultsEntity(
        id = res?.id,
        title = res?.title,
        content = res?.content
    )

    fun mapRespDbModelToRespEntityForMyAds(resp: Response<MyAdsDto?>) = if (resp.isSuccessful) {
        Response.success(mapDbModelToEntity(resp.body()))
    } else {
        resp.errorBody()?.let { Response.error(resp.code(), it) }
    }
}