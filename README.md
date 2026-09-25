# UxComponent

A comprehensive, highly customizable Android UI component library built with Kotlin and XML layouts, featuring advanced styling, gradients, strokes, glow effects, searchable/multi-select spinners, custom input fields, skeleton loaders, custom buttons, horizontal calendars, and rich snackbars.

## Features

-   **`InputField`**: Fully customizable input field with start/end icons, password visibility toggle, validators, custom fonts, advanced backgrounds, stroke gradients, and glow effects.
-   **`SpinnerField`**: Flexible dropdown component supporting standard spinner mode, **Searchable** spinner dialogs, and **Multi-Check** selection dialogs, with full attribute parity (start/end icons, advanced background, strokes, glow effects, and custom fonts).
-   **`CustomButton`**: Rich button supporting loading states, solid colors, gradient backgrounds, custom corner radii, strokes, glow effects, and start/end icons.
-   **`SkeletonLayout`**: Shimmer effect loading placeholder skeleton layout with customizable duration, corner radius, base color, and highlight color.
-   **`HorizontalCalendarView`**: Interactive horizontal date picker with event dot indicator markers.
-   **`SnackbarBuilder`**: Powerful customizable snackbars supporting custom success/error types, solid colors, multi-stop gradients, glow effects, progress bars, icons, actions, custom positioning, and indefinite durations.

## Requirements

-   Android SDK
-   Minimum SDK: 21
-   Kotlin/Java 11 compatible project

## Installation

### 1. Add JitPack repository

In your root `settings.gradle.kts` (or project-level `build.gradle`):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
```

### 2. Add the dependency

In your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.spradhan7656:UxComponent:1.1.0")
}
```

Sync your project after adding the dependency.

---

## Component Usage Guide

### 1. `InputField`
A versatile input field component supporting labels, required indicators, start/end icons, password toggles, validation rules, custom fonts, advanced backgrounds, gradient strokes, and glow effects.

#### XML Usage
```xml
<com.spradhan.uxcomponentLib.InputField
    android:id="@+id/inputUserName"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    app:labelText="Username"
    app:required="true"
    app:hintText="Enter username"
    app:startIcon="@drawable/login_ic"
    app:startIconTint="@color/app_blue"
    app:inputCornerRadius="10dp"
    app:inputStrokeWidth="1.5dp"
    app:inputStrokeColor="@color/app_blue"
    app:inputGlowColor="@color/app_blue"
    app:inputGlowSize="4dp"
    app:labelTextColor="@color/app_deep_blue"
    app:labelTextFontFamily="@font/nunito_bold" />
```

#### Kotlin Usage
```kotlin
val inputUserName = findViewById<InputField>(R.id.inputUserName)

inputUserName.setText("JohnDoe")
    .setLabelFont(R.font.nunito_bold)
    .setInputFont(R.font.roboto)
    .addValidator("Username must be at least 3 characters") { it.length >= 3 }

if (inputUserName.validate()) {
    val text = inputUserName.getText()
}
```

---

### 2. `SpinnerField`
A powerful dropdown field supporting three modes: **Standard Spinner**, **Searchable Dialog**, and **Multi-Check Selection Dialog**, with full attribute parity with `InputField`.

#### XML Usage (Searchable & Multi-Select)
```xml
<!-- Searchable Spinner -->
<com.spradhan.uxcomponentLib.SpinnerField
    android:id="@+id/spinnerDepartment"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    app:labelText="Department"
    app:required="true"
    app:isSearchable="true"
    app:dialogTitle="Select Department"
    app:searchHint="Search department..."
    app:spHintText="Tap to choose department"
    app:inputCornerRadius="10dp"
    app:inputStrokeWidth="1.5dp"
    app:inputStrokeColor="@color/app_blue" />

<!-- Multi-Select Spinner -->
<com.spradhan.uxcomponentLib.SpinnerField
    android:id="@+id/spinnerSkills"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    app:labelText="Technical Skills"
    app:isMultiSelect="true"
    app:dialogTitle="Select Skills"
    app:selectAllText="Select All"
    app:clearAllText="Clear All"
    app:spHintText="Choose skills" />
```

#### Kotlin Usage
```kotlin
val spinnerDept = findViewById<SpinnerField>(R.id.spinnerDepartment)

spinnerDept.setItems(listOf("Engineering", "Product", "Design", "Marketing"))
    .onItemSelected<String> { item, position ->
        // Handle selected item
    }

val spinnerSkills = findViewById<SpinnerField>(R.id.spinnerSkills)
spinnerSkills.setItems(listOf("Kotlin", "Java", "Compose", "Coroutines"))
    .onMultiItemsSelected<String> { selectedItems, selectedPositions ->
        // Handle selected list
    }

if (spinnerDept.validate()) {
    val selected = spinnerDept.getSelectedItem<String>()
}
```

