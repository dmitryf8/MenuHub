package com.mcoolapp.menuhub.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.ActivityEditUserBinding
import com.mcoolapp.menuhub.fragments.UserDetailFragment
import com.mcoolapp.menuhub.repository.ImageRepository
import com.mcoolapp.menuhub.utils.isConnectedToNetwork
import com.mcoolapp.menuhub.viewmodel.UserViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_user.*

const val PERMISSION_REQUEST = 0


class UserEditActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
                UserViewModel.UserViewModelListener{

    private lateinit var viewModel: UserViewModel
    private lateinit var binding: ActivityEditUserBinding
    var userId: String = ""
    companion object {
        val PICK_IMAGE_REQUEST = 332
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()
        val intentID = intent.getStringExtra(UserDetailFragment.USER_ID_KEY)

        System.out.println("intentID = " + intentID)

        userId = FirebaseAuth.getInstance().currentUser!!.uid

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user)

        if (isConnectedToNetwork()) {
            System.out.println("Internet connection alive")
        } else {
            System.out.println("Internet connection dead")
        }

        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        viewModel.setBaseContext(this)

        System.out.println("111111111111111111111111111111111111")
        binding.setLifecycleOwner(this)
        System.out.println("2222222222222222222222222222222222222")
        binding.viewmodel = viewModel

        if (FirebaseAuth.getInstance().currentUser!!.uid == intentID)
        viewModel.loadUser(intentID)
        viewModel.setOnUserViewModelListener(this)

        saveUserDataButton.setOnClickListener {
            viewModel.setBaseContext(this.applicationContext)
            viewModel.saveUser()
        }



    }



    override fun onResume() {
        super.onResume()
        System.out.println("UserEditActivity onResume")

    }

    private fun startEmailPasswordActivity() {
        val intent: Intent = Intent(this, EmailPasswordActivity::class.java)
        startActivity(intent)
    }

    private val disposable = CompositeDisposable()

    private fun CompositeDisposable.addTo(disposable: CompositeDisposable) {
        disposable.add(this)
    }


    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private fun requestPermission() {

        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                PERMISSION_REQUEST
            )

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        System.out.println("onActivityREsult")


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            System.out.println("\"bucket\" + viewModel.getUserID().value!! = " + "bucket" + viewModel.getUserID().value!!)
            imageIdFromUri(data.data!!, "bucket" + viewModel.getUserID().value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    println("viewModel.newUserPhotoId(it!!)")
                    viewModel.newUserPhotoId(it!!)
                }, {
                    System.out.println("imageIdFromUri(data.data!!, \"bucket\" + viewModel.getUserID().value!!) ERROR!! " + it)
                })

        }
    }

    fun imageIdFromUri(uri: Uri, bucketId: String): Observable<String?> {
        return Observable.create { emitter ->
            val imageRepository = ImageRepository(this)
            viewModel.changeProgressbarVisiblity(View.VISIBLE)
            imageRepository.saveImage(uri, bucketId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    viewModel.changeProgressbarVisiblity(View.INVISIBLE)
                })

        }
    }

    fun chooseUserImage(v: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("image/*")
        ActivityCompat.startActivityForResult(this, intent, PICK_IMAGE_REQUEST, null)
    }

    override fun onSavingUserDataEnd() {
        this.finish()
    }



}
