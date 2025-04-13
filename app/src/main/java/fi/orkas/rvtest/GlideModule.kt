package fi.orkas.rvtest

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.util.Locale
import kotlin.math.roundToLong

@GlideModule
class GlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean = false

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val heapSize = Runtime.getRuntime().maxMemory()
        val maxHeapSizeMultiplier = 0.5
        val arrayPoolSize = 8 * 1024 * 1024
        val availableSize = (heapSize * maxHeapSizeMultiplier) - arrayPoolSize

        val memoryCacheSize = (availableSize * 0.5).roundToLong()
        val bitmapPoolSize = (availableSize * 0.5).roundToLong()

        Log.d(
            "GlideModule",
            """Glide cache settings:
            | Heap size: ${heapSize.inMiB} MiB
            | Available size: ${availableSize.roundToLong().inMiB} MiB
            | Memory cache size: ${memoryCacheSize.inMiB} MiB
            | Bitmap pool size: ${bitmapPoolSize.inMiB} MiB
            """.trimMargin()
        )

        builder
            .setArrayPool(LruArrayPool(arrayPoolSize))
            .setMemoryCache(LruResourceCache(memoryCacheSize))
            .setBitmapPool(LruBitmapPool(bitmapPoolSize))
            .setDefaultRequestOptions(
                RequestOptions()
                    .dontAnimate()
                    .dontTransform()
                    .fallback(Color.BLACK.toDrawable())
                    .error(Color.BLACK.toDrawable())
                    .placeholder(Color.TRANSPARENT.toDrawable())
            )
    }

    companion object {
        val Long.inMiB get() = String.format(Locale.getDefault(), "%.2f", this.toFloat() / (1024 * 1024))
    }
}
