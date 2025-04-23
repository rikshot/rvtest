package fi.orkas.rvtest.repository

import android.net.Uri
import androidx.navigation.NavController

data class MediaCard(
    val id: Int,
    val title: String,
    val thumbnail: Uri?,
    val poster: Uri?,
    val onClick: (NavController) -> Unit
)
