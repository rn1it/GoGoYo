package com.rn1.gogoyo.friend.cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.firebase.firestore.auth.User
import com.rn1.gogoyo.DataBinderMapperImpl
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendCardsBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Users
import com.yuyakaido.android.cardstackview.CardStackLayoutManager

class FriendCardsFragment(userId: String) : Fragment() {

    private lateinit var binding: FragmentFriendCardsBinding
    private val viewModel by viewModels<FriendCardsViewModel> { getVmFactory(userId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_cards, container, false)
        binding.lifecycleOwner = this

        val cardStackView = binding.cardStackView
        cardStackView.layoutManager = CardStackLayoutManager(requireContext())
//        cardStackView.adapter = CardStaA

//        val viewPager = binding.friendCardsViewPager
//        viewPager.clipToPadding = false
//        viewPager.clipChildren = false
//        viewPager.offscreenPageLimit = 3

//        viewPager.beginFakeDrag()
//        viewPager.fakeDragBy(-10f)
//        viewPager.endFakeDrag()

        //disable slide
//        viewPager.isUserInputEnabled = false

        // not to show slide to end effect
//        viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(4))
//        viewPager.setPageTransformer(transformer)

        val user1 = Users("001", "001")
        val user2 = Users("002", "002")
        val user3 = Users("003", "003")

        val list = mutableListOf<Users>()
        list.add(user1)
        list.add(user2)
        list.add(user3)

        val adapter = FriendCardsAdapter()

//        viewPager.adapter = adapter
        adapter.submitList(list)

        return binding.root
    }
}