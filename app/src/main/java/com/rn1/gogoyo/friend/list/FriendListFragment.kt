package com.rn1.gogoyo.friend.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendListBinding
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Friends
import com.rn1.gogoyo.model.Users
import java.util.*


class FriendListFragment : Fragment() {

    private lateinit var binding: FragmentFriendListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_list, container, false)
        binding.lifecycleOwner = this

        val recyclerView = binding.friendListRv
        val adapter = FriendListAdapter()

        recyclerView.adapter = adapter

        val f1 = Friends(Users("01", "01"))
        val f2 = Friends(Users("02", "02"))
        val f3 = Friends(Users("03", "03"))
        val f4 = Friends(Users("04", "04"))
        val list = mutableListOf<Friends>()

        list.add(f1)
        list.add(f2)
        list.add(f3)
        list.add(f4)

        adapter.submitList(list)

        binding.friendListSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(query: String): Boolean {
                adapter.submitList(filter(list, query))
                return true
            }
        })

        return binding.root
    }

    // return query list
    fun filter(list: List<Friends>, query: String): List<Friends> {

        val lowerCaseQueryString = query.toLowerCase(Locale.ROOT)
        val filteredList = mutableListOf<Friends>()

        for (friend in list) {
            val name = friend.user!!.name.toLowerCase(Locale.ROOT)

            if (name.contains(lowerCaseQueryString)) {
                filteredList.add(friend)
            }
        }

        return filteredList
    }
}