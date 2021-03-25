package com.mcoolapp.menuhub.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentPostWithCommentsListBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository.FirebaseCommentRepository
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.CommentListAdapter
import com.mcoolapp.menuhub.model.comments.Comment
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.viewmodel.PostViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostWithCommentsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostWithCommentsListFragment : Fragment() {
    private var postID: String? = null

    private val postViewModel by viewModels<PostViewModel>()
    private val firebasePostRepository = FirebasePostRepository()
    private val userRepository = UserRepository()
    private val firebaseCommentRepository = FirebaseCommentRepository()
    private lateinit var binding: FragmentPostWithCommentsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).setRightButtonInvisible()
        arguments?.let {
            if (it.containsKey(POST_ID)) {
                postID = it.getString(POST_ID)!!
                println("postID = " + postID)

            } else {

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPostWithCommentsListBinding.inflate(layoutInflater).apply {
            //viewmodel = menuItemViewModel
            lifecycleOwner = this@PostWithCommentsListFragment
        }

        if (postID != null) {
            postViewModel.loadPost(postID!!)
        }
        postViewModel.getPostImageWithBucket().observeForever {
            binding.postImageWithBucket = it!!
        }

        postViewModel.getPost().observeForever {
            binding.post = it!!
            (activity as MainActivity).setTitleText(it.ownerName!!)
        }

        postViewModel.getCommunicationPartPost().observeForever {
            binding.cpp = it!!
        }

        postViewModel.getCppCommentIDList().observeForever {
            binding.numberOfCommentsTextViewPWC.text = it.size.toString()
            binding.commentsListRecyclerViewPCW.setHasFixedSize(false)
            binding.commentsListRecyclerViewPCW.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            binding.commentsListRecyclerViewPCW.isNestedScrollingEnabled = false
            binding.commentsListRecyclerViewPCW.adapter = CommentListAdapter(
                it!!,
                firebasePostRepository,
                userRepository,
                firebaseCommentRepository,
                findNavController()
            )
        }

        binding.sendAnswerImageView.setOnClickListener {
            val comment = Comment()

            comment.commentAuthorID = FirebaseAuth.getInstance().currentUser!!.uid
            comment.createTimestamp = Timestamp.now()
            comment.updateTimestamp = Timestamp.now()
            comment.commentText = binding.myCommentEditText.text.toString()
            comment.postID = postID!!

            userRepository.getUser(FirebaseAuth.getInstance().currentUser!!.uid)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    comment.commentAuthorName = it!!.userName
                    postViewModel.addComment(comment)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it) {

                                binding.myCommentEditText.text.clear()

                                val imm =
                                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(
                                    binding.myCommentEditText.windowToken,
                                    0
                                )
                                Toast.makeText(requireContext(), R.string.comment_added, Toast.LENGTH_LONG).show()
                                binding.commentsListRecyclerViewPCW.invalidate()

                                binding.postScrollView.fullScroll(binding.postScrollView.maxScrollAmount)
                                val bottom: Int =
                                    binding.commentsListRecyclerViewPCW.getAdapter()!!.getItemCount() - 1
                                binding.commentsListRecyclerViewPCW.smoothScrollToPosition(bottom)

                            }
                        }, {

                        })
                        .addTo(disposable)
                }, {

                })
                .addTo(disposable)


        }
        return binding.root
    }

    fun View.hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0) }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (postID != null) {

        } else {
        }


    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    companion object {
        const val POST_ID = "fogjdiogj4e98u239t94gj0e3tjwjogjoso3ut8"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostWithCommentsListFragment.apply {

            }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

}