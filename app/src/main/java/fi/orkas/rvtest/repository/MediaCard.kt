package fi.orkas.rvtest.repository

import android.net.Uri
import androidx.core.net.toUri
import fi.orkas.rvtest.POSTER_HEIGHT
import fi.orkas.rvtest.POSTER_WIDTH

data class MediaCard(
    val posterUrl: Uri,
    val title: String,
    val width: Int = POSTER_WIDTH,
    val height: Int = POSTER_HEIGHT
)

fun MovieResult.toMediaCard(details: Details): MediaCard = MediaCard(
    "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri(),
    title,
    POSTER_WIDTH,
    POSTER_HEIGHT
)

fun TvResult.toMediaCard(details: Details): MediaCard =
    MediaCard("${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri(), name)
