plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.matias.tiendavirtualdonapp_kotlin"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.matias.tiendavirtualdonapp_kotlin"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie) /*Animaciones*/
    implementation(libs.firebaseAuthV) /*Autenticación con firebase*/
    implementation(libs.firebaseDatabase) /*Base de datos con firebase*/
    implementation(libs.imagePicker) /*Recortar una imagen*/
    implementation(libs.glide) /*Leer imagenes*/
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}