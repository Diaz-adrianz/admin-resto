package com.pt10_restoku

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myappname.TinyDB
import com.google.gson.Gson

class DetailActivity : AppCompatActivity() {

    private lateinit var vPoster: ImageView
    private lateinit var vNama: TextView
    private lateinit var vTags: TextView
    private lateinit var vDeskripsi: TextView
    private lateinit var vHarga: TextView
    private lateinit var cbTersedia: CheckBox
    private lateinit var thisMenu: MenuModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar!!.title = "WAREG RESTO"

        vPoster = findViewById(R.id.poster)
        vNama = findViewById(R.id.nama)
        vTags = findViewById(R.id.tags)
        vDeskripsi = findViewById(R.id.deskripsi)
        vHarga = findViewById(R.id.harga)
        cbTersedia = findViewById(R.id.ketersediaan)

        val bundle: Bundle? = intent.extras
        val gson: Gson = Gson()

        thisMenu = gson.fromJson(bundle!!.getString("MENU"), MenuModel::class.java)

        Glide
            .with(this)
            .load(thisMenu.poster)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .centerCrop()
            .into(vPoster);
        vNama.text = thisMenu.nama
        vTags.text = thisMenu.tags.joinToString(" - ")
        vDeskripsi.text = thisMenu.deskripsi
        vHarga.text = thisMenu.harga.toString()
        cbTersedia.isChecked = thisMenu.tersedia
    }

    fun setKetersediaan(v: View) {
        val cb: CheckBox = v as CheckBox

        thisMenu.tersedia = cb.isChecked

        var listMenu = ArrayList<MenuModel>()
        var tempList = ArrayList<Any>()

        tempList = TinyDB(this).getListObject("menuuu", MenuModel::class.java)

        for (menu in tempList) {
            listMenu.add(menu as MenuModel)
        }


        var pos: Int = -1
        for (menu in listMenu) {
            if (menu.nama == thisMenu.nama) {
                pos = listMenu.indexOf(menu)
            }
        }


        listMenu.set(pos, thisMenu)

        val tempList2 = ArrayList<Any>()
        for (p in listMenu) {
            tempList2.add(p as Any)
        }


        TinyDB(this).putListObject("menuuu", tempList2)
    }

}