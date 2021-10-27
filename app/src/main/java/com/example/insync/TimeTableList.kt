package com.example.insync

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insync.model.Event
import com.example.insync.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.example.insync.MainActivity.Companion.gUser


class TimeTableList : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    lateinit var s1: MutableList<String>
    lateinit var s2: MutableList<String>
    lateinit var urlLinks: MutableList<String>

    var images: Array<Int> = arrayOf(
        R.mipmap.ic_launcher,
        R.mipmap.ic_launcher,
        R.mipmap.ic_launcher,
        R.mipmap.ic_launcher,
        R.mipmap.ic_launcher
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table_list)

        recyclerView = findViewById(R.id.recyclerView)

        s1 = resources.getStringArray(R.array.subject_name).toMutableList()
        s2 = resources.getStringArray(R.array.lecture_timing).toMutableList()
        urlLinks = mutableListOf()
        for (i in 0..5) {
            urlLinks.add("https://www.google.com/")
        }


//        retrieveDataForTeacher(gUser, "Monday")

        val myRecyclerAdapter: MyRecyclerAdapter =
            MyRecyclerAdapter(applicationContext, s1, s2, images)
        recyclerView.adapter = myRecyclerAdapter
        myRecyclerAdapter.setOnItemClickListener(object : MyRecyclerAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(applicationContext, s1[position], Toast.LENGTH_SHORT).show()
                val url = urlLinks[position]
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        })
        recyclerView.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topmenu, menu)
        return true
    }

    fun CreateNewEvent(view: android.view.View) {


        val intent = Intent(applicationContext, AddEventActivity::class.java)
        val prev_intent = getIntent()
        var insyncUserArray = prev_intent.getStringArrayListExtra("insyncUser")!!

        intent.putStringArrayListExtra("insyncUser", insyncUserArray)
        //for debugging
        Toast.makeText(this, insyncUserArray[1], Toast.LENGTH_SHORT).show()

        startActivity(intent)
    }

    // function to retrieve data for teacher
    fun retrieveDataForTeacher(insyncUser: User, day: String) {

        val dayToday = day
        var teacherDataForToday =
            FirebaseFirestore.getInstance().collection("teacherDB").document(insyncUser.uid)
                .collection("weekday").document(dayToday).collection("events").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result!!
                        displayReceivedData(result)
                    } else {

                    }
                }
    }

    private fun displayReceivedData(data: QuerySnapshot) {
        val eventArray = arrayListOf<Event>()
        for (i in data) {
            eventArray.add(Event(i))
        }
        s1.clear()
        s2.clear()
        urlLinks.clear()
        for (i in 0..5) {
            s1[i] = eventArray[i].name
            s2[i] = eventArray[i].startAt
            urlLinks[i] = eventArray[i].lectureLink
        }
    }

    fun homeIntentFun(item: android.view.MenuItem) {
        val intent = Intent(applicationContext, TimeTableList::class.java)
        startActivity(intent)
    }

    fun scheduleIntentFun(item: android.view.MenuItem) {
        val intent = Intent(applicationContext, ScheduleActivity::class.java)
        startActivity(intent)
    }

    fun accountIntentFun(item: android.view.MenuItem) {
        val intent = Intent(applicationContext, TimeTableList::class.java)
        startActivity(intent)
    }

    fun logoutIntentFun(item: android.view.MenuItem) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

}