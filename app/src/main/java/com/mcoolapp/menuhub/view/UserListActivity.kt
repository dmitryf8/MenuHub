package com.mcoolapp.menuhub.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.view.adapters.UserListItemsAdapter
import com.mcoolapp.menuhub.repository.userrepository.FirebaseUserRepository
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity(), UserListItemsAdapter.ClickListener {
    override fun onItemClicked(item: User) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra("ID", item.id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        userListRecyclerView.setHasFixedSize(true)
        userListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        userListRecyclerView.isNestedScrollingEnabled = false
        System.out.println("UserListActivity : AppCompatActivity()")
    }
}
