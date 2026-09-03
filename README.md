# UxComponent

A reusable Android UI component library built with Kotlin and XML
layouts.

## Features

-   XML-based custom Android views
-   `SpinnerField` component
-   Custom labels and required-field support
-   Custom field background
-   Custom dropdown icon
-   Item selection callbacks
-   Field validation
-   Enable/disable support
-   Easy integration through JitPack

## Requirements

-   Android SDK
-   Minimum SDK: 21
-   Kotlin/Java 11 compatible project

## Installation

### 1. Add JitPack repository

In your application's `settings.gradle.kts`:

``` kotlin
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

In `app/build.gradle.kts`:

``` kotlin
dependencies {
    implementation("com.github.spradhan7656:UxComponent:1.0.0")
}
```

Sync the project after adding the dependency.

## Usage

### SpinnerField in XML

``` xml
<com.spradhan.uxcomponentLib.SpinnerField
    android:id="@+id/supplierSpF"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:labelText="Supplier" />
```

Make sure your XML root declares the `app` namespace:

``` xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

### Set items

``` kotlin
val supplierField = findViewById<SpinnerField>(R.id.supplierSpF)

supplierField.setItems(
    listOf("Supplier A", "Supplier B", "Supplier C")
)
```

### Listen for selection

``` kotlin
supplierField.onItemSelected<String> { item, position ->
    // Handle selected item
}
```

### Required field

``` xml
<com.spradhan.uxcomponentLib.SpinnerField
    android:id="@+id/supplierSpF"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:labelText="Supplier"
    app:required="true" />
```

Or from Kotlin:

``` kotlin
supplierField.setRequired(true)
```

### Validate

``` kotlin
if (supplierField.validate()) {
    // Field is valid
}
```

### Update items

``` kotlin
supplierField.updateItems(
    listOf("Supplier A", "Supplier B", "Supplier C", "Supplier D")
)
```

### Set selected item

``` kotlin
supplierField.setSelection(1)
```

### Enable or disable

``` kotlin
supplierField.setFieldEnabled(false)
```

### Custom dropdown icon

``` kotlin
supplierField.setDropdownIcon(R.drawable.ic_arrow_down)
```

### Custom background

``` kotlin
supplierField.setBgdDrawable(
    getDrawable(R.drawable.my_background)!!
)
```

## XML Attributes

`SpinnerField` currently supports:

  Attribute           Type        Description
  ------------------- ----------- -----------------------------------
  `labelText`         string      Text displayed as the field label
  `required`          boolean     Marks the field as required
  `fieldBackground`   reference   Custom field background drawable
  `dropdownIcon`      reference   Custom dropdown icon

Example:

``` xml
<com.spradhan.uxcomponentLib.SpinnerField
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:labelText="Supplier"
    app:required="true"
    app:dropdownIcon="@drawable/ic_arrow_down" />
```

## Project Structure

The repository contains a demo application and the library module:

``` text
UxComponent/
├── app/
│   └── Demo application
│
├── uxcomponentLib/
│   └── Android library
│
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## Local Development

To build the library locally on Windows PowerShell:

``` powershell
.\gradlew.bat :uxcomponentLib:assembleRelease
```

To publish the library to the local Maven repository:

``` powershell
.\gradlew.bat :uxcomponentLib:publishToMavenLocal
```

## Versioning

Releases are versioned using Git tags.

Example:

``` bash
git tag 1.0.0
git push origin 1.0.0
```

JitPack can then build the tagged version.

## Repository

GitHub:

https://github.com/spradhan7656/UxComponent

JitPack:

https://jitpack.io/#spradhan7656/UxComponent

## License

Add your preferred open-source license here.

For example, this project can use the MIT License.

## Author

**spradhan7656**

GitHub: https://github.com/spradhan7656
