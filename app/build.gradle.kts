plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.kochrezepte"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.kochrezepte"
        minSdk = 33
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.4.0"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.android.material:material:1.12.0")


    // --- KochRezepte için eklenen bağımlılıklar ---
    // Sürümleri Temmuz 2026 itibarıyla güncel/stabil olarak doğrulandı.

    // Navigasyon
    implementation("androidx.navigation:navigation-compose:2.9.7")

    // ViewModel + Compose entegrasyonu (viewModel() fonksiyonu için)
    // NOT: androidx.lifecycle kütüphaneleri lockstep (aynı numarayla) yayınlanır,
    // bu yüzden senin toml'undaki lifecycleRuntimeKtx (2.8.7) ile aynı sürümü kullandım.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Genişletilmiş Material ikon seti (BottomNavBar, RecipeListItem vb. için)
    // Versiyon belirtilmiyor çünkü yukarıdaki compose-bom platformu tarafından yönetiliyor.
    implementation("androidx.compose.material:material-icons-extended")

    // DataStore (Ayarlar)
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // kotlinx.serialization (JSON tarif/kategori kaydı) — Kotlin 2.2 ile uyumlu
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Coil 3 — DİKKAT: Coil eski io.coil-kt:coil-compose'dan io.coil-kt.coil3'e taşındı.
    // Kotlin dosyalarındaki import da buna göre "coil3.compose.AsyncImage" olarak güncellendi.
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
}