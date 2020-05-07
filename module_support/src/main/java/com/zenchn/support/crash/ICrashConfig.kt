package com.zenchn.support.crash

import java.io.File

/**
 * 作    者：wangr on 2017/4/27 16:11
 * 描    述：
 * 修订记录：
 */
interface ICrashConfig {
    val reportMode: Boolean
    val filePath: String?
    val fileNamePrefix: String?
    val dateFormat: String?
    val fileNameSuffix: String?
    fun uploadExceptionToServer(logFile: File?)
}