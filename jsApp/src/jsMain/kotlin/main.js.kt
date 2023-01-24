import androidx.compose.ui.window.Window
import com.squaredem.composecalendardemo.MainContent
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Compose Calendar Demo") {
            MainContent()
        }
    }
}