package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.ActivityMainBinding
import com.mcoolapp.menuhub.fragments.UserDetailFragment
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MainViewModel
import com.mcoolapp.menuhub.repository.ImageRepository
import com.mcoolapp.menuhub.services.MHubFCMService
import com.mcoolapp.menuhub.view.PERMISSION_REQUEST
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    LifecycleOwner,
    MainViewModel.MainViewModelListener {

    private lateinit var binding: ActivityMainBinding
    val mainViewModel by viewModels<MainViewModel>()

    lateinit var navController: NavController

    companion object {
        val MENU_ITEM_IMAGE_REQUEST = 644
        val POST_IMAGE_REQUEST = 637
        val SHARED_PREFERENCE = "mnbjhghjfrtyhgvhgv"
    }

    var userId = ""

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra(MHubFCMService.NOTIFICATION_MARKER)) {
            println(
                "we have a NOTIFICATION_MARKER with id -> " + intent.getStringExtra(
                    MHubFCMService.NOTIFICATION_MARKER
                )
            )
        } else {
            println("we dont have NOTIFICATON_MARKER")
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }
        setContentView(binding.root)

        binding.setLifecycleOwner(this)
        binding.mainViewModel = mainViewModel

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        mainViewModel.setContext(this)
        val sharedPreference =
            getSharedPreferences(MainActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        if (sharedPreference.contains("fcmToken")) {

            val token = sharedPreference.getString("fcmToken", "")

            println("fcmToken in SharedPreferences -> " + token)
            if (!token.equals("")) {
                mainViewModel.sendRegistrationToServer(token!!)
            }
        } else {
            println("-------------------we dont have fcmToken here===================------")
        }

        navController = findNavController(this, R.id.nav_host_fragment)

        if (FirebaseAuth.getInstance().currentUser != null) {
            userId = FirebaseAuth.getInstance().currentUser!!.uid
            navController.navigate(R.id.postListFragment)
        } else {

        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {

        }

        /**  readQRTextView.setOnClickListener {
        navView.visibility = View.GONE
        navController.navigate(R.id.QRCodeScannerFragment2)
        }
         **/
        binding.cartImageView.setOnClickListener {

        }

        binding.chatsImageView.setOnClickListener {
            navController.navigate(R.id.chatListFragment)
        }


        binding.bottomNavView.setupWithNavController(navController)
        navController.navigate(R.id.postListFragment)

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener when (it.itemId) {
                R.id.searchTab -> {

                    true
                }
                R.id.homeTab -> {
                    navController.navigate(R.id.postListFragment)
                    true
                }
                R.id.addPostTab -> {
                    navController.navigate(R.id.editPostFragment)
                    true
                }
                R.id.profileTab -> {
                    val bundle = Bundle()
                    bundle.putString(UserDetailFragment.USER_ID_KEY, userId)
                    navController.navigate(R.id.userDetailFragment4, bundle)
                    true
                }
                R.id.qrCodeScanerTab -> {
                    navController.navigate(R.id.QRCodeScannerFragment2)
                    true
                }
                else -> false
            }
        }
    }

    fun setCartButtonVisible() {
        mainViewModel.setCartButtonVisible()
    }

    fun setChatButtonVisible() {
        mainViewModel.setChatsButtonVisible()
    }

    fun setRightButtonInvisible() {
        mainViewModel.setRightButtonInvisible()
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onShowUser(id: String?) {
        val bundle = Bundle()
        bundle.putString(UserDetailFragment.USER_ID_KEY, id)
        navController.navigate(R.id.userDetailFragment4, bundle)
        System.out.println("onShowUser( " + id + " )")

    }

    fun onClick() {
        System.out.println("click")
    }

    fun setTitleText(s: String) {
        header_title.text = s
    }

    fun chooseMenuItemImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.setType("image/*")
        ActivityCompat.startActivityForResult(this, intent, MENU_ITEM_IMAGE_REQUEST, null)
    }

    fun choosePostImage() {
        println("choosePostImage start")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.setType("image/*")
        ActivityCompat.startActivityForResult(this, intent, POST_IMAGE_REQUEST, null)
        println("choosePostImage end")
    }


    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        System.out.println("onActivityREsult")

        userId = FirebaseAuth.getInstance().currentUser!!.uid

        println("-----------------------USER_ID in MainActivity -------------------> " + userId)

        if (resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            if (requestCode == MENU_ITEM_IMAGE_REQUEST) {
                imageIdFromUri(data.data!!, "bucket" + userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        imageIdListener!!.imageID(it!!)
                    }, {
                        println("error in onActivityResult(menuItem image request) - > " + it.stackTrace!!.contentToString())
                    })
                    .addTo(disposable)
            } else {
                if (requestCode == POST_IMAGE_REQUEST) {
                    imageIdFromUri(data.data!!, "bucket" + userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            postImageIDListener!!.imageID(it!!)
                        }, {
                            println("error in onActivityResult(post image request) - > " + it.stackTrace!!.contentToString())
                        })
                        .addTo(disposable)
                }
            }


        }
    }

    fun imageIdFromUri(uri: Uri, bucketId: String): Observable<String?> {
        return Observable.create { emitter ->
            val imageRepository = ImageRepository(this)
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
                    emitter.onComplete()
                })

        }
    }

    interface MenuItemImageIDListener {
        fun imageID(id: String)
    }

    interface PostEditImageIDListener {
        fun imageID(id: String)
    }


    var imageIdListener: MenuItemImageIDListener? = null
    var postImageIDListener: PostEditImageIDListener? = null

    interface OnChatCommandListener {
        fun onActivityCreated()
    }

    var onChatCommandListener: OnChatCommandListener? = null

    fun setOCCListener(listener: OnChatCommandListener) {
        onChatCommandListener = listener
    }
}