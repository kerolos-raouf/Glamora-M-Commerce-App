import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id ("androidx.navigation.safeargs.kotlin")

    alias(libs.plugins.apollo)
    alias(libs.plugins.google.gms.google.services)
    //id("com.apollographql.apollo3") version "4.0.1"
}

android {
    namespace = "com.example.glamora"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.glamora"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperty = Properties()
        val file = rootProject.file("local.properties")
        if(file.exists())
        {
            file.inputStream().use {
                localProperty.load(it)
            }
        }

        val adminApiAccessToken : String = localProperty.getProperty("ADMIN_API_ACCESS_TOKEN") ?: "null"
        val apiKey : String = localProperty.getProperty("API_KEY") ?: "null"
        val apiSecretKey : String = localProperty.getProperty("API_SECRET_KEY") ?: "null"


        buildConfigField("String","ADMIN_API_ACCESS_TOKEN",adminApiAccessToken)
        buildConfigField("String","API_KEY",apiKey)
        buildConfigField("String","API_SECRET_KEY",apiSecretKey)
    }

    buildFeatures{
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val localProperty = Properties()
val file = rootProject.file("local.properties")
if(file.exists())
{
    file.inputStream().use {
        localProperty.load(it)
    }
}

val adminApiAccessToken : String = localProperty.getProperty("ADMIN_API_ACCESS_TOKEN") ?: "null"

apollo{
    service("service"){
        packageName.set("com.example")
        introspection {
            endpointUrl.set("https://android-alex-team1.myshopify.com/admin/api/2023-01/graphql.json")
            headers.put("X-Shopify-Access-Token", adminApiAccessToken)
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //google play service
    implementation(libs.play.services.location)


    ////more features in the activity like lazy initialization
    implementation(libs.androidx.activity.ktx)
    ////to use viewModel with fragment
    implementation(libs.androidx.fragment.ktx)


    //retrofit and gson converter
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    ///room database
    val room_version = "2.6.1"

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    ///navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    ///hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    ///open street map api
    implementation(libs.osmdroid.android)

    ///glide dependency for images
    implementation(libs.glide)

    //lottie animation
    implementation(libs.lottie)

    //indicator
    implementation(libs.dotsindicator)
    implementation(libs.androidx.viewpager2)

    // OkHttp (Required for networking with Apollo)
    implementation(libs.okhttp)

    // Apollo Client for GraphQL
    implementation(libs.apollo.runtime)
    //implementation("com.apollographql.apollo3:apollo-api::4.0.1")
    //circular image view
    implementation(libs.circleimageview)

    // google Auth
    implementation (libs.play.services.auth)


    //Stripe
    implementation (libs.stripe.android)

    //paypal
    implementation (libs.card.payments)
}