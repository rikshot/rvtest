package fi.orkas.rvtest

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Priority
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsBytes
import java.io.File
import java.io.InputStream
import java.util.Locale
import kotlin.math.roundToLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(Color.BLACK.toDrawable())
                    .placeholder(Color.TRANSPARENT.toDrawable())
            )
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val glideHttpClient = HttpClient(CIO) {
            expectSuccess = true
            install(HttpCache) {
                publicStorage(FileStorage(File(context.cacheDir, "glide")))
            }
            install(ContentEncoding)
        }
        registry.replace(GlideUrl::class.java, InputStream::class.java, KtorModelLoader.Factory(scope, glideHttpClient))
    }

    companion object {
        val Long.inMiB get() = String.format(Locale.getDefault(), "%.2f", this.toFloat() / (1024 * 1024))
    }
}

class KtorModelLoader(private val scope: CoroutineScope, private val httpClient: HttpClient) :
    ModelLoader<GlideUrl, InputStream> {
    override fun buildLoadData(
        model: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream?>? = ModelLoader.LoadData(model, KtorDataFetcher(scope, httpClient, model))

    override fun handles(model: GlideUrl): Boolean = true

    class Factory(private val scope: CoroutineScope, private val httpClient: HttpClient) :
        ModelLoaderFactory<GlideUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> =
            KtorModelLoader(scope, httpClient)

        override fun teardown() {}
    }
}

class KtorDataFetcher(
    private val scope: CoroutineScope,
    private val httpClient: HttpClient,
    private val url: GlideUrl
) : DataFetcher<InputStream> {
    private var job: Job? = null
    private var inputStream: InputStream? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        job =
            scope.launch {
                inputStream = httpClient.request(url.toURL()).bodyAsBytes().inputStream()
                callback.onDataReady(inputStream)
            }
    }

    override fun cleanup() {
        inputStream?.close()
    }

    override fun cancel() {
        inputStream?.close()
        job?.cancel()
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.REMOTE
}