---

### 3. `CustomButton`
A rich button component supporting loading states, solid colors, multi-stop gradients, corner radii, strokes, glow effects, and icons.

#### XML Usage
```xml
<com.spradhan.uxcomponentLib.CustomButton
    android:id="@+id/btnLogin"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    app:cb_text="Submit"
    app:cb_loadingText="Processing..."
    app:cb_cornerRadius="12dp"
    app:cb_gradientColorStart="@color/app_blue"
    app:cb_gradientColorEnd="@color/app_deep_blue"
    app:cb_startIcon="@drawable/login_ic"
    app:cb_startIconTint="@color/white" />
```

#### Kotlin Usage
```kotlin
val button = findViewById<CustomButton>(R.id.btnLogin)

button.setOnClickListener {
    button.setLoading(true)
    // Perform task...
}
```

---

### 4. `SkeletonLayout`
A shimmer effect loading placeholder layout used while fetching data.

#### XML Usage
```xml
<com.spradhan.uxcomponentLib.SkeletonLayout
    android:id="@+id/skeletonLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <include layout="@layout/item_profile" />
</com.spradhan.uxcomponentLib.SkeletonLayout>
```

#### Kotlin Usage
```kotlin
val skeleton = findViewById<SkeletonLayout>(R.id.skeletonLayout)

skeleton.setShimmerDuration(1500L)
    .setCornerRadius(12f)
    .setBaseColor(Color.parseColor("#E0E0E0"))
    .setHighlightColor(Color.parseColor("#F5F5F5"))

skeleton.showSkeleton()

// When data arrives:
skeleton.hideSkeleton()
```

---

### 5. `HorizontalCalendarView`
An interactive horizontal date picker component supporting custom event indicator dots.

#### XML Usage
```xml
<com.spradhan.uxcomponentLib.HorizontalCalendarView
    android:id="@+id/horizontalCalendarView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

#### Kotlin Usage
```kotlin
val calendarView = findViewById<HorizontalCalendarView>(R.id.horizontalCalendarView)

calendarView.addEventDots(Date(), listOf(Color.RED, Color.GREEN))

calendarView.setOnDateSelectedListener { selectedDate ->
    // Handle date selection
}
```

---

### 6. `SnackbarBuilder`
A highly customizable snackbar builder supporting success/error/info types, solid colors, multi-stop gradient backgrounds, glow effects, progress bars, start/end icons, click actions, and custom positioning.

#### Kotlin Usage
```kotlin
SnackbarBuilder(this)
    .message("Operation completed successfully!")
    .type(SnackbarType.SUCCESS)
    .position(SnackbarPosition.BOTTOM)
    .duration(SnackbarDuration.SHORT)
    .icon(R.drawable.ic_check, tint = Color.WHITE)
    .show()

val snackbar = SnackbarBuilder(this)
    .message("Syncing data...")
    .solidColor(Color.DKGRAY)
    .position(SnackbarPosition.BOTTOM)
    .duration(SnackbarDuration.INDEFINITE)
    .endIcon(R.drawable.close_ic, tint = Color.WHITE) {
        // End icon click callback
    }
    .show()

snackbar.hide()
```

---

## XML Attributes Summary

`SpinnerField` and `InputField` support a rich set of attributes:

- `labelText`: String label text
- `required`: Boolean marking field as required (`*`)
- `fieldBackground`: Custom background drawable
- `startIcon`, `endIcon`: Leading and trailing icons with tint and sizing
- `isSearchable`, `isMultiSelect`: Enable searchable or multi-check dialog modes
- `dialogTitle`, `searchHint`, `spHintText`: Dialog customization texts
- `inputBackgroundColor`, `inputCornerRadius`, `inputStrokeWidth`, `inputStrokeColor`: Advanced shape and stroke styling
- `inputStrokeGradientStart`, `inputStrokeGradientEnd`: Gradient borders
- `inputGlowColor`, `inputGlowSize`: Soft glow shadow effect
- `labelTextColor`, `labelTextFontFamily`, `inputFontFamily`, `fontFamily`: Typography customization

## Repository

GitHub:

https://github.com/spradhan7656/UxComponent

JitPack:

https://jitpack.io/#spradhan7656/UxComponent

## License

MIT License

## Author

**spradhan7656**

GitHub: https://github.com/spradhan7656
