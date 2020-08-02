package com.hsicen.extensions.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * 作者：hsicen  2020/8/2 11:18
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：进程工具
 */
object ProcessUtil {

    fun isMainProcess(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val packageName = context.applicationContext.packageName
        val processName = getProcessName(context)
        return packageName == processName
    }

    private fun getProcessName(context: Context): String? {
        var processName = processFromFile
        if (processName == null) { // 如果装了xposed一类的框架，上面可能会拿不到，回到遍历迭代的方式
            processName = getProcessNameByAM(context)
        }
        return processName
    }

    private val processFromFile: String?
        get() {
            var reader: BufferedReader? = null
            return try {
                val pid = Process.myPid()
                val file = "/proc/$pid/cmdline"
                reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(file),
                        "iso-8859-1"
                    )
                )
                var c: Int
                val processName = StringBuilder()
                while (reader.read().also { c = it } > 0) {
                    processName.append(c.toChar())
                }
                processName.toString()
            } catch (e: Exception) {
                null
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                    }
                }
            }
        }

    private fun getProcessNameByAM(context: Context): String? {
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        while (true) {
            val plist = am.runningAppProcesses
            if (plist != null) {
                for (info in plist) {
                    if (info.pid == Process.myPid()) {
                        processName = info.processName
                        break
                    }
                }
            }
            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
            try {
                Thread.sleep(100L) // take a rest and again
            } catch (ex: InterruptedException) {
            }
        }
    }

}