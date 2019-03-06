package com.first.ritik.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplications {
    private val TAG = "ParseApplication"
    val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse is called $xmlData")
        var status = true
        var inEntry = false
        var textValue = ""
        try {
            val factory = XmlPullParserFactory.newInstance() //creates object Lecture 101
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.toLowerCase()   // in start it is null so we have to use the safe call operator(?) for that
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "parse starting tag for " + tagName)
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }
                    XmlPullParser.TEXT -> textValue = xpp.text
                    XmlPullParser.END_TAG -> {
                        Log.d(TAG, "parse: Ending tag for " + tagName)
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry() //create a new object                                }
                                }
                                "name" -> currentRecord.name=textValue
                                "artist" -> currentRecord.artist=textValue
                                "releaseDate" -> currentRecord.artist=textValue
                                "summary" -> currentRecord.summary=textValue
                                "image" -> currentRecord.imageURL=textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        for(app in applications)
        {
            Log.d(TAG,"**********************************")
            Log.d(TAG,app.toString())
        }
        return status
    }
}