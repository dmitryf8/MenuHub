package com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentPostListBinding
import com.mcoolapp.menuhub.databinding.MenuItemsRecyclerViewBinding
import com.mcoolapp.menuhub.databinding.PostLayoutBinding
import com.mcoolapp.menuhub.fragments.PostWithCommentsListFragment
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository.FirebaseCommentRepository
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.CommentListAdapter
import com.mcoolapp.menuhub.model.comments.Comment
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.post.PostWithCPP
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.model.post.CommunicationPartPost
import com.mcoolapp.menuhub.model.post.Post
import com.mcoolapp.menuhub.model.post.PostIDWithCPPID
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import com.mcoolapp.menuhub.repository.userrepository.FirebaseUserRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PostListAdapter(
    private var postIDWithCPPIDList: List<PostIDWithCPPID?>,
    private val firebasePostRepository: FirebasePostRepository,
    private val userRepository: UserRepository,
    private val firebaseCommentRepository: FirebaseCommentRepository,
    private val navController: NavController
) :
    RecyclerView.Adapter<PostListAdapter.PostListViewHolder>() {
    private val disposable = CompositeDisposable()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        println("onCreateViewHolder postWithCPPList.size " + postIDWithCPPIDList.size)
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostLayoutBinding.inflate(inflater)
        return PostListViewHolder(binding, disposable, firebasePostRepository, userRepository, firebaseCommentRepository, navController)
    }

    override fun getItemCount(): Int {
        return postIDWithCPPIDList.size
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        holder.bind(postIDWithCPPIDList.get(position))
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable.clear()
    }

    class PostListViewHolder(
        private val binding: PostLayoutBinding,
        private val disposable: CompositeDisposable,
        private val firebasePostRepository: FirebasePostRepository,
        private val userRepository: UserRepository,
        private val firebaseCommentRepository: FirebaseCommentRepository,
        private val navController: NavController
    ) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {


        fun bind(item: PostIDWithCPPID?) {
            println("bind post with id  " + item!!.postID)
            val postID = item.postID
            var post = Post()
            val cppID = item.cppID
            var cpp = CommunicationPartPost()


            binding.commentsIV.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(PostWithCommentsListFragment.POST_ID, postID)
                navController.navigate(R.id.action_postListFragment_to_postWithCommentsListFragment, bundle)
            }

            firebasePostRepository.getPost(postID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    post = it!!

                    userRepository.getUser(post.ownerId!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            binding.imageWithBucket = ImageWithBucket(
                                it!!.userPhotoId,
                                "bucket" + it.id
                            )
                        }, {
                            println("error in PostListAdapter userRepository.getUser(post.ownerId!!) -> " + it.message)
                        })
                        .addTo(disposable)
                    firebasePostRepository.getCommunicationPartPost(cppID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            cpp = it!!

                            binding.post = post
                            binding.cpp = cpp
                            binding.currentuid = FirebaseAuth.getInstance().currentUser!!.uid

                            binding.likesIV.setOnClickListener {
                                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

                                if (cpp.likedUserIdList!!.contains(currentUserID)) {
                                    (cpp.likedUserIdList as ArrayList<String>).remove(currentUserID)
                                    println("uid not contains")
                                    binding.likesIV.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp)
                                    binding.numberOfLikes.setText(cpp.likedUserIdList!!.size.toString())
                                    updateCPP(cpp)
                                } else {
                                    (cpp.likedUserIdList as ArrayList<String>).add(currentUserID)
                                    println("uid contains")
                                    binding.likesIV.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp)
                                    binding.numberOfLikes.setText(cpp.likedUserIdList!!.size.toString())
                                    updateCPP(cpp)
                                }
                            }

                        }, {
                            println("error in postListViewHolder-> " + it)
                        })
                        .addTo(disposable)
                }, {
                    println("error in postListViewHolder-> " + it)
                })
                .addTo(disposable)
            binding.addCommentButton.setOnClickListener {
                userRepository.getUser(FirebaseAuth.getInstance().currentUser!!.uid)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if (binding.editTextComment.text.toString().length > 0) {
                            val comment = Comment()
                            comment.postID = post.id
                            comment.commentAuthorID = it!!.id
                            comment.commentAuthorName = it.userName
                            comment.commentText = binding.editTextComment.text.toString()
                            comment.createTimestamp = Timestamp.now()
                            comment.updateTimestamp = Timestamp.now()

                            println(comment.toHashMapForCreate())

                            firebaseCommentRepository.createComment(comment)
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    (cpp.commentIdList as ArrayList).add(it!!)
                                    cpp.updateTimestamp = Timestamp.now()
                                    firebasePostRepository.updateCommunicationPartPost(cpp)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe({
                                                   if (it!!) binding.editTextComment.text.clear()
                                            binding.invalidateAll()
                                        }, {
                                            println("error in postListViewHolder firebasePostRepository.updateCommunicationPartPost(cpp) -> " + it.message)

                                        })
                                        .addTo(disposable)
                                }, {
                                    println("error in postListViewHolder firebaseCommentRepository.createComment(comment) -> " + it.message)
                                })
                                .addTo(disposable)
                        }

                    }, {
                        println("Error in PostListAdapter userRepository.getUser(FirebaseAuth.getInstance().currentUser!!.uid) -> " + it.message)
                    })
                    .addTo(disposable)

            }
        }

        private fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
            compositeDisposable.add(this)
        }

        private fun updateCPP(cpp: CommunicationPartPost) {
            firebasePostRepository.updateCommunicationPartPost(cpp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it!!) {
                        //TODO
                    }
                }, {
                    println("error in PostListViewHolder.bind -> " + it.message)
                })
                .addTo(disposable)
        }

    }

}
