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
data object HomeRoute

@Serializable
data class DetailsRoute(val id: Int, val type: DetailsType)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (width, height) = Pair(
            resources.getDimensionPixelSize(R.dimen.card_width),
            resources.getDimensionPixelSize(R.dimen.card_height)
        )
        Glide.get(this).preFillBitmapPool(PreFillType.Builder(width, height))

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = binding.root.getFragment<NavHostFragment>()
        val navController = navHostFragment.navController
        navController.graph = navController.createGraph(startDestination = HomeRoute) {
            fragment<HomeFragment, HomeRoute> {
                label = "Home"
            }
            fragment<DetailsFragment, DetailsRoute> {
                label = "Details"
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
