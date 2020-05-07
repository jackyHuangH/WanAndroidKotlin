package com.zenchn.support.crash;


import com.zenchn.support.SupportConfig;

import java.io.File;

/**
 * 作    者：wangr on 2017/5/4 10:50
 * 描    述：异常信息处理者（自定义处理）
 * 修订记录：
 */
public class DefaultCrashConfig implements ICrashConfig {

    @Override
    public boolean getReportMode() {
        return SupportConfig.isReport;
    }

    @Override
    public String getFilePath() {
        return SupportConfig.FILE_PATH;
    }

    @Override
    public String getFileNamePrefix() {
        return SupportConfig.FILE_NAME_PREFIX;
    }

    @Override
    public String getDateFormat() {
        return SupportConfig.FILE_DATE_FORMAT;
    }

    @Override
    public String getFileNameSuffix() {
        return SupportConfig.FILE_NAME_SUFFIX;
    }

    @Override
    public void uploadExceptionToServer(File logFile) {
        // Upload Exception Message To Your Web Server.
    }

}
