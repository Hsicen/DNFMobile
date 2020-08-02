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
    const val ktx = "1.3.1"

    const val appcompat = "1.1.0"
    const val constraintLayout = "1.1.3"
    const val logger = "2.2.0"


    //Test version
    const val junitJava = "4.13"
    const val junitAndroid = "1.1.1"
    const val espresso = "3.2.0"
}


//统一管理项目中使用的依赖库
object Deps {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.ktx}"

    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val logger = "com.orhanobut:logger:${Versions.logger}"


    //Test deps
    const val junitJava = "junit:junit:${Versions.junitJava}"
    const val junitAndroid = "androidx.test.ext:junit:${Versions.junitAndroid}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}

