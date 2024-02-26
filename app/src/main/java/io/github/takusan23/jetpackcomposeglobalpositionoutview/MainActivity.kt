package io.github.takusan23.jetpackcomposeglobalpositionoutview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import io.github.takusan23.jetpackcomposeglobalpositionoutview.ui.theme.JetpackComposeGlobalPositionOutViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JetpackComposeGlobalPositionOutViewTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // コンポーネントの座標
    val position = remember { mutableStateOf(IntRect.Zero) }
    // ドラッグで移動
    val offset = remember { mutableStateOf(IntOffset(0, 0)) }
    // 横に長いコンポーネントの LayoutCoordinates
    // 画面外にいるコンポーネントの座標の取得に必要
    val longComponentLayoutCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "JetpackComposeGlobalPositionOutView") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 横にスクロールできるように
            // スクロールといえばレッツノート
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {

                // 横に長ーーーいコンポーネントを置く
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .requiredWidth(3000.dp)
                        .onGloballyPositioned { longComponentLayoutCoordinates.value = it }
                ) {

                    // スクロール出来てるか確認用に文字を横にズラーッと並べる
                    Row {
                        (0 until 50).forEach {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = it.toString()
                            )
                        }
                    }

                    if (longComponentLayoutCoordinates.value != null) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .offset { offset.value }
                                .background(Color.Red)
                                // コンポーネントの座標
                                .onGloballyPositioned {
                                    // 横に長いコンポーネントから見た座標を取り出す
                                    // localBoundingBoxOf 参照
                                    position.value = longComponentLayoutCoordinates.value!!
                                        .localBoundingBoxOf(it)
                                        .roundToIntRect()
                                }
                                // ドラッグで移動
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offset.value = IntOffset(
                                            x = (offset.value.x + dragAmount.x).toInt(),
                                            y = (offset.value.y + dragAmount.y).toInt()
                                        )
                                    }
                                }
                        )
                    }
                }
            }

            Text(
                text = """
                    left = ${position.value.left}
                    top = ${position.value.top}
                    right = ${position.value.right}
                    bottom = ${position.value.bottom}
                """.trimIndent()
            )
        }
    }
}