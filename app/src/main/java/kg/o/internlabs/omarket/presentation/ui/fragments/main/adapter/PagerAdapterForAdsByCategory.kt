/*
package kg.o.internlabs.omarket.presentation.ui.fragments.main.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kg.o.internlabs.omarket.databinding.CardViewMainAdsBinding
import kg.o.internlabs.omarket.domain.entity.ads.MainResult
import kg.o.internlabs.omarket.domain.entity.ads.ResultX
import kg.o.internlabs.omarket.presentation.ui.fragments.main.MainFragment
import kg.o.internlabs.omarket.presentation.ui.fragments.main.PagerImageAdapter

class PagerAdapterForAdsByCategory : PagingDataAdapter<MainResult, PagerAdapterForMain.AdsViewHolder>
    (AdsComparatorForAdsByCaregory) {
    private lateinit var adClicked: AdClickedInMain
    private var fragmentContext: MainFragment? = null

    override fun onBindViewHolder(holder: AdsViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            getItem(position)?.let { it1 -> adClicked.adClicked(//it1) }
        }
    }

    fun setInterface(adClicked: AdClickedInMain, mainFragment: MainFragment) {
        this.adClicked = adClicked
        fragmentContext = mainFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AdsViewHolder(CardViewMainAdsBinding.inflate(LayoutInflater.from(parent.context)))

    inner class AdsViewHolder(private val binding: CardViewMainAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var favorite = true

        fun bind(item: MainResult?) = with(binding) {
            vipIcon.isVisible = item?.promotionType?.type == "vip"
            oPayIcon.isVisible = item?.oMoneyPay ?: false
            setPager(item?.minifyImages, binding)
            item?.let { setPrice(it) }
            oldPriceProduct.isVisible = item?.oldPrice.isNullOrEmpty().not()
            if (oldPriceProduct.isVisible) {
                item?.let { setOldPriceWithCurrency(it) }
            }
            nameProduct.text = item?.title
            nameCategory.text = item?.category?.name
            placeProduct.text = item?.let { placeAndDelivery(it) }

            favoriteIcon.setOnClickListener {
                if (item?.favorite == false || item?.favorite == null && !favorite) {
                    favoriteIcon.setImageResource(kg.o.internlabs.core.R.drawable.ic_favorite_pressed)
                    favorite = true
                } else {
                    favorite = false
                    favoriteIcon.setImageResource(kg.o.internlabs.core.R.drawable.ic_favorite)
                }
            }
        }

        private fun setPager(minifyImages: List<String>?, binding: CardViewMainAdsBinding) =
            with(binding) {
                val pagerAdapter2 =
                    fragmentContext?.let {
                        PagerImageAdapter(
                            it,
                            minifyImages,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                imgAds.adapter = pagerAdapter2
                indicator.attachToPager(imgAds)
                indicator.attachToPager(imgAds)
            }

        @SuppressLint("SetTextI18n")
        private fun setPrice(item: MainResult): Unit = with(binding) {
            with(item) {
                if (currency == "som") {
                    val resultString = "$price c"
                    val spannableString = SpannableString(resultString)
                    spannableString.setSpan(
                        UnderlineSpan(), resultString.lastIndex,
                        resultString.length, 0
                    )
                    priceProduct.text = spannableString
                } else if (currency != null) {
                    priceProduct.text = "$price $currency"
                } else {
                    priceProduct.text = price
                }
            }
        }

        private fun setOldPriceWithCurrency(item: MainResult) = with(binding) {
            if (item.oldPrice == null || item.oldPrice == "") {
                oldPriceProduct.visibility = View.GONE
            } else {
                oldPriceProduct.visibility = View.VISIBLE
                oldPriceProduct.paintFlags =
                    oldPriceProduct.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    private fun placeAndDelivery(item: MainResult) =
        if (item.delivery == true) {
            fragmentContext?.requireActivity()
                ?.getString(kg.o.internlabs.core.R.string.delivery_available)
                ?.let { String.format(it, item.location?.name) }
        } else {
            item.location?.name
        }
}

object AdsComparatorForAdsByCaregory : DiffUtil.ItemCallback<MainResult>() {
    override fun areItemsTheSame(oldItem: MainResult, newItem: MainResult): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: MainResult, newItem: MainResult): Boolean {
        return oldItem == newItem
    }
}
*/
