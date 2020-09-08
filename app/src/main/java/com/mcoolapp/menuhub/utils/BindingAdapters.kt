package com.mcoolapp.menuhub.utils


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuItemsListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuItemsListMenuEditAdapter
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.repository.ImageRepository
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


@BindingAdapter("adapter")
fun setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter
}

@BindingAdapter("menuItemsList")
fun setMenuItemsList(view: RecyclerView, list: MutableLiveData<List<String>>) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    val context = parentActivity!!.applicationContext
    val menuRepository = MenuRepository()
    menuRepository.setBaseContext(context)
    list.observeForever {
        //TODO
    }
}

@BindingAdapter("mutableVisibility")
fun setMutableVisibility(view: View, visibility: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && visibility != null) {
        visibility.observeForever {
            view.visibility = it
        }
    }
}

@BindingAdapter("mutableText")
fun setMutableText(view: TextView, text: MutableLiveData<String>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && text != null) {
        text.observe(parentActivity, Observer { value -> view.text = value ?: "" })

    }
}

@BindingAdapter("stringFromList")
fun getStringFromList(textView: TextView, list: List<String?>) {
    var s = ""
    for (st in list) {
        if (st != "") s = s + st + ", "
    }
    textView.text = s
}

@BindingAdapter("mutableTextList")
fun setMutableTextList(view: TextView, text: MutableLiveData<List<String>>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && text != null) {
        text.observe(parentActivity, Observer { value ->
            var s = ""
            value.forEach {
                s += it + " "
            }
            view.text = s ?: ""
        })

    }
}

@BindingAdapter("observeString")
fun observeString(editText: EditText, liveData: MutableLiveData<String>) {

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!liveData.value.equals(s.toString())) {
                liveData.value = s.toString()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    editText.addTextChangedListener(watcher)

    liveData.observeForever() {
        if (editText.text.isEmpty()) {
            editText.text.insert(0, it)
        }
    }
}

@BindingAdapter("observeListString")
fun observeStringList(editText: EditText, liveData: MutableLiveData<List<String>>) {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!liveData.value!!.joinToString(", ","","").equals(s.toString())) {
                liveData.value = s.toString().split(", ")
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    editText.addTextChangedListener(watcher)

    liveData.observeForever() {
        if (editText.text.isEmpty()) {
            editText.text.insert(0, it.joinToString(", "))
        }
    }
}
@BindingAdapter("observeDouble")
@JvmSuppressWildcards
fun observeDouble(editText: EditText, liveData: MutableLiveData<Double>) {
    editText.addTextChangedListener {
        try {
            liveData.value = it.toString().toDouble()
        } catch (ex: Exception) {
            System.out.println("@BindingAdapter(\"observeDouble\") error = " + ex)
        }

    }
}

@BindingAdapter("popupMenu")
fun setPopupMenu(view: EditText, @MenuRes resId: Int) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && resId != null) {
        view.setOnClickListener {
            val popUp = PopupMenu(parentActivity, view)

            //Inflate our menu Layout.
            popUp.menuInflater.inflate(resId, popUp.menu)


            //Set Click Listener on Popup Menu Item
            popUp.setOnMenuItemClickListener { myItem ->

                //Getting Id of selected Item
                val item = myItem!!.itemId

                view.text.clear()
                view.text.insert(0, myItem.title)

                true
            }
            popUp.show()
        }

    }
}


@BindingAdapter("imageIDWithBucket")
fun imageWithBucketForLiveData(
    view: ImageView,
    imageWithBucket: MutableLiveData<ImageWithBucket>
) {

    val parentActivity: AppCompatActivity? = view.getParentActivity()
    val imageRepository = ImageRepository(parentActivity!!.baseContext)

    System.out.println(" BindingAdapter imageIDWithBucket fun imageWithBucket: bucketName = " + imageWithBucket!!)

    if (imageWithBucket != null && imageWithBucket.value != null) {
        imageWithBucket.observe(parentActivity, Observer { value ->
            imageRepository.getImageFromBucket(value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Glide.with(view)
                        .load(it)
                        //.circleCrop()
                        .into(view)

                }, {
                    System.out.println("BindingAdapter imagewithBucket error " + it)
                })
        })
    } else {
    }
}

@BindingAdapter("imageIDWithBucketNoLiveData")
fun imageWithBucketNoLiveData(view: ImageView, imageWithBucket: ImageWithBucket?) {

    val parentActivity: AppCompatActivity? = view.getParentActivity()
    val imageRepository = ImageRepository(parentActivity!!.baseContext)
    if (imageWithBucket != null)
        imageRepository.getImageFromBucket(imageWithBucket)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Glide.with(view)
                    .load(it)
                    .into(view)

            }, {
                System.out.println("BindingAdapter imagewithBucket error " + it)
            })

}


@BindingAdapter("android:arrayAdapter")
fun setAutoCompleteAdapter(
    view: MultiAutoCompleteTextView,
    array: Array<String>
) {
    val adapter =
        ArrayAdapter<String>(view.context, R.layout.support_simple_spinner_dropdown_item, array)
    view.setAdapter(adapter)
}



