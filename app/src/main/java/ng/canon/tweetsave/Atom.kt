package ng.canon.tweetsave

import android.Manifest
import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Patterns
import android.widget.Toast
import com.esafirm.rxdownloader.RxDownloader
import com.facebook.ads.InterstitialAd
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class Atom : AppCompatActivity() {
    var observable: Observable<String>? = null
    var mu: Array<String>? = null
    var version = ""
    var Primaryresponse: Response? = null
    var realUrl =""
    var file_url = ""
    var track = ""
    var topic = ""
    var tlc = ""
    var finalUrl = ""
    var mInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atom)
        val intents = intent
        tlc = intents.getStringExtra(Intent.EXTRA_TEXT)

        if (ContextCompat.checkSelfPermission(this@Atom, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {



            //Load Write permissions here

          linkCore(tlc)

        }else{


            lasma()




        }


    }




















    fun SaveDit(link: String): String {

        var pink = ""
        val saveclient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
        val saverequest = Request.Builder()
                .url(link)
                .build()
        val response = saveclient.newCall(saverequest).execute()


        val json = JSONObject(response.body()!!.string())


        return json.toString()
    }












    override fun onBackPressed() {

    }











    fun linkCore(videoID: String) {


        observable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    mu = extracTors(videoID)
                    realUrl = mu!![0]
                    val instaUrl = "https://www.saveitoffline.com/process/?url=$realUrl"
                    val respond = SaveDit(instaUrl)
                    val json = JSONObject(respond)
                    val arrays = json.getJSONArray("urls")
                    var links = arrays.getJSONObject(0).getString("id")
                    finalUrl = links


                    subscriber.onNext(links)

                } catch (e: Exception) {

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {


                    }

                    override fun onComplete() {


                        val toast = getString(R.string.starts)
                        mrSave(finalUrl)
                        Toast.makeText(this@Atom,""+toast,Toast.LENGTH_LONG).show()
                        finish()



                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext, ""+getString(R.string.sorry), Toast.LENGTH_LONG).show()
                        finish()

                    }

                    override fun onNext(response: String) {

                    }
                })

    }



    companion object {


        fun extracTors(text: String): Array<String> {
            val links = ArrayList<String>()
            val m = Patterns.WEB_URL.matcher(text)
            while (m.find()) {
                val urls = m.group()
                links.add(urls)
            }

            return links.toTypedArray()
        }
    }





    fun lasma(){
        val request = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        request.send()
        request.listeners {

            onAccepted { permissions ->

               linkCore(tlc)
            }

            onDenied { permissions ->
                lasma()
            }

            onPermanentlyDenied { permissions ->
                // Notified when the permissions are permanently denied.
            }

            onShouldShowRationale { permissions, nonce ->
                // Notified when the permissions should show a rationale.
                // The nonce can be used to request the permissions again.
            }
        }
        // load permission methods here
    }















    fun mrSave(urld: String){
        val rxDownloader = RxDownloader(applicationContext)
        var extension = "mp4"
        var desc = getString(R.string.starts)
        val timeStamp =  System.currentTimeMillis()
        val filename = topic
        val name = "tweet_$timeStamp" + "." + extension
        val dex = File(Environment.getExternalStorageDirectory().absolutePath, "twittersave")
        if (!dex.exists())
            dex.mkdirs()

        val Download_Uri = Uri.parse(urld)
        val downloadManager =  getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request =  DownloadManager.Request(Download_Uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false)
        request.setTitle(name)
        request.setDescription(desc)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir("/twittersave",  name)

        rxDownloader.download(request).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {


                    }

                    override fun onNext(t: String) {


                    }

                    override fun onSubscribe(d: Disposable) {


                    }


                })

    }




}
