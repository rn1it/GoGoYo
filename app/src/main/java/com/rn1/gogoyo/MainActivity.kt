package com.rn1.gogoyo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.rn1.gogoyo.databinding.ActivityMainBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.CurrentFragmentType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val navController =  findNavController(R.id.myNavHostFragment)

        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(NavigationDirections.actionGlobalHomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_statistic -> {
                navController.navigate(NavigationDirections.actionGlobalStatisticFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_friend -> {
                navController.navigate(NavigationDirections.actionGlobalFriendFragment(UserManager.userUID!!))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_mypets -> {
                navController.navigate(NavigationDirections.actionGlobalMyPetsFragment(UserManager.userUID!!))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        //Login Check
        if (!UserManager.isLoggedIn) {

            // if user not login, intent to login activity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else {
            val userName = UserManager.userName  ?:  "No Name"
            viewModel.loginAndSetUser(UserManager.userUID!!, userName)

            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            binding.lifecycleOwner = this
            binding.viewModel = viewModel

            // observe current fragment change, only for show info
            viewModel.currentFragmentType.observe(this, Observer {
                Log.d(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                Log.d(TAG,"[${viewModel.currentFragmentType.value}]")
                Log.d(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            })

            viewModel.navigateToWalk.observe(this, Observer {
                it?.let {
                    setUpWalkBottom()
                    viewModel.onDoneNavigateToWalk()
                }
            })

            viewModel.popBack.observe(this, Observer {
                it?.let {
                    findNavController(R.id.myNavHostFragment).popBackStack()
                }
            })

            viewModel.navigateToHomeByBottomNav.observe(this, Observer {
                it?.let {
                    binding.bottomNavigationView.selectedItemId = R.id.nav_home
                    viewModel.onNavigateToHomeDone()
                }
            })

            viewModel.navigateToStatisticByBottomNav.observe(this, Observer {
                it?.let {
                    binding.bottomNavigationView.selectedItemId = R.id.nav_statistic
                    viewModel.onNavigateToStatisticDone()
                }
            })

            setUpBottomNav()
            setupNavController()
            setupStatusBar()
        }
    }

    private fun setUpWalkBottom() {
        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalWalkFragment())
        binding.bottomNavigationView.menu.getItem(2).isChecked = true
    }


    private fun setUpBottomNav() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        // TODO add badge here
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener{ navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            viewModel.currentFragmentType.value =  when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.articleContentFragment -> CurrentFragmentType.ARTICLE_CONTENT
                R.id.postFragment -> CurrentFragmentType.POST_ARTICLE
                R.id.walkFragment -> CurrentFragmentType.WALK
                R.id.friendFragment -> CurrentFragmentType.FRIEND
                R.id.friendCardsFragment -> CurrentFragmentType.FRIEND_CARD
                R.id.friendListFragment -> CurrentFragmentType.FRIEND_LIST
                R.id.friendChatFragment -> CurrentFragmentType.FRIEND_CHAT
                R.id.myPetsFragment -> CurrentFragmentType.PROFILE_PET
                R.id.walkStartFragment -> CurrentFragmentType.WALK_START
                R.id.chatRoomFragment -> CurrentFragmentType.CHAT_ROOM
                R.id.walkEndFragment -> CurrentFragmentType.WALK_END
                R.id.statisticFragment -> CurrentFragmentType.STATISTIC
                R.id.editPetFragment -> CurrentFragmentType.EDIT_PET
                R.id.editUserFragment -> CurrentFragmentType.EDIT_USER
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupStatusBar(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    companion object {
        private const val TAG = "GoGoYo"
    }
}