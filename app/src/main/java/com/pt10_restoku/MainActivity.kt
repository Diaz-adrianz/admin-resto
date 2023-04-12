package com.pt10_restoku

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappname.TinyDB
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var listMenu: ArrayList<MenuModel>
    private lateinit var filteredListMenu: ArrayList<MenuModel>
    private lateinit var btnTambah: Button
    private lateinit var btnRefresh: Button
    private var posToEdit: Int = -1

    private lateinit var RVlistMenu: RecyclerView
    private lateinit var RVlistMenuAdapter: MenuAdapter
    private lateinit var BStambahMenu: BottomSheetDialog
    private lateinit var VtambahMenu: View
    private lateinit var DB: TinyDB

    private lateinit var inpNama: EditText
    private lateinit var inpDeskripsi: EditText
    private lateinit var inpPoster: EditText
    private lateinit var inpHarga: EditText
    private lateinit var inpTags: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnTutup: Button

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "WAREG RESTO"

//        SETUP DB SHAREDPREF
        DB = TinyDB(this)

//        SETUP RECYCLERVIEW
        RVlistMenu = findViewById(R.id.list_menu)
        RVlistMenu.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
        RVlistMenu.setHasFixedSize(true)

        listMenu = arrayListOf<MenuModel>()
        filteredListMenu = arrayListOf<MenuModel>()

        setupRecyclerView()
        setupFragmentTambah()
        getMenu()

        btnRefresh = findViewById(R.id.refresh_menu)
        btnRefresh.setOnClickListener{
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.options_menu,menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenu(newText!!)
                return false
            }
        })
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                getMenu()
                filteredListMenu.clear()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun filterMenu(text: String) {
        filteredListMenu.clear()

        for (item in listMenu) {
            if (item.nama.toLowerCase().contains(text.toLowerCase())) {
                filteredListMenu.add(item)
            }
        }
        if (filteredListMenu.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        }
        RVlistMenuAdapter.filterList(filteredListMenu)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMenu() {
        var tempList = ArrayList<Any>()

        listMenu.clear()
        tempList = DB.getListObject("menuuu", MenuModel::class.java)

        for (menu in tempList) {
            listMenu.add(menu as MenuModel)
        }

        RVlistMenuAdapter.filterList(listMenu)
    }

    private fun setMenu(mode: String, pos: Int) {
        if (mode == "DELETE") {
            listMenu.removeAt(pos)
        } else {
            val MenuBaru: MenuModel = MenuModel(
                inpNama.text.toString(),
                inpDeskripsi.text.toString(),
                inpPoster.text.toString(),
                true,
                inpHarga.text.toString().toLong(),
                inpTags.text.toString().split(", ")
            )
            if (mode == "EDIT") {
                listMenu.set(pos, MenuBaru)
            } else if (mode == "CREATE"){
                listMenu.add(0, MenuBaru)
            }
        }

        val tempList = ArrayList<Any>()
        for (p in listMenu) {
            tempList.add(p as Any)
        }
        DB.putListObject("menuuu", tempList)

        getMenu()
        resetFormTambah()
    }

    private fun setupRecyclerView() {

        RVlistMenuAdapter = MenuAdapter(this, listMenu)

        val swipegesture = object : SwipeGesture(this){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val from_pos = viewHolder.bindingAdapterPosition
                val to_pos = target.bindingAdapterPosition

                Collections.swap(listMenu,from_pos,to_pos)
                RVlistMenuAdapter.notifyItemMoved(from_pos,to_pos)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                var thisPos: Int = viewHolder.absoluteAdapterPosition

                if (filteredListMenu.isNotEmpty()) {
                    thisPos = listMenu.indexOf(filteredListMenu[viewHolder.absoluteAdapterPosition])
                }

                when(direction){
                    ItemTouchHelper.LEFT ->{
                        setMenu("DELETE", thisPos)
                    }
                    ItemTouchHelper.RIGHT -> {
                        val currentMenu = listMenu[thisPos]
                        posToEdit = thisPos

                        if (currentMenu.tersedia) {
                            inpNama.setText(currentMenu.nama)
                            inpDeskripsi.setText(currentMenu.deskripsi)
                            inpPoster.setText(currentMenu.poster)
                            inpHarga.setText(currentMenu.harga.toString())
                            inpTags.setText(currentMenu.tags.joinToString(", "))

                            BStambahMenu.show()
                        } else {
                            Toast.makeText(this@MainActivity, "Ubah ketersediaan terlebih dahulu", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(RVlistMenu)
        RVlistMenu.adapter = RVlistMenuAdapter

        RVlistMenuAdapter.setOnItemClickListener(object : MenuAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {

                var thisPos = position
                if (filteredListMenu.isNotEmpty()) {
                    thisPos = listMenu.indexOf(filteredListMenu[thisPos])
                }

                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                val gson = Gson()
                intent.putExtra("MENU", gson.toJson(listMenu[thisPos]))
                startActivity(intent)
            }
        })
    }

    private fun resetFormTambah() {
        inpNama.text.clear()
        inpDeskripsi.text.clear()
        inpPoster.text.clear()
        inpHarga.text.clear()
        inpTags.text.clear()
        BStambahMenu.dismiss()
        posToEdit = -1
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupFragmentTambah() {
        VtambahMenu = layoutInflater.inflate(R.layout.tambah_item, null)
        BStambahMenu = BottomSheetDialog(this)
        BStambahMenu.setContentView(VtambahMenu)
        BStambahMenu.setCancelable(false)

        inpNama = VtambahMenu.findViewById(R.id.nama)
        inpDeskripsi = VtambahMenu.findViewById(R.id.deskripsi)
        inpPoster = VtambahMenu.findViewById(R.id.poster)
        inpHarga = VtambahMenu.findViewById(R.id.harga)
        inpTags = VtambahMenu.findViewById(R.id.tags)

        btnSimpan = VtambahMenu.findViewById(R.id.btn_simpan)
        btnTutup = VtambahMenu.findViewById(R.id.btn_tutup)
        btnTambah = findViewById(R.id.tambah_menu)

        btnTambah.setOnClickListener{
            BStambahMenu.show()
        }
        btnSimpan.setOnClickListener{
            var thisMode = "CREATE"

            if (posToEdit >= 0) {
                thisMode = "EDIT"
            }
            setMenu(thisMode, posToEdit)
        }
        btnTutup.setOnClickListener{
            resetFormTambah()
        }
    }
}