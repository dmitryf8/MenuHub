package com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository.FirebaseCommentRepository
import com.mcoolapp.menuhub.model.comments.Comment
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.post.CommunicationPartPost
import com.mcoolapp.menuhub.model.post.Post
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import com.mcoolapp.menuhub.repository.postrepository.PostRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class PostViewModel : ViewModel(), LifecycleOwner {
    private var currentUserID = MutableLiveData<String>()
    private var postId = MutableLiveData<String>()
    private var progressBarVisibility = MutableLiveData<Int>()

    private var post = MutableLiveData<Post>()
    private var communicationPartPost = MutableLiveData<CommunicationPartPost>()

    private var postOwnerName = MutableLiveData<String>()
    private var postOwnerID = MutableLiveData<String>()
    private var postImageWithBucket = MutableLiveData<ImageWithBucket?>()
    private var postDescription = MutableLiveData<String>()
    private var postCreateTimestamp = MutableLiveData<Timestamp>()
    private var postUpdateTimestamp = MutableLiveData<Timestamp>()
    private var cppLikedUserIDList = MutableLiveData<List<String>>()
    private var cppCommentIDList = MutableLiveData<List<String>>()

    private var likedUserIDListSize = MutableLiveData<String>()
    private var commentIDListSize = MutableLiveData<String>()

    private val userRepository = UserRepository()
    private val menuRepository = MenuRepository()
    private val postRepository = PostRepository()
    private val commentRepository = FirebaseCommentRepository()

    private lateinit var context: Context

    init {
        progressBarVisibility.value = View.GONE
        postId.value = ""


    }



    fun setContext(context: Context) {
        this.context = context
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

    fun getCommunicationPartPost(): MutableLiveData<CommunicationPartPost> {
        return communicationPartPost
    }

    fun getLikedIDListSize(): MutableLiveData<String> {

        return likedUserIDListSize
    }

    fun getCommentIDListSize(): MutableLiveData<String> {
        return commentIDListSize
    }

    fun getCreateTimestamp(): MutableLiveData<Timestamp> {
        return postCreateTimestamp
    }

    fun getUpdateTimestamp(): MutableLiveData<Timestamp> {
        return postUpdateTimestamp
    }

    fun getPostOwnerID(): MutableLiveData<String> {
        return postOwnerID
    }

    fun getCurrentUserID(): MutableLiveData<String> {
        return currentUserID
    }

    fun getPostID(): MutableLiveData<String> {
        return postId
    }

    fun getPost(): MutableLiveData<Post> {
        return post
    }

    fun getCppCommentIDList(): MutableLiveData<List<String>> {
        return cppCommentIDList
    }

    fun getPostOwnerName(): MutableLiveData<String> {
        return postOwnerName
    }

    fun getPostImageWithBucket(): MutableLiveData<ImageWithBucket?> {
        return postImageWithBucket
    }

    fun getPostDescription(): MutableLiveData<String> {
        return postDescription
    }

    fun getProgressBarVisibility(): MutableLiveData<Int> {
        return progressBarVisibility
    }
    //TODO продолжить с communicationParPost

    fun bind(post: Post?) {
        println("bind postOwnerID = " + post!!.ownerId)
        this.post.value = post
        postId.value = post.id
        postOwnerID.value = post.ownerId
        postOwnerName.value = post.ownerName
        postImageWithBucket.value = post.getImageWithBucket()
        postDescription.value = post.description
        postCreateTimestamp.value = post.createTimestamp
        postUpdateTimestamp.value = post.updateTimestamp


        postRepository.getCommunicationPartPostList(listOf(post.communicationPartID!!))
            .subscribeOn(Schedulers.io())
            .subscribe({
                communicationPartPost.value = it!!.get(0)
                cppLikedUserIDList.value = it.get(0).likedUserIdList
                cppCommentIDList.value = it.get(0).commentIdList
                commentIDListSize.value = it.get(0).commentIdList!!.size.toString()
                likedUserIDListSize.value = it.get(0).likedUserIdList!!.size.toString()
            }, {
                println("ERROR -> PostViewModel.bind -> postRepository.getCommunicationPartPost( " + post.communicationPartID + " ): " + it.message)
            })
            .addTo(disposable)
    }

    fun loadPost(id: String) {
        println("loadPost()")
        postRepository.getPost(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("loaded post " +it!!.id)
                bind(it)
            }, {
                println("ERROR -> PostViewModel.loadPost -> postRepository.getPost( " + id + " ): " + it.message)
            })
            .addTo(disposable)
    }

    fun addComment(comment: Comment): Observable<Boolean> {
        return Observable.create {emitter ->
        commentRepository.createComment(comment)
            .subscribeOn(Schedulers.io())
            .subscribe({
                //(getCppCommentIDList().value as ArrayList).add(it!!)
                (getCommunicationPartPost().value!!.commentIdList as ArrayList).add(it!!)
                postRepository.updateCommunicationPartPost(getCommunicationPartPost().value!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        emitter.onNext(true)
                        if (!emitter.isDisposed) emitter.onComplete()
                    }, {
                        println("ERROR -> PostViewModel.addComment -> post.updateCommunicationPartPost( " + getCommunicationPartPost().value!!.id + " ): " + it.message)
                        emitter.onError(it)
                        if (!emitter.isDisposed) emitter.onComplete()
                    })
                    .addTo(disposable)
            }, {
                println("ERROR -> PostViewModel.addComment -> post.commentRepository.createComment( " + comment.id + " ): " + it.message)
            })
            .addTo(disposable)
    }
    }

    fun savePost(): Observable<Boolean> {
        return Observable.create {emitter ->
            //progressBarVisibility.value = View.VISIBLE
            if (postId.value == "") {

                val post = Post()
                post.id = ""
                post.description = postDescription.value
                post.ownerId = postOwnerID.value
                post.ownerName = postOwnerName.value
                post.createTimestamp = Timestamp.now()
                post.updateTimestamp = Timestamp.now()
                post.photoId = postImageWithBucket.value!!.imageId

                userRepository.getUserName(FirebaseAuth.getInstance().uid.toString())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        post.ownerName = it
                        postRepository.createPost(post)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                println("postID -> "+ it)
                                post.id = it
                                var cpp = CommunicationPartPost()
                                cpp.ownerID = post.ownerId
                                cpp.createTimestamp = Timestamp.now()
                                cpp.updateTimestamp = Timestamp.now()
                                postRepository.createCommunicationPartPost(cpp)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        postRepository.addCPPIDtoPost(post.id, it)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({
                                                userRepository.addPostID(post.id)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        emitter.onNext(it)
                                                        //progressBarVisibility.value = View.INVISIBLE
                                                        if (!emitter.isDisposed) emitter.onComplete()
                                                        if (it )
                                                            println("postID added to userPostIDList")

                                                        else
                                                            println("postID NOT added to userPostIDList")
                                                    }, {
                                                        //progressBarVisibility.value = View.INVISIBLE
                                                        emitter.onError(it)
                                                        if (!emitter.isDisposed) emitter.onComplete()
                                                        println("error PostViewModel savePost().createPost(" + post.id + ").createCommunicationPartPost().addCPPIDtoPostID() userRepository.addPostID -> " + it.stackTrace.toString())

                                                    })
                                                    .addTo(disposable)
                                            }, {
                                                //progressBarVisibility.value = View.INVISIBLE
                                                println("error PostViewModel savePost().createPost(" + post.id + ").createCommunicationPartPost().addCPPIDtoPostID() -> " + it.stackTrace.toString())

                                            })
                                            .addTo(disposable)
                                    }, {
                                        //progressBarVisibility.value = View.INVISIBLE
                                        println("error PostViewModel savePost().createPost(" + post.id + ").createCommunicationPartPost() -> " + it.message)
                                    })
                                    .addTo(disposable)
                            }, {
                                //progressBarVisibility.value = View.INVISIBLE
                                println("error PostViewModel savePost().createPost( " + post.id + " ) -> " + it.message.toString())
                            })
                            .addTo(disposable)
                    }, {
                        //progressBarVisibility.value = View.INVISIBLE
                    })
                    .addTo(disposable)

            } else {
                var post = Post()
                post.id = postId.value!!
                post.description = postDescription.value
                post.ownerId = postOwnerID.value
                post.ownerName = postOwnerName.value
                post.updateTimestamp = Timestamp.now()
                post.createTimestamp = postCreateTimestamp.value
                post.photoId = postImageWithBucket.value!!.imageId
                postRepository.updatePost(post)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        //progressBarVisibility.value = View.INVISIBLE
                        if (it) println("post was updated") else println("post was not updated")
                    }, {
                        //progressBarVisibility.value = View.INVISIBLE
                        println("error PostViewModel savePost().updatePost(" + post.id + ") -> " + it.message)
                    })
                    .addTo(disposable)
            }
        }

    }

    fun setProgressbarVisible() {
        progressBarVisibility.value = View.VISIBLE
    }

    fun setProgressbarInvisible() {
        progressBarVisibility.value = View.INVISIBLE
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    fun setPostImageId(id: String) {

        postOwnerID.value = FirebaseAuth.getInstance().currentUser!!.uid
        postImageWithBucket.value = ImageWithBucket(id, "bucket" + postOwnerID.value)

        println("bucketName in fun setPostImageId (id: String) -------------->" + "bucket" + postOwnerID.value)
        userRepository.setBaseContext(context)
        userRepository.getUserName(id)
            .subscribeOn(Schedulers.io())
            .subscribe({
                println("postOwnerName ------> " + it)
                postOwnerName.value = it
            }, {
                println("error in postViewModel setPostImageID getUserName" + it)
            })
            .addTo(disposable)
        postImageWithBucket.value = ImageWithBucket(id, "bucket" + postOwnerID.value)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}