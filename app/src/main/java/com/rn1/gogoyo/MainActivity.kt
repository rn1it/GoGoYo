package com.rn1.gogoyo

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.rn1.gogoyo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityMainBinding

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        val navController = findNavController(R.id.myNavHostFragment)

        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(NavigationDirections.actionGlobalHomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_event -> {
                navController.navigate(NavigationDirections.actionGlobalEventFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_friend -> {
                navController.navigate(NavigationDirections.actionGlobalMakeFriendsFragment())
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


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
//        binding.viewModel =



        val user: MutableMap<String, Any> = HashMap()
        user["first"] = "Ada"
        user["last"] = "Lovelace"
        user["born"] = 1815

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "FragmentActivity.TAG",
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }

        setUpBottomNav()
        setupNavController()
    }


    private fun setUpBottomNav() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        // TODO add badge here
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener{ navController: NavController, navDestination: NavDestination, bundle: Bundle? ->

        }
    }

}