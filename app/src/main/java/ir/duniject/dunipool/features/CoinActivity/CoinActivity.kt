package ir.duniject.dunipool.features.CoinActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import ir.duniject.dunipool.R
import ir.duniject.dunipool.apiManager.model.model.*
import ir.duniject.dunipool.apiManager.model.model.model.ChartData

import ir.duniject.dunipool.apiManager.model.model.model.CoinAboutItem
import ir.duniject.dunipool.apiManager.model.model.model.CoinsData
import ir.duniject.dunipool.databinding.ActivityCoinBinding

class CoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoinBinding
    lateinit var dataThisCoin: CoinsData.Data
    lateinit var dataThisCoinAbout: CoinAboutItem
    val apiManeger = ApiManeger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fromIntent = intent.getBundleExtra("bundle")!!
        dataThisCoin = fromIntent.getParcelable<CoinsData.Data>("bundle1")!!

        if (fromIntent.getParcelable<CoinAboutItem>("bundle2") != null) {

            dataThisCoinAbout = fromIntent.getParcelable<CoinAboutItem>("bundle2")!!
        } else {
            dataThisCoinAbout = CoinAboutItem()
        }

        binding.layoutToolbar.toolbar.title = dataThisCoin.coinInfo.fullName


        initUi()


    }

    private fun initUi() {

        initChartUi()
        initStatisticsUi()
        initAboutUi()

    }

    private fun initAboutUi() {
        binding.layoutAbout.txtWebsite.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout.coinGithub
        binding.layoutAbout.txtTwiter.text = "@" + dataThisCoinAbout.coinTwitter
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout.coinReddit
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout.coinDesc


        binding.layoutAbout.txtWebsite.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!)

        }

        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinGithub!!)
        }

        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout.coinReddit!!)
        }
        binding.layoutAbout.txtTwiter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout.coinTwitter)
        }

    }

    private fun openWebsiteDataCoin(Url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Url))
        startActivity(intent)
    }

    private fun initStatisticsUi() {


        binding.layoutStatistiscs.tvOpenAmount.text = dataThisCoin.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistiscs.tvTodayHighAmount.text = dataThisCoin.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistiscs.tvTodayLowAmunt.text = dataThisCoin.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistiscs.tvChangeTodayAmount.text = dataThisCoin.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistiscs.tvAlgorithm.text = dataThisCoin.coinInfo.algorithm
        binding.layoutStatistiscs.tvTotalVolum.text = dataThisCoin.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistiscs.tvAvgMarketCapAmount.text = dataThisCoin.dISPLAY.uSD.mKTCAP
        binding.layoutStatistiscs.tVSupplyNumber.text = dataThisCoin.dISPLAY.uSD.sUPPLY

    }

    @SuppressLint("ResourceAsColor")
    private fun initChartUi() {

        var period: String = HOUR
        reguestAndShowData(period)
        binding.layoutChart.radio.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.radio_12h -> {
                    period = HOUR
                }
                R.id.radio_1d -> {
                    period = HOUR24
                }
                R.id.radio_1w -> {
                    period = WEEK
                }
                R.id.radio_1m -> {
                    period = MONTH
                }
                R.id.radio_3m -> {
                    period = MONTH3
                }
                R.id.radio_1y -> {
                    period = YEAR
                }
                R.id.radio_All -> {
                    period = ALL
                }
            }

            reguestAndShowData(period)


        }

        binding.layoutChart.txtchartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChange1.text = "" + dataThisCoin.dISPLAY.uSD.cHANGE24HOUR

        if (dataThisCoin.coinInfo.fullName == "BUSD") {
            binding.layoutChart.txtChartChange2.text = "0%"
        } else {
            binding.layoutChart.txtChartChange2.text =
                dataThisCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }
        val thagir = dataThisCoin.rAW.uSD.cHANGE24HOUR
        if (thagir > 0) {

            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )

            binding.layoutChart.sparkviewMain.lineColor =
                ContextCompat.getColor(binding.root.context, R.color.colorGain)


        } else if (thagir < 0) {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )

            binding.layoutChart.sparkviewMain.lineColor =
                ContextCompat.getColor(binding.root.context, R.color.colorLoss)

        }
binding.layoutChart.sparkviewMain.setScrubListener {
    if (it == null){
        binding.layoutChart.txtchartPrice.text = dataThisCoin.dISPLAY.uSD.pRICE

    } else{
        binding.layoutChart.txtchartPrice.text = (it as ChartData.Data).close.toString()
    }
}

    }

    fun reguestAndShowData(period: String) {
        apiManeger.getChartData(
            dataThisCoin.coinInfo.name,
            period,
            object : ApiManeger.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onError(errorMessage: String) {

                    Toast.makeText(
                        this@CoinActivity,
                        "error => " + errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {
                    val chartAdapter =
                        ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkviewMain.adapter = chartAdapter

                }

            })
    }

}