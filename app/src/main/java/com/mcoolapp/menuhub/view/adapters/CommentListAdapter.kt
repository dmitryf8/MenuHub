package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.CommentLayoutBinding
import com.mcoolapp.menuhub.databinding.PostLayoutBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository.FirebaseCommentRepository
import com.mcoolapp.menuhub.model.comments.Comment
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.post.CommunicationPartPost
import com.mcoolapp.menuhub.model.post.Post
import com.mcoolapp.menuhub.model.post.PostIDWithCPPID
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import com.mcoolapp.menuhub.view.adapters.PostListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CommentListAdapter(
    private var commentIDList: List<String?>,
    private val firebasePostRepository: FirebasePostRepository,
    private val userRepository: UserRepository,
    private val firebaseCommentRepository: FirebaseCommentRepository,
    private val navController: NavController
) :
    RecyclerView.Adapter<CommentListAdapter.CommentListViewHolder>() {
    private val disposable = CompositeDisposable()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder {
        println("onCreateViewHolder commentIDList.size " + commentIDList.size)
        val inflater = LayoutInflater.from(parent.context)
        val binding = CommentLayoutBinding.inflate(inflater)
        return CommentListViewHolder(
            binding,
            disposable,
            firebasePostRepository,
            userRepository,
            firebaseCommentRepository,
            navController
        )
    }

    override fun getItemCount(): Int {
        return commentIDList.size
    }

    override fun onBindViewHolder(holder: CommentListViewHolder, position: Int) {
        holder.bind(commentIDList.get(position))
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable.clear()
    }

    class CommentListViewHolder(
        private val binding: CommentLayoutBinding,
        private val disposable: CompositeDisposable,
        private val firebasePostRepository: FirebasePostRepository,
        private val userRepository: UserRepository,
        private val firebaseCommentRepository: FirebaseCommentRepository,
        private val navController: NavController
    ) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {


        fun bind(item: String?) {
            println("bind comment with id  " + item!!)
            val commentID = item

            firebaseCommentRepository.getComment(commentID)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    binding.comment = it!!

                    binding.likeImageViewCL.setOnClickListener {
                        if (binding.comment?.likedIDList!!.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                            (binding.comment?.likedIDList as ArrayList).remove(FirebaseAuth.getInstance().currentUser!!.uid)
                            binding.likeImageViewCL.setImageDrawable(binding.likeImageViewCL.context.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                        } else {
                            (binding.comment?.likedIDList as ArrayList).add(FirebaseAuth.getInstance().currentUser!!.uid)
                            binding.likeImageViewCL.setImageDrawable(binding.likeImageViewCL.context.getDrawable(R.drawable.ic_baseline_favorite_24))
                        }
                        firebaseCommentRepository.updateComment(binding.comment!!)
                            .subscribeOn(Schedulers.io())
                            .subscribe({

                            }, {

                            })
                            .addTo(disposable)
                    }

                    userRepository.getUser(it.commentAuthorID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                                   binding.imageWithBucket = ImageWithBucket(it!!.userPhotoId, "bucket" + it.id)
                        }, {

                        })
                        .addTo(disposable)

                }, {
                    println("error in firebaseCommentRepository.getComment(commentID) - > " + it.message)
                })
                .addTo(disposable)

        }

        fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
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
