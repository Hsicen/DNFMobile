/**
 * 作者：hsicen  2020/8/1 17:33
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：依赖库管理
 */

//统一管理项目中的版本信息
object Versions {
    //Build Configs
    const val minSDK = 21
    const val compileSDK = 30
    const val targetSDK = 30
    const val buildTools = "30.0.0"

    //App Version
    const val versionCode = 1
    const val versionName = "1.0.0"

    //Plugins
    const val buildGradle = "4.0.1"

    //kotlin
    const val kotlinVersion = "1.3.72"

    //官方库
    const val ktx = "1.3.1"
    const val appcompat = "1.1.0"
    const val constraintLayout = "1.1.3"
    const val coroutines = "1.3.5"
    const val material = "1.1.0"


    //三方库
    const val logger = "2.2.0"
    const val adapterHelper = "2.9.45-androidx"
    const val glide = "4.8.0"
    const val dagger = "2.28.3"


    //Test version
    const val junitJava = "4.13"
    const val junitAndroid = "1.1.1"
    const val espresso = "3.2.0"
}


//统一管理项目中使用的依赖库
object Deps {
    //官方系统库
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.ktx}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val material = "com.google.android.material:material:${Versions.material}"

    //日志打印
    const val logger = "com.orhanobut:logger:${Versions.logger}"

    //Adapter
    const val adapterHelper =
        "com.github.CymChad:BaseRecyclerViewAdapterHelper:${Versions.adapterHelper}"

    //Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideOkHttp = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
    const val glideRV = "com.github.bumptech.glide:recyclerview-integration:${Versions.glide}"

    //coroutines
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    //dagger
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerKapt = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidKapt = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    //Test deps
    const val junitJava = "junit:junit:${Versions.junitJava}"
    const val junitAndroid = "androidx.test.ext:junit:${Versions.junitAndroid}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}