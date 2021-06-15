package Adapter

import Models.Music
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilhomjon.h5561mediaplayer.R
import kotlinx.android.synthetic.main.item_rv.view.*

class RvAdapter(var list: List<Music>, var rvItemClick: RvItemClick)
    :RecyclerView.Adapter<RvAdapter.Vh>(){

    inner class Vh(var itemView:View):RecyclerView.ViewHolder(itemView){
        fun onBind(music: Music, position:Int){
            itemView.txt_item_artist.text = music.author
            itemView.txt_item_title.text = music.title
            if (music.imagePath!=""){
                val bm = BitmapFactory.decodeFile(list[position].imagePath)
                itemView.imageView.setImageBitmap(bm)
            }
            itemView.setOnClickListener {
                rvItemClick.itemClick(music, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}
interface RvItemClick{
    fun itemClick(music: Music, position: Int)
}