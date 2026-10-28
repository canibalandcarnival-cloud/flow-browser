package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import coil.compose.AsyncImage
import com.example.data.FavoriteItem
import com.example.data.ReadingListItem
import com.example.data.ThemeMode
import com.example.data.WallpaperPreset
import com.example.ui.components.FrostedGlassSurface
import com.example.ui.components.RubberBandBouncyContainer
import com.example.ui.components.SquircleIcon
import com.example.ui.components.bouncyClick
import com.example.ui.components.glassEffect
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldPrimary

@Composable
fun StartPageScreen(
  favorites: List<FavoriteItem>,
  readingList: List<ReadingListItem>,
  themeMode: ThemeMode,
  wallpaperPreset: WallpaperPreset,
  customWallpaperUri: String? = null,
  isDark: Boolean,
  onNavigateUrl: (String) -> Unit,
  onToggleTheme: () -> Unit,
  onSelectWallpaper: (WallpaperPreset) -> Unit,
  onImportWallpaper: (String) -> Unit,
  onAddFavorite: (String, String) -> Unit,
  onShowAllFavorites: () -> Unit,
  onShowAllReadingList: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var showThemeSelector by remember { mutableStateOf(false) }
  var showAddFavoriteDialog by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onImportWallpaper(uri.toString())
    }
  }

  val accentColor = Color(wallpaperPreset.accentColor)
  val headerTextColor = if (isDark) Color.White else Color(0xFF131A15)
  val subtextColor = if (isDark) Color.White.copy(alpha = 0.72f) else Color(0xFF233027).copy(alpha = 0.75f)

  val textShadow = if (isDark) {
    Shadow(color = Color(0x99000000), offset = Offset(0f, 2f), blurRadius = 4f)
  } else {
    Shadow(color = Color(0x33FFFFFF), offset = Offset(0f, 1f), blurRadius = 2f)
  }

  RubberBandBouncyContainer(modifier = modifier) { flingBehavior ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState, flingBehavior = flingBehavior)
        .padding(horizontal = 18.dp)
        .padding(top = 74.dp, bottom = 96.dp)
    ) {
    // Top Bar quick action pill (Theme Mode & Wallpaper)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Wallpaper chip
      FrostedGlassSurface(
        modifier = Modifier
          .bouncyClick { showThemeSelector = !showThemeSelector }
          .testTag("wallpaper_settings_button"),
        shape = RoundedCornerShape(20.dp),
        elevation = 4.dp,
        isDark = isDark
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = "Themes",
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = wallpaperPreset.label,
            color = headerTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Dark / Light Mode toggle pill
      FrostedGlassSurface(
        modifier = Modifier
          .bouncyClick(onClick = onToggleTheme)
          .testTag("theme_toggle_button"),
        shape = RoundedCornerShape(20.dp),
        elevation = 4.dp,
        isDark = isDark
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = "Toggle Dark/Light Mode",
            tint = if (isDark) accentColor else Color(0xFFE89A24),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isDark) "Dark Mode" else "Light Mode",
            color = headerTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Expandable Theme/Wallpaper Selector Drawer
    AnimatedVisibility(visible = showThemeSelector) {
      FrostedGlassSurface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 6.dp,
        isDark = isDark
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Choose Wallpaper",
              color = headerTextColor,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "Tap + to import photo",
              color = subtextColor,
              fontSize = 11.5.sp
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Presets
            val mainPresets = listOf(
              WallpaperPreset.FOREST,
              WallpaperPreset.DARK,
              WallpaperPreset.SUNSET,
              WallpaperPreset.MINT_FROST
            )

            mainPresets.forEach { preset ->
              val isSelected = preset == wallpaperPreset
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (isSelected) {
                      accentColor.copy(alpha = if (isDark) 0.35f else 0.25f)
                    } else {
                      if (isDark) Color(0x33000000) else Color(0x1A000000)
                    }
                  )
                  .border(
                    width = if (isSelected) 1.8.dp else 0.5.dp,
                    color = if (isSelected) accentColor else Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .bouncyClick { onSelectWallpaper(preset) }
                  .testTag("wallpaper_preset_${preset.name.lowercase()}"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = preset.label.split(" ").first(),
                  color = if (isSelected) (if (isDark) Color.White else Color(0xFF141916)) else headerTextColor.copy(alpha = 0.75f),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              }
            }

            // Custom photo chip if loaded
            if (customWallpaperUri != null) {
              val isCustomSelected = wallpaperPreset == WallpaperPreset.CUSTOM
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (isCustomSelected) {
                      accentColor.copy(alpha = if (isDark) 0.35f else 0.25f)
                    } else {
                      if (isDark) Color(0x33000000) else Color(0x1A000000)
                    }
                  )
                  .border(
                    width = if (isCustomSelected) 1.8.dp else 0.5.dp,
                    color = if (isCustomSelected) accentColor else Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .bouncyClick { onSelectWallpaper(WallpaperPreset.CUSTOM) }
                  .testTag("wallpaper_preset_custom"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Custom",
                  color = if (isCustomSelected) (if (isDark) Color.White else Color(0xFF141916)) else headerTextColor.copy(alpha = 0.75f),
                  fontSize = 11.sp,
                  fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Medium
                )
              }
            }

            // Plus (+) button to import wallpaper from device photos
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.25f))
                .border(1.2.dp, accentColor, RoundedCornerShape(12.dp))
                .bouncyClick(scaleDown = 0.82f) {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
                .testTag("import_wallpaper_plus_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Import Wallpaper",
                tint = if (isDark) Color.White else Color(0xFF141916),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // ==========================================
    // FAVORITES SECTION
    // ==========================================
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Favorites",
        style = TextStyle(
          color = headerTextColor,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          shadow = textShadow
        ),
        modifier = Modifier.testTag("section_favorites_title")
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .bouncyClick(onClick = onShowAllFavorites)
          .testTag("favorites_show_all")
      ) {
        Text(
          text = "Show All",
          style = TextStyle(
            color = subtextColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            shadow = textShadow
          )
        )
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
          contentDescription = null,
          tint = subtextColor,
          modifier = Modifier.size(13.dp)
        )
      }
    }

    // 4-Column Grid for Favorites
    val favoritesChunked = favorites.take(8).chunked(4)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      favoritesChunked.forEach { rowItems ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          rowItems.forEach { item ->
            Column(
              modifier = Modifier
                .width(76.dp)
                .bouncyClick { onNavigateUrl(item.url) }
                .testTag("favorite_item_${item.id}"),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              SquircleIcon(
                iconType = item.iconType,
                bgColor = item.bgColor,
                fgColor = item.fgColor,
                badgeText = item.badgeText,
                iconSize = 64.dp
              )
              Spacer(modifier = Modifier.height(7.dp))
              Text(
                text = item.title,
                style = TextStyle(
                  color = headerTextColor,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Medium,
                  textAlign = TextAlign.Center,
                  shadow = textShadow
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
              )
            }
          }
          // Fill empty space in row if < 4 items
          repeat(4 - rowItems.size) {
            Spacer(modifier = Modifier.width(76.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(26.dp))

    // ==========================================
    // READING LIST SECTION
    // ==========================================
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Reading List",
        style = TextStyle(
          color = headerTextColor,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          shadow = textShadow
        ),
        modifier = Modifier.testTag("section_reading_list_title")
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .bouncyClick(onClick = onShowAllReadingList)
          .testTag("reading_list_show_all")
      ) {
        Text(
          text = "Show All",
          style = TextStyle(
            color = subtextColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            shadow = textShadow
          )
        )
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
          contentDescription = null,
          tint = subtextColor,
          modifier = Modifier.size(13.dp)
        )
      }
    }

    // List of frosted glass cards
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      readingList.take(3).forEach { item ->
        val cardShape = RoundedCornerShape(24.dp)

        FrostedGlassSurface(
          modifier = Modifier
            .fillMaxWidth()
            .bouncyClick { onNavigateUrl(item.url) }
            .testTag("reading_card_${item.id}"),
          shape = cardShape,
          elevation = 6.dp,
          isDark = isDark
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Left Thumbnail
            Image(
              painter = painterResource(id = item.thumbnailRes),
              contentDescription = item.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                  0.5.dp,
                  Color.White.copy(alpha = if (isDark) 0.15f else 0.4f),
                  RoundedCornerShape(16.dp)
                )
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Right Info
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.title,
                color = headerTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = item.excerpt,
                color = subtextColor,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
              )
              Spacer(modifier = Modifier.height(5.dp))
              Text(
                text = item.domain,
                color = subtextColor.copy(alpha = 0.8f),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
  }

  // Dialog for adding a custom favorite
  if (showAddFavoriteDialog) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddFavoriteDialog = false },
      title = { Text("Add Favorite") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("URL") },
            placeholder = { Text("https://example.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (title.isNotBlank() && url.isNotBlank()) {
              onAddFavorite(title.trim(), url.trim())
              showAddFavoriteDialog = false
            }
          }
        ) {
          Text("Add")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddFavoriteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
