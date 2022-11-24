package com.primex.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalAbsoluteElevation
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.primex.preferences.*
import com.primex.sample.ui.theme.SampleTheme
import com.primex.ui.Button
import com.primex.ui.Preference
import com.primex.ui.SliderPreference
import com.primex.ui.activity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext


private val KEY_COUNTER =
    intPreferenceKey("Counter3", false, object : IntSaver<Boolean>{
        override fun save(value: Boolean): Int = if (value) 1 else 0

        override fun restore(value: Int): Boolean = value == 1
    })

class MainActivity : ComponentActivity() {

    lateinit var preferences: Preferences

    @OptIn(ExperimentalComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(this)
        setContent {
            SampleTheme {
                Box(modifier = Modifier.fillMaxSize()) {

                    val counter by preferences.observeAsState(key = KEY_COUNTER)

                    Button(label = "$counter", onClick = {
                        preferences[KEY_COUNTER] = !counter
                    })


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

val bg = Color(0xffe0e0e0)

@Composable
@Preview(showBackground = true, backgroundColor = 0xffe0e0e1/* widthDp = 360, heightDp = 720*/)
fun DefaultPreview() {

    SampleTheme {
        PreviewPerf()
    }
}

@Composable
@NonRestartableComposable
fun <S, O> preference(key: Key<S, O>): State<O?> {
    val activity = LocalContext.activity
    require(activity is MainActivity)
    val preferences =activity.preferences
    val flow = when(key){
        is Key.Key1 -> preferences[key]
        is Key.Key2 -> preferences[key]
    }

    val first = remember(key.name) {
        runBlocking { flow.first() }
    }

    return produceState(first, flow, EmptyCoroutineContext){
        flow.collectLatest {
            value = it
        }
    }
}


@Composable
private fun PreviewPerf() {
    Column {

        SliderPreference(
            title = AnnotatedString("Color Secondary"),
            icon = Icons.Default.AddCircle,
            summery = AnnotatedString("Select your favourite secondary color."),
            defaultValue = 0.2f,
            onValueChange = {},
            steps = 5,
            iconChange = Icons.Default.Favorite
        )

        Preference(
            title = AnnotatedString("Color Secondary"),
            summery = AnnotatedString("Select your favourite secondary color."),
        )

    }
}
