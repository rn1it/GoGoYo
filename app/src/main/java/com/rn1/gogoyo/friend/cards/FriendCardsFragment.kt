package com.rn1.gogoyo.friend.cards

import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendCardsBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.util.Logger
import com.yuyakaido.android.cardstackview.*

class FriendCardsFragment(userId: String) : Fragment(), CardStackListener {

    private lateinit var binding: FragmentFriendCardsBinding
    private val viewModel by viewModels<FriendCardsViewModel> { getVmFactory(userId) }
    private val adapter by lazy { CardStackAdapter(viewModel, mutableListOf()) }
    private lateinit var  cardStackView: CardStackView
    val list = mutableListOf<Users>()
    private val mp = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.d("Cards onCreateView ")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_cards, container, false)
        binding.lifecycleOwner = this

        cardStackView = binding.cardStackView

        cardStackView.adapter = adapter

        cardStackView.layoutManager = CardStackLayoutManager(requireContext(), this)
//        manager = CardStackLayoutManager(requireContext(), this)

        viewModel.usersNotFriend.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("usersNotFriend = $it")
                Logger.d("aaaaaaaaaaaaaaaaaaaaa = ${it.size}")
                list.addAll(it)

                // because fire base callback bug
                val resetList = it.toHashSet()
                list.clear()
                list.addAll(resetList.toList())

                if (list.size == 0) {
                    binding.friendCardNotificationTv.visibility = View.VISIBLE
                    binding.buttonContainer.visibility = View.GONE
                } else {
                    binding.friendCardNotificationTv.visibility = View.GONE
                    binding.buttonContainer.visibility = View.VISIBLE
//                    newAdapter.submitList(list)

                    val old = adapter.getUsers()
                    val new = mutableListOf<Users>().apply {
                        addAll(list)
                    }
                    val callback = CardStackAdapter.UserDiffCallBack(old, new)
                    val result = DiffUtil.calculateDiff(callback)
                    adapter.setUsers(new)
                    result.dispatchUpdatesTo(adapter)
                }
            }
        })

        viewModel.dataChange.observe(viewLifecycleOwner, Observer {
            it?.let {
                //test
                Logger.d("notifyItemChanged = $it")
                adapter.notifyItemChanged(it[0], it[1])
            }
        })

        viewModel.showBarkToast.observe(viewLifecycleOwner, Observer {
            it?.let {
                play(it)
                viewModel.onDoneShowBarkToast()
            }
        })

        viewModel.showVideoDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                setExoplayer(it)
                viewModel.onDoneShowVideoDialog()
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
        val manager = cardStackView.layoutManager as CardStackLayoutManager
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
//        Logger.d("onCardDragging: d = ${direction.name}, r = $ratio")
    }

    /**
     * get card like or dislike, position = list[] + 1
     */
    override fun onCardSwiped(direction: Direction?) {
        val manager = cardStackView.layoutManager as CardStackLayoutManager
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")

        val user = list[manager.topPosition - 1]
        Logger.w("user = $user")
        Logger.w("list before = $list")
        list.remove(user)
        Logger.w("list after = $list")
        when (direction) {
            Direction.Left -> {
                viewModel.addOrPassCard(list, user.id, false)
            }
            Direction.Right -> {
                viewModel.addOrPassCard(list, user.id, true)
                Toast.makeText(context, "已對 ${user.name} 送出好友邀請", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCardRewound() {
        val manager = cardStackView.layoutManager as CardStackLayoutManager
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        val manager = cardStackView.layoutManager as CardStackLayoutManager
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
        val manager = cardStackView.layoutManager as CardStackLayoutManager
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
            val user = list[manager.topPosition]
            list.remove(user)

            viewModel.addOrPassCard(list, user.id, false)
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

            val user = list[manager.topPosition]
            list.remove(user)
            Toast.makeText(context, "已對 ${user.name} 送出好友邀請", Toast.LENGTH_SHORT).show()
            viewModel.addOrPassCard(list, user.id, true)
        }

        //        val rewind = binding.rewindButton
//        rewind.setOnClickListener {
//            val setting = RewindAnimationSetting.Builder()
//                .setDirection(Direction.Bottom)
//                .setDuration(Duration.Normal.duration)
//                .setInterpolator(DecelerateInterpolator())
//                .build()
//            manager.setRewindAnimationSetting(setting)
//            cardStackView.rewind()
//            Log.d("CardStackView", "rewind: p = ${manager.topPosition}")
//        }
    }

    /**
     * for audio play
     */
    private fun play(path: String?) {
        stopMediaPlayer()
        if (path.isNullOrBlank()) {
            Toast.makeText(context, "還沒有上傳聲音喔!", Toast.LENGTH_SHORT).show()
        } else {

            // show bark toast
            val inflater  = layoutInflater
            val container = requireActivity().findViewById<ViewGroup>(R.id.custom_toast)
            val layout = inflater .inflate(R.layout.custom_toast_dog_bark, container)

            with (Toast(context)) {
                setGravity(Gravity.CENTER, 0, 0)
                duration = Toast.LENGTH_SHORT
                view = layout
                show()
            }

            Logger.d("play path = $path")
            val uri = Uri.parse(path)
            mp.reset()
            mp.setDataSource(requireContext(), uri)
            mp.setOnPreparedListener {
                it.start()


            }
            mp.prepare()
        }

    }

    /**
     * for audio stop
     */
    private fun stopMediaPlayer() {
        if (mp.isPlaying) {
            mp.stop()
//            binding.audioPlayBt.setImageResource(R.drawable.play_music)
        }
    }

    /**
     * for video play
     */
    private fun setExoplayer(url: String?) {

        stopMediaPlayer()

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.introvid)
        dialog.show()

        val lp = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        lp.copyFrom(dialog.window!!.attributes)
        dialog.window!!.attributes = lp

        val playerView = dialog.findViewById(R.id.exoplayer_item) as PlayerView

        try {
            val exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())
            val video = Uri.parse(url)
            val dataSourceFactory = DefaultHttpDataSourceFactory("video")
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
            val mediaSource: MediaSource =
                ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null)
            playerView.player = exoPlayer
            exoPlayer.prepare(mediaSource)
            exoPlayer.playWhenReady = false
        } catch (e: Exception) {
            Toast.makeText(context, "還沒有上傳影片喔!", Toast.LENGTH_SHORT).show()
            Log.e("ViewHolder", "exoplayer error$e")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Logger.d("Cards onViewCreated ")
    }
}