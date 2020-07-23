package kupluk.smk.coding

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.internal.Objects
import kotlinx.android.synthetic.main.activity_jadwal_sholat.*
import kotlinx.android.synthetic.main.fragment_tasbih.*
import kupluk.smk.coding.activity.JadwalSholatActivity
import kupluk.smk.coding.api.Api
import kupluk.smk.coding.data.jadwal.Data
import kupluk.smk.coding.data.jadwal.jadwal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class TasbihFragment : Fragment() {
    private val eventDate = Calendar.getInstance()
    private val currentDate = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super .onCreate(savedInstanceState)

    }

    private val handler = Handler()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasbih, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.post(object : Runnable{
            override fun run() {
                handler.postDelayed(
                    this,1000)
                    fetchJson()
            }
        })

    }

//LOAD API
//    End Load API
//Load ConTdown

    fun updateTime(jadwalShalat: Data?) {
        var subuh = ambilJam( jadwalShalat!!.subuh)
        var dzuhur  = ambilJam( jadwalShalat!!.dzuhur)
        var ashar = ambilJam( jadwalShalat!!.ashar)
        var maghrib = ambilJam( jadwalShalat!!.maghrib)
        var isya = ambilJam( jadwalShalat!!.isya)

        currentDate.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        eventDate.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        when{
            //subuh
                currentDate[Calendar.HOUR] >= 19 && currentDate[Calendar.HOUR] <= 23 || currentDate[Calendar.HOUR] >= 0 && currentDate[Calendar.HOUR] <=5 ->{
                    countDown(subuh)

            }
            currentDate[Calendar.HOUR] > 5 && currentDate[Calendar.HOUR] <= 11->{
                countDown(dzuhur)
            }
            currentDate[Calendar.HOUR] >= 12 && currentDate[Calendar.HOUR] <= 15 ->{
            countDown(ashar)
            }
            currentDate[Calendar.HOUR] >= 16 && currentDate[Calendar.HOUR] < 18->{
            countDown(maghrib)
            }
            currentDate[Calendar.HOUR] >= 18 && currentDate[Calendar.HOUR] <= 19->{
                countDown(isya)
            }else->{
            Toast.makeText(this.context,"jam error",Toast.LENGTH_SHORT).show()
            }
        }
        endEvent(currentDate, eventDate)


    }

    private fun endEvent(currentdate: Calendar, eventdate: Calendar) {
        if (currentdate.time >= eventdate.time) {
            waktu.text = "Happy New Year!"
            //Stop Handler
            handler.removeMessages(0)
        }
    }
//
//    end load cd


    private fun fetchJson() {
        val current = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = formatter.format(current)

        val call: Call<jadwal> = Api.getSuratServices.getJadwal(formattedDate)
        call.enqueue(object : Callback<jadwal> {
            override fun onFailure(call: Call<jadwal>, t: Throwable) {
                t.printStackTrace()
            }
            override fun onResponse(call: Call<jadwal>, response: Response<jadwal>) {
                response.body()?.jadwal?.data.let { updateTime(it) }
            }
        })
    }
      private  fun countDown(jam : Int){
            eventDate[Calendar.HOUR] = jam
            eventDate[Calendar.SECOND] = 0
            val diff = eventDate.timeInMillis - currentDate.timeInMillis
            val hours = diff / (1000 * 60 * 60) % 24
            val minutes = diff / (1000 * 60) % 60
            val seconds = (diff / 1000) % 60
            waktu.text = " ${hours}jam ${minutes} menit ${seconds} detik"
        }
    private fun ambilJam(jam : String):Int{
        var titikDua =  jam?.indexOf(":")!!.toInt()
        var b = jam?.substring(0,titikDua)
        var c = jam?.substring(titikDua+1,jam?.length).toInt()
        return b.toInt()
    }
}