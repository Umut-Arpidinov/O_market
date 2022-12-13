package kg.o.internlabs.omarket.domain.entity.ads

data class ResultX(
    val adType: String? = null,
    val address: String? = null,
    val author: Author? = null,
    val authorId: Int? = null,
    val category: CategoryAds? = null,
    val commentary: String? = null,
    val complaintCount: Int? = null,
    val contractPrice: Boolean? = null,
    val createdAt: String? = null,
    val currency: String? = null,
    val currencyUsd: Double? = null,
    val delivery: Boolean? = null,
    val description: String? = null,
    val detail: Int? = null,
    val favorite: Boolean? = null,
    val filters: List<Int>? = null,
    val hasImage: Boolean? = null,
    val id: Int? = null,
    val images: List<String>? = null,
    val isOwn: Boolean? = null,
    val latitude: Any? = null,
    val location: LocationX? = null,
    val longitude: Any? = null,
    val minifyImages: List<String>? = null,
    val moderatorId: Any? = null,
    val modifiedAt: String? = null,
    val numOfViewsInFeed: Int? = null,
    val oMoneyPay: Boolean? = null,
    val oldPrice: String? = null,
    val openingAt: String? = null,
    val price: String? = null,
    val priceSort: String? = null,
    val promotionType: PromotionType? = null,
    val publishedAt: String? = null,
    val removedAt: String? = null,
    val reviewCount: Int? = null,
    val status: String? = null,
    val telegramProfile: String? = null,
    val telegramProfileIsIdent: Boolean? = null,
    val title: String? = null,
    val uuid: String? = null,
    val viewCount: Int? = null,
    val whatsappNum: String? = null,
    val whatsappNumIsIdent: Boolean? = null
)