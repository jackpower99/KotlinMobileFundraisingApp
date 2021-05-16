package ie.wit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ie.wit.R
import ie.wit.fragments.AllMoviesFragment
import ie.wit.fragments.MovieFragment
import ie.wit.fragments.UserFavouritesFragment
import ie.wit.fragments.ViewFragment
import ie.wit.helpers.*
import ie.wit.main.MovieApp
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction

    lateinit var app: MovieApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)

        app = application as MovieApp

        navView.setNavigationItemSelectedListener(this)

        navView.getHeaderView(0).navheaderemail.text = app.auth.currentUser?.email
        navView.getHeaderView(0).nav_header_name.text = app.auth.currentUser?.displayName

        checkExistingPhoto(app,this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        ft = supportFragmentManager.beginTransaction()

        val fragment = MovieFragment.newInstance()
        ft.replace(R.id.homeFrame, fragment)
        ft.commit()

        navView.getHeaderView(0).imageView
            .setOnClickListener { showImagePicker(this,1) }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_movies -> navigateTo(ViewFragment.newInstance())
            R.id.nav_add -> navigateTo(MovieFragment.newInstance())
            R.id.nav_favourites -> navigateTo(UserFavouritesFragment.newInstance())
            R.id.nav_sign_out ->
                signOut()
            R.id.nav_report_all ->
                navigateTo(AllMoviesFragment.newInstance())

            else -> toast("You Selected Something Else")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add -> toast("You Selected Donate")
            R.id.action_view -> toast("You Selected Report")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
         else
            super.onBackPressed()
    }

    internal fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun signOut()
    {
        app.auth.signOut()
        startActivity<Login>()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (data != null) {
                    writeImageRef(app,readImageUri(resultCode, data).toString())
                    Picasso.get().load(readImageUri(resultCode, data).toString())
                        .resize(180, 180)
                        .transform(CropCircleTransformation())
                        .into(navView.getHeaderView(0).imageView, object : Callback {
                            override fun onSuccess() {
                                // Drawable is ready
                                uploadImageView(app,navView.getHeaderView(0).imageView)
                            }
                            override fun onError(e: Exception) {}
                        })
                }
            }
        }
    }
}
