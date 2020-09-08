package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import kotlinx.android.synthetic.main.fragment_news.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        openUserButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(UserDetailFragment.USER_ID_KEY, FirebaseAuth.getInstance().uid)
            findNavController().navigate(R.id.action_newsFragment_to_userDetailFragment4, bundle )
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    //добавить параметры если нужно
                }
            }
    }
}