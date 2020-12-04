package com.rn1.gogoyo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.rn1.gogoyo.databinding.ActivityMainBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.util.CurrentFragmentType


private const val TAG = "GoGoYo"
class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val navController =  findNavController(R.id.myNavHostFragment)

        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(NavigationDirections.actionGlobalHomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_event -> {
                navController.navigate(NavigationDirections.actionGlobalChatRoomFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_friend -> {
//                navController.navigate(NavigationDirections.actionGlobalMakeFriendsFragment())
                navController.navigate(NavigationDirections.actionGlobalFriendFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_mypets -> {
                navController.navigate(NavigationDirections.actionGlobalMyPetsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Users("","")
        val users = db.collection("users")
        val document = users.document()
        user.id = document.id



        //Login Check
        if (!UserManager.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else {
            Log.d(TAG, "jlkjlkjlkjlk ${UserManager.userUID}")

            document
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "asasasas ${user}")
                    } else {

                    }
                }

        }


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

        setUpBottomNav()
        setupNavController()
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

                else -> viewModel.currentFragmentType.value
            }
        }
    }

}