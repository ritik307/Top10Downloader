package com.first.ritik.top10downloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.io.InputStream
import java.net.URL

class ViewHolder(v:View)
{
    val tvName:TextView = v.findViewById(R.id.tvName)
    val tvArtist:TextView =v.findViewById(R.id.tvArtist)
    val tvSummary:TextView =v.findViewById(R.id.tvSummary)
    val tvImage:ImageView=v.findViewById(R.id.tvImage)
}

class FeedAdapter(context: Context,private val resource:Int,private val application:List<FeedEntry>)
    :ArrayAdapter<FeedEntry>(context,resource)
{
    private val TAG="FeedAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(TAG,"getCount() called")
        return application.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {  //to understand getVie refer this : https://stackoverflow.com/questions/10120119/how-does-the-getview-method-work-when-creating-your-own-custom-adapter
        val view:View
        val viewHolder:ViewHolder
        if(convertView ==null)        //Lecture 109
        {
            view=inflater.inflate(resource,parent,false)
            viewHolder=ViewHolder(view)     //Lecture 110
            view.tag=viewHolder             //https://stackoverflow.com/questions/5291726/what-is-the-main-purpose-of-settag-gettag-methods-of-view
        }
        else
        {
            view=convertView
            viewHolder=view.tag as ViewHolder
        }
        Log.d(TAG,"---------------------------------parent is : $parent-------------------------------------------")

//        val tvName = view.findViewById<TextView>(R.id.tvName)
//        val tvSummary = view.findViewById<TextView>(R.id.tvSummary)
//        val tvArtist = view.findViewById<TextView>(R.id.tvArtist)

        val currentApp=application[position]

        viewHolder.tvName.text=currentApp.name
        viewHolder.tvArtist.text=currentApp.artist
        viewHolder.tvSummary.text=currentApp.summary
        Picasso.get().load(currentApp.imageURL).into(viewHolder.tvImage)

        return view

    }
}