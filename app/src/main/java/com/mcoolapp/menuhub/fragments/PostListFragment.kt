package com.mcoolapp.menuhub.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentMenuBinding
import com.mcoolapp.menuhub.databinding.FragmentMenuEditBinding
import com.mcoolapp.menuhub.databinding.FragmentPostListBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository.FirebaseCommentRepository
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MenuViewModel
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.view.adapters.PostListAdapter
import com.mcoolapp.menuhub.viewmodel.PostListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_menu.*
import java.util.*
import kotlin.collections.ArrayList

class PostListFragment : Fragment() {


    private val postListViewModel by viewModels<PostListViewModel>()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var postIDList: ArrayList<String>
    private val firebasePostRepository = FirebasePostRepository()
    private val userRepository = UserRepository()
    private val firebaseCommentRepository = FirebaseCommentRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).setTitleText(getString(R.string.news))
        arguments?.let {
            postIDList = it.getStringArrayList(POST_LIST)!!
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPostListBinding.inflate(layoutInflater).apply {
            plViewModel = postListViewModel
            lifecycleOwner = this@PostListFragment
        }
        return binding.root
    }

    companion object {
        private const val POST_LIST = "jrjskejnfkrbg8h2i984hfkw"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostListFragment().apply {
            }
    }

    fun getLastUpdateTime(context: Context): Long {
        val pref: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val str = pref.getLong("lastUpdateTimestamp", 0)
        return str
    }

    fun setLastUpdateTime(timestamp: com.google.firebase.Timestamp, context: Context) {
        val pref: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val edt = pref.edit()
        edt.putLong("lastUpdateTimestamp", timestamp.seconds)
        edt.apply()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = Date(888883)

        //TODO Загружать именно postIDList, а не наблюдаемых пользователей
        postListViewModel.loadObservableUsersPostList(
            FirebaseAuth.getInstance().currentUser!!.uid,
            Timestamp(date)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("it.size in PostListFragment -> " + it.size)
                binding.postListRecyclerView.setHasFixedSize(true)
                binding.postListRecyclerView.layoutManager =
                    GridLayoutManager(context, 1)
                binding.postListRecyclerView.isNestedScrollingEnabled = true
                val adapter = PostListAdapter(
                    it,
                    firebasePostRepository,
                    userRepository,
                    firebaseCommentRepository,
                    findNavController()
                )
                binding.postListRecyclerView.adapter = adapter


            }, {
                println("Error inPostListFragment : " + it.message)
            })
            .addTo(disposable)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}