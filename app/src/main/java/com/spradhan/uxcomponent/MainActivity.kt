package com.spradhan.uxcomponent

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.spradhan.uxcomponentLib.CustomButton
import com.spradhan.uxcomponentLib.GradientConfig
import com.spradhan.uxcomponentLib.SnackbarBuilder
import com.spradhan.uxcomponentLib.SnackbarDuration
import com.spradhan.uxcomponentLib.SnackbarPosition
import com.spradhan.uxcomponentLib.SnackbarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<CustomButton>(R.id.btnLogin)
//        val loadingBar = SnackbarBuilder(this)
//            .message("Syncing your data…")
//            .gradient(
//                GradientConfig(
//                    colors = intArrayOf(
//                        0xFF232526.toInt(),
//                        0xFF414345.toInt()
//                    ),
//                    cornerRadiusDp = 20f
//                )
//            )
//            .glow(0xFF4FC3F7.toInt(), sizeDp = 5f)
//            .progress(max = 100, initial = 0)
//            .padding(
//                horizontalDp = 10f,
//                verticalDp = 10f
//            )
//            .duration(SnackbarDuration.INDEFINITE)
//            .dismissOnClick(false)
//            .swipeToDismiss(false)
//            .show()
//
//        loadingBar.updateProgress(10)
//        loadingBar.updateProgress(25)
//        loadingBar.updateProgress(50)




//        loadingBar.updateProgress(25)
//        loadingBar.updateProgress(50)
//        loadingBar.updateProgress(75)
//        loadingBar.updateProgress(100)

// later, once the sync finishes:
//        loadingBar.dismiss()

      /*  SnackbarBuilder(this)
            .message("fjwlefjlwejfiowjeiogfwjiofjwiojfiowjfiowjfiowjfiojwiofjwiofjiow")
            .icon(R.drawable.ic_launcher_foreground, tint = Color.WHITE)
            .gradient(
                GradientConfig(
                    colors = intArrayOf(0xFF11998E.toInt(), 0xFF38EF7D.toInt()),
                    cornerRadiusDp = 10f
                )
            )
            .position(SnackbarPosition.BOTTOM)
            .marginBottom(23f)
            .duration(SnackbarDuration.SHORT)
            .animationDuration(300)
            .show()*/
//
//        SnackbarBuilder(this)
//            .message("Item deleted")
//            .action("UNDO", color = Color.YELLOW) {
//
//            }
//            .solidColor(Color.DKGRAY)
//            .onDismissed {
//                // fires whether dismissed by timeout, swipe, or action tap
//
//            }
//            .show()

        SnackbarBuilder(this)
            .message("Upload completed successfully")
            .messageTextSize(12f)
            .type(SnackbarType.SUCCESS)
            .icon(com.spradhan.uxcomponentLib.R.drawable.login_ic, tint = ContextCompat.getColor(this,
                com.spradhan.uxcomponentLib.R.color.yellow))
            .position(SnackbarPosition.BOTTOM)
            .show()




//        button.setLoading(true)

    }
}