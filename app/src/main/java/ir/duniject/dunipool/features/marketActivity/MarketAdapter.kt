package ir.duniject.dunipool.features.marketActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.duniject.dunipool.R
import ir.duniject.dunipool.apiManager.model.model.BASE_URL_IMAGE
import ir.duniject.dunipool.apiManager.model.model.model.CoinsData
import ir.duniject.dunipool.databinding.ItemRecyclerMarketBinding

class MarketAdapter(

    private val data: ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback

) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    lateinit var binding: ItemRecyclerMarketBinding


    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindViews(dataCoin: CoinsData.Data) {

            binding.txtCoinName.text = dataCoin.coinInfo.fullName

            binding.txtPrice.text = "$" + dataCoin.rAW.uSD.pRICE.toString()


            val marketcap = dataCoin.rAW.uSD.mKTCAP / 1000000000
            val indexDot = marketcap.toString().indexOf('.')
            binding.txtMarketCap.text =
                "$" + dataCoin.rAW.uSD.mKTCAP.toString().substring(0, indexDot + 3) + "B"


            val taghir = dataCoin.rAW.uSD.cHANGE24HOUR
            if (taghir > 0) {
                binding.txtTagir.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                binding.txtTagir.text =
                    dataCoin.rAW.uSD.cHANGE24HOUR.toString().substring(0, 4) + "%"


            } else if (taghir < 0) {
                binding.txtTagir.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                binding.txtTagir.text =
                    dataCoin.rAW.uSD.cHANGE24HOUR.toString().substring(0, 5) + "%"
            } else {
                binding.txtTagir.text = "0%"
            }





            Glide
                .with(itemView)
                .load(BASE_URL_IMAGE + dataCoin.coinInfo.imageUrl)
                .into(binding.imgItem)


            itemView.setOnClickListener {
                recyclerCallback.onCoinItemClicked(dataCoin)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        binding = ItemRecyclerMarketBinding.inflate(inflater, parent, false)

        return MarketViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    override fun getItemCount(): Int = data.size


    interface RecyclerCallback {

        fun onCoinItemClicked(dataCoin: CoinsData.Data)

    }

}