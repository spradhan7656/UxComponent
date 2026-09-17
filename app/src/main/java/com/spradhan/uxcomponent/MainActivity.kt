package com.spradhan.uxcomponent

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.spradhan.uxcomponentLib.CustomButton
import com.spradhan.uxcomponentLib.GradientConfig
import com.spradhan.uxcomponentLib.SkeletonLayout
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
        val inputUserName = findViewById<com.spradhan.uxcomponentLib.InputField>(R.id.inputUserName)
        val spinnerRole = findViewById<com.spradhan.uxcomponentLib.SpinnerField>(R.id.spinnerRole)

        // Setup custom InputField
        inputUserName.setLabel("Full Name")
            .setHint("Enter your name")
            .setRequired(true)
            .addValidator("Name is too short") { it.length >= 3 }

        // Setup custom SpinnerField
        val roles = listOf("Android Engineer", "Product Designer", "Project Manager", "QA Analyst")
        spinnerRole.setLabel("Primary Work Role")
            .setRequired(true)
            .setItems(roles)

        val skeleton = findViewById<SkeletonLayout>(R.id.skeletonLayout)
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)

        // Programmatically control the animation time and styles from the class file
        skeleton.setShimmerDuration(1500L)
        skeleton.setCornerRadius(12f)
        skeleton.setBaseColor(Color.parseColor("#E0E0E0"))
        skeleton.setHighlightColor(Color.parseColor("#F5F5F5"))

        // Create dummy list data
        val dummyData = listOf(
            Pair("Alex Carter", "Senior Android Developer"),
            Pair("Beatrice Smith", "UX/UI Product Designer"),
            Pair("Charles Cooper", "Backend System Engineer"),
            Pair("Diana Prince", "Product Operations Lead"),
            Pair("Evan Wright", "DevOps Infrastructure Lead")
        )
        val adapter = ProfileAdapter(dummyData)
        recyclerView.adapter = adapter

        // while loading:
        skeleton.showSkeleton()

        // Simulate data arriving after 3 seconds, then hide the skeleton and show list
        lifecycleScope.launch {
            delay(3000)
            skeleton.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE
        }

        button.setOnClickListener {
            val isNameValid = inputUserName.validate()
            val isRoleValid = spinnerRole.validate()

            if (isNameValid && isRoleValid) {
                button.setLoading(true)
                lifecycleScope.launch {
                    delay(1500)
                    button.setLoading(false)

                    SnackbarBuilder(this@MainActivity)
                        .message("Profile metadata validated successfully!")
                        .type(SnackbarType.SUCCESS)
                        .position(SnackbarPosition.BOTTOM)
                        .show()
                }
            } else {
                SnackbarBuilder(this@MainActivity)
                    .message("Please fill all required inputs correctly.")
                    .solidColor(Color.RED)
                    .position(SnackbarPosition.BOTTOM)
                    .show()
            }
        }

// once data arrives, populate your views as normal, then:

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