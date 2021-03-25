package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.repository.ImageRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_image.*


class ImageFragment : Fragment() {

    private lateinit var imageWithBucket: ImageWithBucket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(TableEditFragment.IMAGE_ID) && it.containsKey(TableEditFragment.BUCKET_NAME))
                imageWithBucket = ImageWithBucket(it.getString(TableEditFragment.IMAGE_ID), it.getString(TableEditFragment.BUCKET_NAME)!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).setRightButtonInvisible()
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageRepository = ImageRepository(requireContext())

        imageRepository.getImageFromBucket(imageWithBucket)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                imageFragmentImageView.setImageBitmap(it)
            }, {
                println("error in ImageFragment -> " + it.message)
            }).addTo(disposable)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}