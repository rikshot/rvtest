package fi.orkas.rvtest.repository

import android.net.Uri
import androidx.core.net.toUri

data class MediaCard(val id: Int, val title: String, val thumbnail: Uri, val poster: Uri)

fun MovieResult.toMediaCard(details: Details): MediaCard = MediaCard(
    id,
    title,
    "${details.images.secureBaseUrl}${details.images.posterSizes[0]}$posterPath".toUri(),
    "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri()
)

fun TvResult.toMediaCard(details: Details): MediaCard = MediaCard(
    id,
    name,
    "${details.images.secureBaseUrl}${details.images.posterSizes[0]}$posterPath".toUri(),
    "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri()
)
