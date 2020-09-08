package com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.user.User


class UserListViewHolder(view: View, val context: Context) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    val userPhotoUserListItemImageView = view.findViewById<ImageView>(R.id.userPhotoUserListItemImageView)
    val userNameUserListItemImageView = view.findViewById<TextView>(R.id.userNameUserListItemTextView)
    val userEmailUserListItemImageView = view.findViewById<TextView>(R.id.userEmailUserListItemTextView)


    fun bind(user: User) {

        val storageRef = FirebaseStorage.getInstance().reference
        val photoRef = storageRef.child("userPhotos").child("accPh" + user.id)

        System.out.println(photoRef.toString())

        photoRef.downloadUrl.addOnSuccessListener {
            Glide.with(context)
                .load(it)
                .circleCrop()
                .into(userPhotoUserListItemImageView)
        }.addOnFailureListener{
            System.out.println("Has received error in userlistviewholder with download")
        }




        //load(itemImageView, student.url)
        userNameUserListItemImageView.text = user.name
        userEmailUserListItemImageView.text = user.eMail
    }
}