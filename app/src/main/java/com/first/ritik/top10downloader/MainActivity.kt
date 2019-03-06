package com.first.ritik.top10downloader

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu

import android.view.MenuItem

import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

import kotlin.properties.Delegates



private const val TAG = "MainActivity"

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
    override fun toString(): String {
        return """
            name= $name
            artist= $artist
            releaseDate= $releaseDate
            summary= $summary
            imageURL= $imageURL
            """.trimIndent()
    }
}


class MainActivity : AppCompatActivity() {

    private var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit: Int = 10
//    private val downloadData by lazy {
//        DownloadData(this, xmlListView)
//    }  //because context and listwiew will not be created before setContentView(Line 51) Lecture-106
    private var feedCachedUrl="Invalidated"
    private val State_Url="feedUrl"
    private val State_Limit="feedLimit"
    private var downloadData: DownloadData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: android.support.v7.app.ActionBar? = supportActionBar                 //to add color to the toolbar
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#3C3C3E")))//this is for color too
        if(savedInstanceState !=null)
        {
            feedUrl=savedInstanceState.getString(State_Url)
            feedLimit=savedInstanceState.getInt(State_Limit)
        }
        Log.d(TAG, "onCreate: start")
        downloadUrl(feedUrl)
//        val downloadData=DownloadData(this,xmlListView)
//        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "onCreate: done")
    }


    fun downloadUrl(feedUrl: String)  // Lecture 112
    {
        if(feedUrl!=feedCachedUrl)
        {
            val downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl.format(feedLimit))
            Log.d(TAG, "ocCreate: done")
            Log.d(TAG,"Url changed successfully")
            feedCachedUrl=feedUrl
        }
        else
        {
            Log.d(TAG,"the url didnt changed")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        val feedUrl: String

        when (item?.itemId) {
            R.id.mnuFree ->
                {
                    feedUrl ="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
                    setTitle("Top Free Applications")
                }
            R.id.mnuPaid -> {
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
                setTitle("Top Paid Applications")
            }
            R.id.mnuSongs -> {
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
                setTitle("Top Songs")
            }
                R.id.mnuTop10, R.id.mnuTop25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit to $feedLimit")
                }
            }
            R.id.mnuRefresh -> feedCachedUrl="Invalidated"
            else ->
                return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(State_Url,feedUrl)
        outState.putInt(State_Limit,feedLimit)
    }
    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) :
            AsyncTask<String, Void, String>()   // lecture 101 onwards
        {
            private val TAG = "DownloadData"

            var propcontext: Context by Delegates.notNull()
            var proplistview: ListView by Delegates.notNull()

            init {
                propcontext = context
                proplistview = listView
            }


            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//              Log.d(TAG,"onPostExecution: parameter is $result")
                val parseApplication = ParseApplications()
                parseApplication.parse(result)

//                val arrayAdapter=ArrayAdapter<FeedEntry>(propcontext,R.layout.list_item,parseApplication.applications)
//                proplistview.adapter=arrayAdapter
                val feedAdapter = FeedAdapter(propcontext, R.layout.list_record, parseApplication.applications)
                Log.d(
                    TAG,
                    "-------------------------------testing listrecord ${R.layout.list_record}------------------------------"
                )
                proplistview.adapter = feedAdapter
            }

            override fun doInBackground(vararg params: String?): String {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d(TAG, "doInBackgroud: start with ${params[0]} ")
                val rssFeed = downloadXML(params[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground : Error in downloading")
                }
                return rssFeed

            }

            private fun downloadXML(urlPath: String?): String {
                Log.d(TAG, "downloadXML: inside")
                return URL(urlPath).readText()

//                val xmlResult=StringBuffer()
//                try{
//                    val url=URL(urlPath)           // throw MalformedURLException
//                    val connection:HttpURLConnection=url.openConnection() as HttpURLConnection  //throw IOException
//                    val response=connection.responseCode
//                    Log.d(com.first.ritik.top10downloader.TAG," downloadXML : the response code was $response")
//
// //            val inputStream = connection.inputStream
// //            val inputStreamReader=InputStreamReader(inputStream)
// //            val reader=BufferedReader(inputStreamReader)                               // you can write the above code in below format for more check vid 97
// //                    val reader=BufferedReader(InputStreamReader(connection.inputStream))
// //                    val inputBuffer=CharArray(500)
// //                    var charsRead =0
// //                    while (charsRead>=0)                   // to understand the while loop check vid 97 question section and the vid itself
// //                    {
// //                        charsRead=reader.read(inputBuffer)
// //                        Log.d(TAG,"----------------------whats inside  charsRead $charsRead-----------------------")
// //                        if(charsRead >0)
// //                        {
// //                            xmlResult.append(String(inputBuffer,0,charsRead))
// //                        }
// //                    }
// //                    reader.close()
// //                    Log.d(TAG,"Recieved ${xmlResult.length} bytes")
//                    connection.inputStream.buffered().reader().use {xmlResult.append(it.readText()) }   //LECTURE-99 ctrl+mclick to check // it is pinting to xmlResult
//                    return xmlResult.toString()
//                }
// //                catch(e:MalformedURLException)
// //                {
// //                    Log.e(TAG,"downloadXMl: Invalid URL ${e.message}")
// //                }
// //                catch (e:IOException)
// //                {
// //                    Log.e(TAG,"downloadXML: IO exception in reading data: ${e.message}")
// //                }
// //                catch (e:SecurityException)
// //                {
// //                    Log.e(TAG,"downloadXML: No internet access provided ${e.message} ")
// //                }
// //                catch(e:Exception)
// //                {
// //                    Log.e(TAG,"downloadXML: Unknown Exception ${e.message} ")
// //                }
//                  catch (e:Exception)
//                  {
//                      val errorMessage:String=when(e)
//                      {
//                          is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
//                          is IOException -> "downloadXML: IO Exception reading data ${e.message}"
//                          is SecurityException -> {
//                              e.printStackTrace()
//                              "downloadXML: Security Exception. Need permission? ${e.message}"
//                          }
//                          else -> "Unknown Exception ${e.message}"
//
//                      }
//                  }
//                return ""
            }

        }
    }

}
