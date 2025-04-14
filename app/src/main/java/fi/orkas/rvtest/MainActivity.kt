package fi.orkas.rvtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.prefill.PreFillType
import dagger.hilt.android.AndroidEntryPoint
import fi.orkas.rvtest.databinding.ActivityMainBinding
import kotlinx.serialization.Serializable

@Serializable
data object Home

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Glide.get(this).preFillBitmapPool(PreFillType.Builder(POSTER_WIDTH, POSTER_HEIGHT))

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = binding.root.getFragment<NavHostFragment>()
        val navController = navHostFragment.navController
        navController.graph = navController.createGraph(startDestination = Home) {
            fragment<HomeFragment, Home> {
                label = "Home"
            }
        }

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            ViewCompat.onApplyWindowInsets(view, insets)
        }
    }
}
