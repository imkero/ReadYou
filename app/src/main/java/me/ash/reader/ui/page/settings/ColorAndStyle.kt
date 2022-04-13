package me.ash.reader.ui.page.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.ash.reader.R
import me.ash.reader.ui.component.*
import me.ash.reader.ui.ext.*
import me.ash.reader.ui.theme.palette.*
import me.ash.reader.ui.theme.palette.TonalPalettes.Companion.toTonalPalettes
import me.ash.reader.ui.theme.palette.dynamic.extractTonalPalettesFromUserWallpaper

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorAndStyle(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val wallpaperTonalPalettes = extractTonalPalettesFromUserWallpaper()
    var radioButtonSelected by remember { mutableStateOf(if (context.themeIndex > 4) 0 else 1) }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface onLight MaterialTheme.colorScheme.inverseOnSurface)
            .statusBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface onLight MaterialTheme.colorScheme.inverseOnSurface,
        topBar = {
            SmallTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface onLight MaterialTheme.colorScheme.inverseOnSurface
                ),
                title = {},
                navigationIcon = {
                    FeedbackIconButton(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    ) {
                        navController.popBackStack()
                    }
                },
                actions = {}
            )
        },
        content = {
            LazyColumn {
                item {
                    DisplayText(text = stringResource(R.string.color_and_style), desc = "")
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .aspectRatio(1.38f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                MaterialTheme.colorScheme.inverseOnSurface
                                        onLight MaterialTheme.colorScheme.surface.copy(0.7f)
                            )
                            .clickable { },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.padding(60.dp),
                            painter = painterResource(id = R.drawable.palettie),
                            contentDescription = stringResource(R.string.welcome),
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    BlockButtonRadios(
                        selected = radioButtonSelected,
                        onSelected = { radioButtonSelected = it },
                        items = listOf(
                            BlockButtonRadiosItem(
                                text = stringResource(R.string.wallpaper_colors),
                                onClick = {},
                            ) {
                                Palettes(
                                    palettes = wallpaperTonalPalettes.run {
                                        if (this.size > 5) {
                                            this.subList(5, wallpaperTonalPalettes.size)
                                        } else {
                                            emptyList()
                                        }
                                    },
                                    themeIndexPrefix = 5,
                                )
                            },
                            BlockButtonRadiosItem(
                                text = stringResource(R.string.basic_colors),
                                onClick = {},
                            ) {
                                Palettes(
                                    palettes = wallpaperTonalPalettes.subList(0, 5)
                                )
                            },
                        ),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Subtitle(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = stringResource(R.string.style)
                    )
                    SettingItem(
                        title = stringResource(R.string.dark_theme),
                        desc = stringResource(R.string.use_device_theme),
                        enable = false,
                        separatedActions = true,
                        onClick = {},
                    ) {
                        Switch(activated = isSystemInDarkTheme(), enable = false)
                    }
                    SettingItem(
                        title = stringResource(R.string.tonal_elevation),
                        enable = false,
                        onClick = {},
                    ) {}
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Subtitle(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = stringResource(R.string.fonts)
                    )
                    SettingItem(
                        title = stringResource(R.string.basic_fonts),
                        desc = "Google Sans",
                        enable = false,
                        onClick = {},
                    ) {}
                    SettingItem(
                        title = stringResource(R.string.reading_fonts),
                        desc = "Google Sans",
                        enable = false,
                        onClick = {},
                    ) {}
                    SettingItem(
                        title = stringResource(R.string.reading_fonts_size),
                        desc = "16sp",
                        enable = false,
                        onClick = {},
                    ) {}
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    )
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Palettes(
    modifier: Modifier = Modifier,
    palettes: List<TonalPalettes>,
    themeIndexPrefix: Int = 0,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeIndex = context.dataStore.data
        .map { it[DataStoreKeys.ThemeIndex.key] ?: 0 }
        .collectAsState(initial = 0).value
    val customPrimaryColor = context.dataStore.data
        .map { it[DataStoreKeys.CustomPrimaryColor.key] ?: "" }
        .collectAsState(initial = "").value
    val tonalPalettes = customPrimaryColor.safeHexToColor().toTonalPalettes()
    var addDialogVisible by remember { mutableStateOf(false) }
    var customColorValue by remember { mutableStateOf(context.customPrimaryColor) }

    if (palettes.isEmpty()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    MaterialTheme.colorScheme.inverseOnSurface
                            onLight MaterialTheme.colorScheme.surface.copy(0.7f),
                )
                .clickable {},
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
                    stringResource(R.string.no_palettes)
                else stringResource(R.string.only_android_8_1_plus),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
        }
    } else {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            palettes.forEachIndexed { index, palette ->
                val isCustom = index == palettes.lastIndex && themeIndexPrefix == 0
                val i = themeIndex - themeIndexPrefix
                SelectableMiniPalette(
                    selected = if (i >= palettes.size) i == 0 else i == index,
                    isCustom = isCustom,
                    onClick = {
                        if (isCustom) {
                            addDialogVisible = true
                        } else {
                            scope.launch(Dispatchers.IO) {
                                context.dataStore.put(
                                    DataStoreKeys.ThemeIndex,
                                    themeIndexPrefix + index
                                )
                            }
                        }
                    },
                    palette = if (isCustom) tonalPalettes else palette
                )
            }
        }
    }

    TextFieldDialog(
        visible = addDialogVisible,
        title = "强调色",
        icon = Icons.Outlined.Palette,
        value = customColorValue,
        placeholder = "#123456",
        onValueChange = {
            customColorValue = it
        },
        onDismissRequest = {
            addDialogVisible = false
            customColorValue = context.customPrimaryColor
        },
        onConfirm = {
            it.checkColorHex()?.let {
                scope.launch(Dispatchers.IO) {
                    context.dataStore.put(DataStoreKeys.CustomPrimaryColor, it)
                    context.dataStore.put(DataStoreKeys.ThemeIndex, 4)
                }
                addDialogVisible = false
                customColorValue = it
            }
        }
    )
}

@Composable
fun SelectableMiniPalette(
    modifier: Modifier = Modifier,
    selected: Boolean,
    isCustom: Boolean = false,
    onClick: () -> Unit,
    palette: TonalPalettes,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isCustom) {
            MaterialTheme.colorScheme.primaryContainer
                .copy(0.5f) onDark MaterialTheme.colorScheme.onPrimaryContainer.copy(0.3f)
        } else {
            MaterialTheme.colorScheme
                .inverseOnSurface onLight MaterialTheme.colorScheme.surface.copy(0.7f)
        },
    ) {
        Surface(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .size(48.dp),
            shape = CircleShape,
            color = palette primary 90,
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .offset((-24).dp, 24.dp),
                    color = palette tertiary 90,
                ) {}
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(24.dp, 24.dp),
                    color = palette secondary 50,
                ) {}
                AnimatedVisibility(
                    visible = selected,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                    exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Checked",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}