//NOM:RAVONINIRINA MORY Queren Francia - L2 - N_40


package com.example.habitudes

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

val AppBackground = Color(0xFFF4F7FC)
val SurfacePureWhite = Color(0xFFFFFFFF)
val SurfaceSoftGrey = Color(0xFFEEF2F6)
val BorderSubtle = Color(0xFFE2E8F0)

val NavyDeep = Color(0xFF0F172A)
val NavySoft = Color(0xFF334155)
val NavySoftBadge = Color(0xFFE2E8F0)
val TextMuted = Color(0xFF64748B)

data class Habit(
    val id: Int,
    val name: String,
    val plusCount: Int = 0,
    val minusCount: Int = 0
) {
    fun getDisplayText(): String {
        return when {
            plusCount > 0 && minusCount > 0 -> "+$plusCount / -$minusCount"
            plusCount > 0 -> "+$plusCount"
            minusCount > 0 -> "-$minusCount"
            else -> ""
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerScreen()
        }
    }
}

@Composable
fun HabitTrackerScreen(viewModel: HabitViewModel = viewModel()) {
    val habits by viewModel.habits.collectAsState()
    val vieCurrent by viewModel.vieCurrent.collectAsState()
    val expCurrent by viewModel.expCurrent.collectAsState()
    val mpCurrent by viewModel.mpCurrent.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()

    HabitTrackerContent(
        habits = habits,
        vieCurrent = vieCurrent,
        expCurrent = expCurrent,
        mpCurrent = mpCurrent,
        profileImageUri = profileImageUri,
        onPlusClicked = { habitId -> viewModel.onPlusClicked(habitId, 100, 50, 100) },
        onMinusClicked = { habitId -> viewModel.onMinusClicked(habitId) },
        onAddHabit = { name -> viewModel.addHabit(name) },
        onDeleteHabit = { habit -> viewModel.deleteHabit(habit) },
        onResetAll = { viewModel.resetAll(50, 12, 15) },
        onProfileImageSelected = { uri -> viewModel.updateProfileImageUri(uri) }
    )
}

@Composable
fun HabitTrackerContent(
    habits: List<Habit>,
    vieCurrent: Int,
    expCurrent: Int,
    mpCurrent: Int,
    profileImageUri: String?,
    onPlusClicked: (Int) -> Unit,
    onMinusClicked: (Int) -> Unit,
    onAddHabit: (String) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onResetAll: () -> Unit,
    onProfileImageSelected: (Uri) -> Unit
) {
    val vieMax = 100
    val expMax = 50
    val mpMax = 100

    var showAddDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onProfileImageSelected(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Mes Habitudes",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyDeep,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            HeroProfileCard(
                vieCurrent = vieCurrent,
                vieMax = vieMax,
                expCurrent = expCurrent,
                expMax = expMax,
                mpCurrent = mpCurrent,
                mpMax = mpMax,
                profileImageUri = profileImageUri,
                onChangePhotoClick = { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Habitudes quotidiennes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    ModernHabitCard(
                        habit = habit,
                        onPlusClicked = { onPlusClicked(habit.id) },
                        onMinusClicked = { onMinusClicked(habit.id) },
                        onLongClick = { habitToDelete = habit }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = NavySoft,
                contentColor = SurfacePureWhite,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp), spotColor = NavySoft)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ajouter_un_bouton),
                        contentDescription = "Ajouter",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nouvelle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SurfacePureWhite
                    )
                }
            }

            FloatingActionButton(
                onClick = { showResetDialog = true },
                containerColor = SurfacePureWhite,
                contentColor = NavySoft,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp), spotColor = NavyDeep.copy(alpha = 0.15f))
                    .border(1.5.dp, BorderSubtle, RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cercle_de_fleches),
                        contentDescription = "Réinitialiser",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Réinitialiser",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySoft
                    )
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    newHabitName = ""
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = SurfacePureWhite,
                title = {
                    Text(
                        text = "Créer une habitude",
                        color = NavyDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("Nom de l'habitude", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavySoft,
                            unfocusedBorderColor = BorderSubtle,
                            focusedLabelColor = NavySoft,
                            cursorColor = NavySoft,
                            focusedTextColor = NavyDeep,
                            unfocusedTextColor = NavyDeep
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newHabitName.isNotBlank()) {
                                onAddHabit(newHabitName)
                                newHabitName = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavySoft),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ajouter", color = SurfacePureWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            newHabitName = ""
                        }
                    ) {
                        Text("Annuler", color = TextMuted)
                    }
                }
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                shape = RoundedCornerShape(24.dp),
                containerColor = SurfacePureWhite,
                title = {
                    Text(
                        text = "Tout réinitialiser ?",
                        color = NavyDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Cette action réinitialisera les compteurs (+/-) de toutes les habitudes ainsi que les jauges de progression.",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetAll()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Réinitialiser", color = SurfacePureWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetDialog = false }
                    ) {
                        Text("Annuler", color = TextMuted)
                    }
                }
            )
        }

        habitToDelete?.let { habit ->
            AlertDialog(
                onDismissRequest = { habitToDelete = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = SurfacePureWhite,
                title = {
                    Text(
                        text = "Supprimer l'habitude",
                        color = NavyDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Voulez-vous retirer « ${habit.name} » de votre liste ?",
                        color = TextMuted,
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteHabit(habit)
                            habitToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Supprimer", color = SurfacePureWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { habitToDelete = null }
                    ) {
                        Text("Annuler", color = TextMuted)
                    }
                }
            )
        }
    }
}

