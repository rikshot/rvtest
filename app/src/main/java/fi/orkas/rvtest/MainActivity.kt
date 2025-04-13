package fi.orkas.rvtest

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.prefill.PreFillType
import fi.orkas.rvtest.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var db: Database
    private lateinit var viewCache: ViewCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Glide.get(this).preFillBitmapPool(PreFillType.Builder(POSTER_WIDTH, POSTER_HEIGHT))

        db =
            Room
                .databaseBuilder(this, Database::class.java, "database.db")
                .createFromAsset("database.db")
                .build()

        viewCache = ViewCache(this)
        viewCache.cache(R.layout.category, 4)
        viewCache.cache(R.layout.card, 21)

        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        val adapter = VerticalAdapter(this, viewCache)
        binding.categories.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(0)
            layoutManager =
                ExtraSpaceLinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            this.adapter = adapter
            addOnScrollListener(adapter.preloader)
        }
        setContentView(binding.root)

        lifecycleScope.launch {
            db.mediaDao().getCategoryMedia().collect {
                adapter.submitList(it)
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
