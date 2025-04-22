package fi.orkas.rvtest.repository

import android.net.Uri
import androidx.core.net.toUri

data class MediaCard(val id: Int, val title: String, val thumbnail: Uri?, val poster: Uri?)

fun MovieResult.toMediaCard(details: Details): MediaCard = MediaCard(
    id,
    title,
    posterPath?.let { posterPath ->
        "${details.images.secureBaseUrl}${details.images.posterSizes[0]}$posterPath".toUri()
    },
    posterPath?.let { posterPath ->
        "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri()
    }
)

fun TvResult.toMediaCard(details: Details): MediaCard = MediaCard(
    id,
    name,
    posterPath?.let { posterPath ->
        "${details.images.secureBaseUrl}${details.images.posterSizes[0]}$posterPath".toUri()
    },
    posterPath?.let { posterPath ->
        "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri()
    }
)
