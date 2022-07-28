package ir.duniject.dunipool.features.marketActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import ir.duniject.dunipool.apiManager.model.model.ApiManeger
import ir.duniject.dunipool.apiManager.model.model.model.CoinAboutData
import ir.duniject.dunipool.apiManager.model.model.model.CoinAboutItem
import ir.duniject.dunipool.apiManager.model.model.model.CoinsData
import ir.duniject.dunipool.databinding.ActivityMarketBinding
import ir.duniject.dunipool.features.CoinActivity.CoinActivity

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {
    lateinit var binding: ActivityMarketBinding
    lateinit var dataNews: ArrayList<Pair<String, String>>
    lateinit var aboutDataMap : MutableMap<String , CoinAboutItem>

    val apiManager = ApiManeger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.toolbar.title = "DuniPool Market"

        binding.layoutWatchlist.btnshowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }


        binding.swipeRefreshlayout.setOnRefreshListener {
            initUi()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshlayout.isRefreshing = false
            }, 2000)

        }

        getAboutDataFromAssets()

    }

    override fun onResume() {
        super.onResume()
        initUi()
    }

    private fun initUi() {
        getNewsFromApi()
        getTopCoinsFromApi()


    }

    private fun getAboutDataFromAssets() {


        val fileInString = applicationContext.assets.open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString , CoinAboutData::class.java)


         aboutDataMap = mutableMapOf<String, CoinAboutItem>()


        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(

                it.info.web,
                it.info.github,
                it.info.twt,
                it.info.desc,
                it.info.reddit

            )
        }

    }


    private fun getNewsFromApi() {
        apiManager.getNews(object :ApiManeger.ApiCallback<ArrayList<Pair<String, String>>>{


            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "error =>"+ errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                dataNews = data
                refreshNews()

            }

        })
        }
    private fun refreshNews() {

        val randomAccess = (0..49).random()

        binding.layoutNews.txtNews.text = dataNews[randomAccess].first


        binding.layoutNews.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
            startActivity(intent)
        }


        binding.layoutNews.txtNews.setOnClickListener {
            refreshNews()
        }

    }

    private fun getTopCoinsFromApi() {
        apiManager.getCoinsList(object :ApiManeger.ApiCallback<List<CoinsData.Data>>{
            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "error =>"+errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(data)
            }

        })

        }
    private fun showDataInRecycler(data : List<CoinsData.Data>){

        val marketAdapter =MarketAdapter(ArrayList(data ), this)
        binding.layoutWatchlist.recyclerMain.adapter = marketAdapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)
    }
    override fun onCoinItemClicked(dataCoin: CoinsData.Data) {

       val intent =Intent(this ,CoinActivity::class.java)

       val bundle = Bundle()
        bundle.putParcelable("bundle1" , dataCoin)
        bundle.putParcelable("bundle2" , aboutDataMap[dataCoin.coinInfo.name]!!)
        intent.putExtra("bundle" , bundle)


        startActivity(intent)
    }




}



