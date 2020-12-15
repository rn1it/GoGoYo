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
import androidx.lifecycle.Observer
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

        cardStackView.adapter = adapter

        viewModel.usersNotFriend.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("it = $it")
                adapter.submitList(it)
            }
        })

        viewModel.dataChange.observe(viewLifecycleOwner, Observer {
            it?.let {
                //test
                Logger.d("notifyItemChanged = $it")
                adapter.notifyItemChanged(it[0], it[1])
            }
        })

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
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_pet_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_pet_name)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

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
            Log.d("CardStackView", "skip: p = ${manager.topPosition}")
            list.removeAt(manager.topPosition)
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
            Log.d("CardStackView", "rewind: p = ${manager.topPosition}")
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

            list.remove(list[manager.topPosition])

            Log.d("CardStackView", "like: p = ${manager.topPosition}")
        }
    }
}