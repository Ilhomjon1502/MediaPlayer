package com.ilhomjon.h5561mediaplayer

import Models.Music
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_media.view.*
import kotlinx.android.synthetic.main.item_rv.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MediaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var root:View
    lateinit var music: Music
    var position:Int = 0
    var mediaPlayer:MediaPlayer? = null
    lateinit var handler:Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_media, container, false)

        position = arguments?.getInt("position", -1)!!
        music = arguments?.getSerializable("music") as Music

        return root
    }

    override fun onResume() {
        super.onResume()


        if (position!=-1) {
            mediaPlayer=null
            mediaPlayer = MediaPlayer.create(context, Uri.parse(MyData.list[position].musicPath))
            mediaPlayer?.start()
            root.btn_pause.background = resources.getDrawable(R.drawable.ic_pause)
            root.seekbar.max = mediaPlayer?.duration!!
            handler = Handler(activity?.mainLooper!!)

            root.txt_all_music_size.text = MyData.list.size.toString()
            root.txt_number_music.text = (position+1).toString()
            if (MyData.list[position].imagePath!=""){
                val bm = BitmapFactory.decodeFile(MyData.list[position].imagePath)
                root.image_music.imageView.setImageBitmap(bm)
            }

            root.txt_music_artist.text = MyData.list[position].author
            root.txt_music_name.text = MyData.list[position].title

            root.txt_all_time_music.text = milliSecondsToTimer(mediaPlayer?.duration!!.toLong())
        }
        if (mediaPlayer?.isPlaying!!) {
            handler.postDelayed(runnable, 100)
        }

        root.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        root.btn_back_30.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.minus(30000))
        }
        root.btn_next_30.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.plus(30000))
        }

        root.image_menu_more.setOnClickListener {
            releaseMP()
            findNavController().popBackStack()
        }
        root.btn_pause.setOnClickListener {
            if (mediaPlayer?.isPlaying!!){
                mediaPlayer?.pause()
                root.btn_pause.background = resources.getDrawable(R.drawable.ic_play)
            }else{
                mediaPlayer?.start()
                root.btn_pause.background = resources.getDrawable(R.drawable.ic_pause)
            }
        }
        root.btn_next_music.setOnClickListener {
            if (++position<MyData.list.size) {
                releaseMP()
                onResume()
            }else{
                position = 0
                releaseMP()
                onResume()
            }
        }
        root.btn_back_music.setOnClickListener {
            if (--position>=0) {
                releaseMP()
                onResume()
            }else{
                position = MyData.list.size-1
                releaseMP()
                onResume()
            }
        }
    }

    //app stop when music stop
    private fun releaseMP(){
        if (mediaPlayer != null){
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        releaseMP()
    }

    private var runnable = object :Runnable{
        override fun run() {

            if (mediaPlayer != null) {
                root.seekbar.progress = mediaPlayer?.currentPosition!!
                root.txt_music_time_position.text =
                    milliSecondsToTimer(mediaPlayer?.currentPosition!!.toLong())
                if (root.txt_music_time_position.text.toString() == root.txt_all_time_music.text.toString()) {
                    releaseMP()
                    if (++position < MyData.list.size) {
                        releaseMP()
                        onResume()
                    } else {
                        position = 0
                        releaseMP()
                        onResume()
                    }
                }
                handler.postDelayed(this, 100)
            }
        }
    }

    fun milliSecondsToTimer(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MediaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}