/**
 * 作者：hsicen  2020/8/1 17:33
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：依赖库管理 和 版本号管理
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
  const val kotlinVersion = "1.4.0"

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
  const val leakCanary = "2.4"
  const val room = "2.2.5"
  const val navigation = "2.3.0"
  const val gson = "2.8.6"
  const val multiDex = "2.0.1"
  const val retrofit = "2.9.0"
  const val okHttp = "4.8.1"
  const val luBan = "1.1.8"
  const val kotPref = "2.11.0"
  const val aRouter = "1.5.0"
  const val aRouterCompile = "1.2.2"
  const val aRouterRegister = "1.0.2"
  const val smartRefresh = "2.0.1"
  const val lifeCycle = "2.2.0"


  /*** =========测试模块 Version=========***/
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
  const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
  const val material = "com.google.android.material:material:${Versions.material}"

  //日志打印
  const val logger = "com.orhanobut:logger:${Versions.logger}"

  //Adapter
  const val adapterHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:${Versions.adapterHelper}"

  //Glide
  const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
  const val glideOkHttp = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
  const val glideRV = "com.github.bumptech.glide:recyclerview-integration:${Versions.glide}"

  //coroutines
  const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
  const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

  //dagger
  const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
  const val daggerApt = "com.google.dagger:dagger-compiler:${Versions.dagger}"
  const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
  const val daggerAndroidApt = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

  //LeakCanary
  const val leakCanaryDebug = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
  const val leakCanaryRelease = "com.squareup.leakcanary:leakcanary-object-watcher-android::${Versions.leakCanary}"

  //Room
  const val room = "androidx.room:room-runtime:${Versions.room}"
  const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
  const val roomApt = "androidx.room:room-compiler:${Versions.room}"

  //Navigation
  const val navigation = "androidx.navigation:navigation-fragment:${Versions.navigation}"
  const val navigationUi = "androidx.navigation:navigation-ui:${Versions.navigation}"
  const val navigationKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"

  //Gson
  const val gson = "com.google.code.gson:gson:${Versions.gson}"

  //MultiDex
  const val multiDex = "androidx.multidex:multidex:${Versions.multiDex}"

  //Retrofit
  const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
  const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

  //OkHttp
  const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
  const val okHttpUrlConnection = "com.squareup.okhttp3:okhttp-urlconnection:${Versions.okHttp}"
  const val okHttpLogInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

  //luBan
  const val luBan = "top.zibin:Luban:${Versions.luBan}"

  //KotPref
  const val kotPref = "com.chibatching.kotpref:kotpref:${Versions.kotPref}"
  const val kotPrefEnum = "com.chibatching.kotpref:enum-support:${Versions.kotPref}"
  const val kotPrefGson = "com.chibatching.kotpref:gson-support:${Versions.kotPref}"

  //ARouter
  const val aRouter = "com.alibaba:arouter-api:${Versions.aRouter}"
  const val aRouterCompile = "com.alibaba:arouter-compiler:${Versions.aRouterCompile}"

  //Smart Refresh
  const val smartRefresh = "com.scwang.smart:refresh-layout-kernel:${Versions.smartRefresh}"
  const val smartRefreshHeader = "com.scwang.smart:refresh-header-classics:${Versions.smartRefresh}"
  const val smartRefreshFooter = "com.scwang.smart:refresh-footer-classics:${Versions.smartRefresh}"

  //LifeCycle
  const val lifeCycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycle}"
  const val lifeCycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifeCycle}"
  const val lifeCycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycle}"
  const val lifeCycleViewModelSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifeCycle}"

  /*** =========测试模块依赖=========***/
  const val junitJava = "junit:junit:${Versions.junitJava}"
  const val junitAndroid = "androidx.test.ext:junit:${Versions.junitAndroid}"
  const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}