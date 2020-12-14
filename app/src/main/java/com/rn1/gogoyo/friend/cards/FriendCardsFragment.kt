package com.rn1.gogoyo.friend.cards

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendCardsBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.util.Logger
import com.yuyakaido.android.cardstackview.*

class FriendCardsFragment(userId: String) : Fragment(), CardStackListener {

    private lateinit var binding: FragmentFriendCardsBinding
    private val viewModel by viewModels<FriendCardsViewModel> { getVmFactory(userId) }
    private val manager by lazy { CardStackLayoutManager(requireContext(), this) }
    private val adapter by lazy { CardStackAdapter(viewModel) }
    private lateinit var  cardStackView: CardStackView
    val list = mutableListOf<Users>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_cards, container, false)
        binding.lifecycleOwner = this

        cardStackView = binding.cardStackView
        cardStackView.layoutManager = CardStackLayoutManager(requireContext())
        cardStackView.adapter = adapter

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

//        val transformer = CompositePageTransformer()
//        transformer.addTransformer(MarginPageTransformer(4))
//        viewPager.setPageTransformer(transformer)

        val user1 = Users("001", "001")
        val user2 = Users("002", "002")
        val user3 = Users("003", "003")


        list.add(user1)
        list.add(user2)
        list.add(user3)

        adapter.submitList(list)
//        val adapter = FriendCardsAdapter()

//        viewPager.adapter = adapter
//        adapter.submitList(list)

        setupCardStackView()
        setupButton()


        return binding.root
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Logger.d("onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
//        if (manager.topPosition == adapter.itemCount - 5) {
//            paginate()
//        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_user_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_user_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }



//    private fun paginate() {
//        val old = adapter.getSpots()
//        val new = old.plus(createSpots())
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        adapter.setSpots(new)
//        result.dispatchUpdatesTo(adapter)
//    }

    private fun setupButton(){
        val skip = binding.skipButton
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = binding.rewindButton
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = binding.likeButton
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()



        }
    }
}