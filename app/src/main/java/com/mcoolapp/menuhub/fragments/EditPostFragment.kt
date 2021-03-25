package com.mcoolapp.menuhub.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentEditPostBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.viewmodel.PostViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditPostFragment : Fragment(), MainActivity.PostEditImageIDListener{
    private var postID: String? = null
    private var communicationPartID: String? = null

    private val postViewModel by viewModels<PostViewModel>()
    private lateinit var binding: FragmentEditPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).setRightButtonInvisible()
        arguments?.let {
            if (it.containsKey(POST_ID)){
                postID = it.getString(POST_ID)!!
                println("incoming postID = "+ postID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //postViewModel.setContext(activity?.baseContext!!)

        println("EditPostFragment onCreateView")

        binding = FragmentEditPostBinding.inflate(layoutInflater).apply {
            viewmodel = postViewModel
            lifecycleOwner = this@EditPostFragment
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("EditPostFragment onViewCreated")
        (activity as MainActivity).setTitleText(requireContext().getString(R.string.edit_post))
        (activity as MainActivity).postImageIDListener = this
        postViewModel.setContext(requireContext())

        if (postID != null) {
            println("postID != null")
            postViewModel.loadPost(postID!!)

        } else {
            println("postID == null")
        }

        binding.postImageView.setOnClickListener {
            println("on click listener start")
            (activity as MainActivity).choosePostImage()
            println("on click listener end")
        }

        binding.savePostButton.setOnClickListener {

            postViewModel.setProgressbarVisible()
            postViewModel.savePost()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    postViewModel.setProgressbarInvisible()
                           if (it) {
                               findNavController().navigateUp()
                               Toast.makeText(requireContext(), "Post was saved", Toast.LENGTH_LONG).show()
                           } else
                               Toast.makeText(requireContext(), "Please, try one time save", Toast.LENGTH_LONG).show()
                }, {
                    println("Error -> " + it.message)
                    Toast.makeText(requireContext(), "!!!! Post was not saved", Toast.LENGTH_LONG).show()
                    postViewModel.setProgressbarInvisible()
                    findNavController().navigateUp()
                })
                .addTo(disposable)
        }

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    companion object {
        public const val POST_ID = "jgnvdjifsfgrsdlsdmzc,vnu83249"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditPostFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun imageID(id: String) {
        postViewModel.setPostImageId(id)
    }
}

