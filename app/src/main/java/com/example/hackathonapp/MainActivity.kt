package com.example.hackathonapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.hackathonapp.ViewModels.LoginVM
import com.example.hackathonapp.navigations.ExclamationTriangle
import com.example.hackathonapp.navigations.Home
import com.example.hackathonapp.navigations.Map
import com.example.hackathonapp.navigations.Navigation
import com.example.hackathonapp.screens.gettingstartedScreen
import com.example.hackathonapp.ui.theme.HackathonAppTheme
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackathonAppTheme {
                val c = LocalContext.current
                // A surface container using the 'background' color from the theme
                MainScreen(this)
                //gettingstartedScreen(getVideoUri())

            }
        }
    }

    private fun getVideoUri(): Uri {
        val rawId = resources.getIdentifier("ships", "raw", packageName)
        val videoUri = "android.resource://$packageName/$rawId"
        return Uri.parse(videoUri)
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MainScreen(activity: Activity) {
    val vm = hiltViewModel<LoginVM>()
    val c = LocalContext.current
    vm.login("meriem", "123456") {
        Log.d("login", "login")
    }
    vm.test(activity)


    val navController = rememberNavController()

    val destinationsList = listOf(Home, ExclamationTriangle, Map)
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    var pickedTime by remember {
        mutableStateOf(LocalTime.NOON)
    }

    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm")
                .format(pickedTime)
        }
    }

    val timePicker = rememberMaterialDialogState()



    Scaffold(

        bottomBar = {

            NavigationBar() {
                destinationsList.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = "icon"
                            )
                        },
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            if (selectedIndex == 1) {
                                timePicker.show()

                            } else {
                                navController.navigate(destinationsList[selectedIndex].route) {
                                    popUpTo(Home.route)
                                    launchSingleTop = true
                                }
                            }
                        })
                }
            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Navigation(navController = navController)

            MaterialDialog(
                dialogState = timePicker,
                //backgroundColor = Color(0xff4172ED).copy(alpha = 0.4f),
                backgroundColor = Color.White,
                properties = DialogProperties(true, true)

            ) {
                var selectedTime by remember {
                    mutableStateOf(LocalTime.now())
                }
                timepicker(
                    initialTime = LocalTime.now(),
                    title = "Set unexpected arrival time",
                    colors = TimePickerDefaults.colors(
                        activeTextColor = Color.White,
                        activeBackgroundColor = Color(0xff002C70),
                        inactiveTextColor = Color.White,
                        headerTextColor = Color.Gray,
                        selectorColor = Color.White,
                        selectorTextColor = Color.Gray
                    )
                ) {
                    selectedTime = it
                }

                Row(Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /*TODO*/ },
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff002C70)
                        ),
                        shape = RoundedCornerShape(35)
                    ) {

                        Text(
                            text = "Save",
                            color = Color.White
                        )

                    }

                    Button(
                        onClick = { /*TODO*/ },
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(35),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {

                        Text(
                            text = "Cancel",
                            color = Color.White
                        )

                    }
                }

            }
        }
    }
}