@Composable
fun HeroProfileCard(
    vieCurrent: Int,
    vieMax: Int,
    expCurrent: Int,
    expMax: Int,
    mpCurrent: Int,
    mpMax: Int,
    profileImageUri: String?,
    onChangePhotoClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = NavyDeep.copy(alpha = 0.15f))
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .background(SurfacePureWhite)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(4.dp, NavyDeep, CircleShape)
                        .background(SurfaceSoftGrey),
                    contentAlignment = Alignment.Center
                ) {
                    val painter = if (!profileImageUri.isNullOrEmpty()) {
                        rememberAsyncImagePainter(model = profileImageUri.toUri())
                    } else {
                        painterResource(id = R.drawable.utilisateur)
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Image Profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfacePureWhite)
                        .border(1.5.dp, BorderSubtle, CircleShape)
                        .shadow(2.dp, CircleShape)
                        .clickable { onChangePhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bouton_modifier),
                        contentDescription = "Changer photo de profil",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProgressBarStat(label = "VIE", current = vieCurrent, max = vieMax, barColor = NavyDeep)
                ProgressBarStat(label = "EXP", current = expCurrent, max = expMax, barColor = NavySoft)
                ProgressBarStat(label = "MP", current = mpCurrent, max = mpMax, barColor = TextMuted)
            }
        }
    }
}

@Composable
fun ProgressBarStat(label: String, current: Int, max: Int, barColor: Color) {
    val targetRatio = if (max > 0) (current.toFloat() / max.toFloat()).coerceIn(0f, 1f) else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = tween(durationMillis = 400),
        label = "ProgressAnimation"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
            Text(text = "$current / $max", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = NavyDeep)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape)
                .background(SurfaceSoftGrey)
        ) {
            if (animatedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedProgress)
                        .clip(CircleShape)
                        .background(barColor)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernHabitCard(
    habit: Habit,
    onPlusClicked: () -> Unit,
    onMinusClicked: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = NavyDeep.copy(alpha = 0.08f))
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfacePureWhite)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onPlusClicked() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Plus",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = { },
                        onLongClick = onLongClick
                    )
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = habit.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep
                )

                val displayText = habit.getDisplayText()
                if (displayText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavySoftBadge)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = displayText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onMinusClicked() },
                contentAlignment = Alignment.Center
            ) {
                Image (
                    painter = painterResource(id = R.drawable.moins),
                    contentDescription = "Moins",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HabitTrackerPreview() {
    HabitTrackerContent(
        habits = listOf(
            Habit(1, "Méditer", plusCount = 1),
            Habit(2, "Boire de l'eau", minusCount = 2),
            Habit(3, "Sport")
        ),
        vieCurrent = 50,
        expCurrent = 12,
        mpCurrent = 15,
        profileImageUri = null,
        onPlusClicked = {},
        onMinusClicked = {},
        onAddHabit = {},
        onDeleteHabit = {},
        onResetAll = {},
        onProfileImageSelected = {}
    )
}