package com.mcoolapp.menuhub.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.repository.ImageRepository
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import com.squareup.picasso.RequestHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_qr_code_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRCodeScannerFragment : Fragment(), ZXingScannerView.ResultHandler {

    //var qrCodeScanner = ZXingScannerView(requireContext())
    private val menuRepository = MenuRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate in QRCodeScanner")
        super.onCreate(savedInstanceState)
        menuRepository.setBaseContext(requireContext())

        (activity as MainActivity).setRightButtonInvisible()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("onCreateView in QRCodeScanner")
        return inflater.inflate(R.layout.fragment_qr_code_scanner, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated qrscannerfragment")
        //qrCodeScanner = view.findViewById<ZXingScannerView>(R.id.qrCodeScanner)
        setScannerProperties()


    }

    override fun onResume() {
        super.onResume()
        println("onResume qrscannerfragment")
        (activity as MainActivity).setTitleText(getString(R.string.scan_code))
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    private fun setScannerProperties() {
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        qrCodeScanner.setMaskColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QRCodeScannerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun handleResult(p0: Result?) {
        if (p0 != null) {
            val text = p0.text
            val wordList = text.split(" ")
            var menuId = ""
            println("wordList[0] = " + wordList[0])
            println("wordList[1] = " + wordList[1])
            if (wordList[0] == "com.mcoolapp.menuhub") {
                menuRepository.getTable(wordList[1])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        println("after scan table_> " + it!!.toHashMapForUpdate())
                        (activity as MainActivity).setTitleText(it!!.tableName)
                        menuId = it.menuID
                        println("after scanning menuID = " + menuId)

                        tableNameLayoutInQRCodeScannerFragment.visibility = View.VISIBLE
                        println("after scanning visibility = " + tableNameLayoutInQRCodeScannerFragment.visibility)
                        openMenuButtonInQRCodeScannerFragment.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString(MenuFragment.MENU_ID, menuId)
                            findNavController().navigate(
                                R.id.action_QRCodeScannerFragment2_to_menuFragment,
                                bundle
                            )
                        }
                    }, {
                        println("Unable to access the table")
                    })
                    .addTo(disposable)
            } else {
                println("Is not MenuHub Code")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        tableNameLayoutInQRCodeScannerFragment.visibility = View.INVISIBLE
        qrCodeScanner.stopCamera()

    }


}