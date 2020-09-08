package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.menu.Table
import com.mcoolapp.menuhub.repository.ImageRepository
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_table_edit.*

class TableEditFragment : Fragment() {

    private var tableID: String = ""
    private var menuID: String = ""
    private val menuRepository = MenuRepository()
    private lateinit var imageRepository: ImageRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuRepository.setBaseContext(requireContext())
        (activity as MainActivity).setTitleText(getString(R.string.edit_table))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table_edit, container, false)
    }

    companion object {
        const val TABLE_ID = "fklsdglbkjjjddddllflfnndsdfjfhcffdfg"
        const val IMAGE_ID = "aesifbmncn,xzz.zkjfhfhd"
        const val BUCKET_NAME = "sjdhfwoeirukcjvcmvndkjfwoei384"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TableEditFragment().apply {
                arguments = Bundle().apply {


                }
            }
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.containsKey(TABLE_ID)){
                tableID = it.getString(TABLE_ID, "")
                println("tableID = " + tableID)
                menuRepository.getTable(tableID)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        tableNameEditText.setText(it!!.tableName)
                    }, {
                        println("error in getTable -> " + it.message)
                    })
                    .addTo(disposable)
            }
            if (it.containsKey(MenuFragment.MENU_ID)) {
                menuID = it.getString(MenuFragment.MENU_ID, "")
                println("arguments content menuId = " + menuID)
            }

        }
        saveTableButton.setOnClickListener {
            println("menuID in onViewCreated = " + menuID)
            if (tableID == "") {
                val table = Table()
                table.ownerId = FirebaseAuth.getInstance().currentUser!!.uid
                table.tableName = tableNameEditText.text.toString()
                table.menuID = menuID
                menuRepository.createTable(table)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        table.id = it
                        menuRepository.updateTable(table)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                imageRepository = ImageRepository(requireContext())
                                if (it!!) imageRepository.generateQRCode(table)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        table.qrCodeImageId = it
                                        menuRepository.updateTable(table)
                                            .subscribeOn(Schedulers.io())
                                            .subscribe({
                                                menuRepository.getMenu(menuID)
                                                    .subscribeOn(Schedulers.io())
                                                    .subscribe({
                                                        it!!.tableIDList = it.tableIDList.plusElement(table.id)
                                                        menuRepository.updateMenu(it)
                                                            .subscribeOn(Schedulers.io())
                                                            .subscribe({
                                                                if (it) {
                                                                    findNavController().navigateUp()
                                                                } else {
                                                                    Toast.makeText(requireContext(), "qrCodeGeneratingError", Toast.LENGTH_LONG).show()
                                                                }
                                                            }, {
                                                                println("error in TableEditFragment.onViewCreated iiiiiiiiiiiiiiiiii - > "+ it.message)

                                                            })
                                                            .addTo(disposable)
                                                    }, {
                                                        println("error  2385343573895794375 in TableEditFragment.onViewCreated imageRepository.generateQRCode(table) menuRepository.updateTable(table) - > "+ it.message)

                                                    })
                                                    .addTo(disposable)
                                            }, {
                                                println("error in TableEditFragment.onViewCreated imageRepository.generateQRCode(table) menuRepository.updateTable(table) - > "+ it.message)
                                            })
                                            .addTo(disposable)
                                    }, {
                                        println("error in TableEditFragment.onViewCreated imageRepository.generateQRCode(table) - > "+ it.message)
                                    }).addTo(disposable)
                            }, {
                                println("error in TableEditFragment.onViewCreated update table in create table - > "+ it.message)
                            }).addTo(disposable)
                    }, {
                        println("error in TableEditFragment.onViewCreated in create table - > "+ it.message)
                    }).addTo(disposable)

            } else {
                menuRepository.getTable(tableID)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        val t = it!!
                        t.tableName = tableNameEditText.text.toString()
                        menuRepository.updateTable(t)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                if (it) {
                                    findNavController().navigateUp()
                                } else {
                                    Toast.makeText(requireContext(), "Table update Error", Toast.LENGTH_LONG).show()
                                }
                            }, {
                                println("error in TableEditFragment.onViewCreated update table in create table - > "+ it.message)
                            }).addTo(disposable)
                    }, {

                    }).addTo(disposable)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}