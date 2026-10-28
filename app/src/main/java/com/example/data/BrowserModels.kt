package com.example.data

import java.util.UUID

enum class ThemeMode(val label: String) {
  SYSTEM("System"),
  LIGHT("Light"),
  DARK("Dark")
}

enum class WallpaperPreset(
  val label: String,
  val drawableResName: String,
  val accentColor: Long,
  val primaryColor: Long,
  val glassTintDark: Long,
  val glassTintLight: Long
) {
  FOREST(
    label = "Pine Forest",
    drawableResName = "wallpaper_forest",
    accentColor = 0xFF34C759,     // Vibrant iOS Nature Emerald
    primaryColor = 0xFF28A745,
    glassTintDark = 0x48111D15,
    glassTintLight = 0x4DFFFFFF
  ),
  DARK(
    label = "Obsidian",
    drawableResName = "wallpaper_dark",
    accentColor = 0xFF00D2FF,     // Radiant Electric Cyan
    primaryColor = 0xFF0A84FF,
    glassTintDark = 0x480D141C,
    glassTintLight = 0x4DFFFFFF
  ),
  SUNSET(
    label = "Sunset Dusk",
    drawableResName = "wallpaper_sunset",
    accentColor = 0xFFFF7597,     // Twilight Coral Rose
    primaryColor = 0xFFFF5A79,
    glassTintDark = 0x4826131D,
    glassTintLight = 0x4DFFFFFF
  ),
  MINT_FROST(
    label = "Mint Glass",
    drawableResName = "",
    accentColor = 0xFF00E5A3,     // Crisp Aquamarine Mint
    primaryColor = 0xFF00C988,
    glassTintDark = 0x480F241C,
    glassTintLight = 0x4DFFFFFF
  ),
  CUSTOM(
    label = "Custom Photo",
    drawableResName = "",
    accentColor = 0xFF5E5CE6,     // Dynamic Indigo Accent
    primaryColor = 0xFF4338CA,
    glassTintDark = 0x48181A24,
    glassTintLight = 0x4DFFFFFF
  )
}

enum class ActiveSegment {
  START_PAGE,
  BOOKMARKS,
  READING_LIST,
  HISTORY
}

data class BrowserTab(
  val id: String = UUID.randomUUID().toString(),
  val title: String = "Start Page",
  val url: String = "",
  val faviconUrl: String? = null,
  val isLoading: Boolean = false,
  val progress: Float = 0f,
  val canGoBack: Boolean = false,
  val canGoForward: Boolean = false,
  val isPrivate: Boolean = false,
  val previewColor: Long = 0xFF143D28
) {
  val isStartPage: Boolean get() = url.isEmpty() || url == "about:blank"
}

data class FavoriteItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val url: String,
  val iconType: String,
  val bgColor: Long = 0xFF2A2A2A,
  val fgColor: Long = 0xFFFFFFFF,
  val badgeText: String? = null
)

data class ReadingListItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val excerpt: String,
  val domain: String,
  val url: String,
  val thumbnailRes: Int,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
)

data class HistoryItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val url: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class BookmarkItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val url: String,
  val folder: String = "Favorites",
  val timestamp: Long = System.currentTimeMillis()
)
