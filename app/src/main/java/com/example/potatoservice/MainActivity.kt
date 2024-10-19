package com.example.potatoservice

import android.os.Bundle
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.potatoservice.databinding.ActivityMainBinding
import com.example.potatoservice.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
	private lateinit var homeViewModel: HomeViewModel
	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val navView: BottomNavigationView = binding.navView

		val navController = findNavController(R.id.nav_host_fragment_activity_main)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		val appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.navigation_home, R.id.navigation_map, R.id.navigation_mypage
			)
		)
//		setupActionBarWithNavController(navController, appBarConfiguration)
		navView.setupWithNavController(navController)
		navView.itemIconTintList = null
	}
}
