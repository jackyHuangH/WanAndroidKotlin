package com.zenchn.apilib.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * 作    者：wangr on 2017/5/25 14:36
 * 描    述： java常用的封装
 * 修订记录：
 */
public class JavaKit {

    private static final String TAG = "JavaKit";

    /**
     * 判断非空相关（字符串、数组、集合等）
     * --------------------------------------------------------------------
     */

    /**
     * 判断一个字符串是否为空
     *
     * @param charSequence
     * @return
     */
    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    /**
     * 判断多个字符串是否为空
     *
     * @param charSequences
     * @return
     */
    public static boolean isEmpty(CharSequence... charSequences) {
        for (CharSequence charSequence : charSequences) {
            if (isEmpty(charSequence))
                return true;
        }
        return false;
    }

    /**
     * 判断数组是否为空
     *
     * @param objects
     * @return
     */
    public static boolean isEmpty(Object[] objects) {
        return null == objects || objects.length == 0;
    }

    /**
     * 判断一个集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    /**
     * 判断一个集合是否为空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    /**
     * 判断一个集合有内容(非空)
     *
     * @param collection
     * @return
     */
    public static boolean isNonNull(Collection<?> collection) {
        return null != collection && !collection.isEmpty();
    }

    /**
     * 判断一个集合有内容(非空)
     *
     * @param map
     * @return
     */
    public static boolean isNonNull(Map<?, ?> map) {
        return null != map && !map.isEmpty();
    }

    /**
     * 关闭相关
     * --------------------------------------------------------------------
     */

    /**
     * 关闭IO
     *
     * @param closeables closeables
     */
    public static void close(Closeable... closeables) {
        if (closeables == null)
            return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeables
     */
    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null)
            return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 作    者：wangr on 2017/5/23 15:37
     * 描    述：生成随机长度字符串的封装类
     * 修订记录：
     */
    public static class Random {

        public static final String ARABIC_NUMBERS = "0123456789";
        public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
        public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String ALL_LETTERS = CAPITAL_LETTERS + LOWER_CASE_LETTERS;
        public static final String NUMBERS_AND_LETTERS = ARABIC_NUMBERS + ALL_LETTERS;

        /**
         * 生成随机长度的字符串（包含大小写字母和数字）
         *
         * @param length
         * @return
         */
        public static String getRandomNumbersAndLetters(int length) {
            return getRandom(NUMBERS_AND_LETTERS, length);
        }

        /**
         * 生成随机长度的数字串
         *
         * @param length
         * @return
         */
        public static String getRandomNumbers(int length) {
            return getRandom(ARABIC_NUMBERS, length);
        }

        /**
         * 生成随机长度的字母串（包含大小写）
         *
         * @param length
         * @return
         */
        public static String getRandomLetters(int length) {
            return getRandom(ALL_LETTERS, length);
        }

        /**
         * 生成随机长度的字母串（大写）
         *
         * @param length
         * @return
         */
        public static String getRandomCapitalLetters(int length) {
            return getRandom(CAPITAL_LETTERS, length);
        }

        /**
         * 生成随机长度的字母串（小写）
         *
         * @param length
         * @return
         */
        public static String getRandomLowerCaseLetters(int length) {
            return getRandom(LOWER_CASE_LETTERS, length);
        }

        /**
         * 生成随机长度的字符串（指定字符范围）
         *
         * @param length
         * @return
         */
        public static String getRandom(String source, int length) {
            return isEmpty(source) ? null : getRandom(source.toCharArray(), length);
        }

        /**
         * 生成随机长度的字符串（指定字符范围）
         *
         * @param length
         * @return
         */
        public static String getRandom(char[] sourceChar, int length) {
            if (isEmpty(Arrays.asList(sourceChar)) || length < 0) {
                return null;
            }

            StringBuilder sb = new StringBuilder(length);
            java.util.Random random = new java.util.Random();
            for (int i = 0; i < length; i++) {
                sb.append(sourceChar[random.nextInt(sourceChar.length)]);
            }
            return sb.toString();
        }

        /**
         * 生成 0 - |max| 或者 -|max| - 0 之间的随机数
         *
         * @param max
         * @return
         */
        public static int getRandom(int max) {
            return getRandom(0, max);
        }

        /**
         * 生成两个数字之间的随机数
         *
         * @param lower
         * @param upper
         * @return
         */
        public static int getRandom(int lower, int upper) {
            int min = Math.min(lower, upper);
            int scope = Math.abs(upper - lower);
            return new java.util.Random().nextInt(scope) + min;
        }
    }

    /**
     * 作    者：wangr on 2017/5/23 16:16
     * 描    述：文件操作的封装类
     * 修订记录：
     */
    public static class FileKit {

        private static final String LINE_SEP = System.getProperty("line.separator");

        private FileKit() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        /**
         * Return the file by path.
         *
         * @param filePath The path of file.
         * @return the file
         */
        public static File getFileByPath(final String filePath) {
            return isSpace(filePath) ? null : new File(filePath);
        }

        /**
         * Return whether the file exists.
         *
         * @param filePath The path of file.
         * @return {@code true}: yes<br>{@code false}: no
         */
        public static boolean isFileExists(final String filePath) {
            return isFileExists(getFileByPath(filePath));
        }

        /**
         * Return whether the file exists.
         *
         * @param file The file.
         * @return {@code true}: yes<br>{@code false}: no
         */
        public static boolean isFileExists(final File file) {
            return file != null && file.exists();
        }

        /**
         * Rename the file.
         *
         * @param filePath The path of file.
         * @param newName  The new name of file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean rename(final String filePath, final String newName) {
            return rename(getFileByPath(filePath), newName);
        }

        /**
         * Rename the file.
         *
         * @param file    The file.
         * @param newName The new name of file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean rename(final File file, final String newName) {
            // file is null then return false
            if (file == null) return false;
            // file doesn't exist then return false
            if (!file.exists()) return false;
            // the new name is space then return false
            if (isSpace(newName)) return false;
            // the new name equals old name then return true
            if (newName.equals(file.getName())) return true;
            File newFile = new File(file.getParent() + File.separator + newName);
            // the new name of file exists then return false
            return !newFile.exists()
                    && file.renameTo(newFile);
        }

        /**
         * Return whether it is a directory.
         *
         * @param dirPath The path of directory.
         * @return {@code true}: yes<br>{@code false}: no
         */
        public static boolean isDir(final String dirPath) {
            return isDir(getFileByPath(dirPath));
        }

        /**
         * Return whether it is a directory.
         *
         * @param file The file.
         * @return {@code true}: yes<br>{@code false}: no
         */
        public static boolean isDir(final File file) {
            return file != null && file.exists() && file.isDirectory();
        }

        /**
         * Return whether it is a file.
         *
         * @param filePath The path of file.
         * @return {@code true}: yes<br>{@code false}: no
         */
        public static boolean isFile(final String filePath) {
            return isFile(getFileByPath(filePath));
        }

        /**
         * Return whether it is a file.
         *
         * @param file The file.
         * @return {@code true}: yes<br>{@code false}: no
         */
        public static boolean isFile(final File file) {
            return file != null && file.exists() && file.isFile();
        }

        /**
         * Create a directory if it doesn't exist, otherwise do nothing.
         *
         * @param dirPath The path of directory.
         * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
         */
        public static boolean createOrExistsDir(final String dirPath) {
            return createOrExistsDir(getFileByPath(dirPath));
        }

        /**
         * Create a directory if it doesn't exist, otherwise do nothing.
         *
         * @param file The file.
         * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
         */
        public static boolean createOrExistsDir(final File file) {
            return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
        }

        /**
         * Create a file if it doesn't exist, otherwise do nothing.
         *
         * @param filePath The path of file.
         * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
         */
        public static boolean createOrExistsFile(final String filePath) {
            return createOrExistsFile(getFileByPath(filePath));
        }

        /**
         * Create a file if it doesn't exist, otherwise do nothing.
         *
         * @param file The file.
         * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
         */
        public static boolean createOrExistsFile(final File file) {
            if (file == null) return false;
            if (file.exists()) return file.isFile();
            if (!createOrExistsDir(file.getParentFile())) return false;
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Create a file if it doesn't exist, otherwise delete old file before creating.
         *
         * @param filePath The path of file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean createFileByDeleteOldFile(final String filePath) {
            return createFileByDeleteOldFile(getFileByPath(filePath));
        }

        /**
         * Create a file if it doesn't exist, otherwise delete old file before creating.
         *
         * @param file The file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean createFileByDeleteOldFile(final File file) {
            if (file == null) return false;
            // file exists and unsuccessfully delete then return false
            if (file.exists() && !file.delete()) return false;
            if (!createOrExistsDir(file.getParentFile())) return false;
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Copy the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyDir(final String srcDirPath,
                                      final String destDirPath) {
            return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
        }

        /**
         * Copy the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @param listener    The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyDir(final String srcDirPath,
                                      final String destDirPath,
                                      final OnReplaceListener listener) {
            return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
        }

        /**
         * Copy the directory.
         *
         * @param srcDir  The source directory.
         * @param destDir The destination directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyDir(final File srcDir,
                                      final File destDir) {
            return copyOrMoveDir(srcDir, destDir, false);
        }

        /**
         * Copy the directory.
         *
         * @param srcDir   The source directory.
         * @param destDir  The destination directory.
         * @param listener The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyDir(final File srcDir,
                                      final File destDir,
                                      final OnReplaceListener listener) {
            return copyOrMoveDir(srcDir, destDir, listener, false);
        }

        /**
         * Copy the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyFile(final String srcFilePath,
                                       final String destFilePath) {
            return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
        }

        /**
         * Copy the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @param listener     The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyFile(final String srcFilePath,
                                       final String destFilePath,
                                       final OnReplaceListener listener) {
            return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
        }

        /**
         * Copy the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyFile(final File srcFile,
                                       final File destFile) {
            return copyOrMoveFile(srcFile, destFile, false);
        }

        /**
         * Copy the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @param listener The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean copyFile(final File srcFile,
                                       final File destFile,
                                       final OnReplaceListener listener) {
            return copyOrMoveFile(srcFile, destFile, listener, false);
        }

        /**
         * Move the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveDir(final String srcDirPath,
                                      final String destDirPath) {
            return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
        }

        /**
         * Move the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @param listener    The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveDir(final String srcDirPath,
                                      final String destDirPath,
                                      final OnReplaceListener listener) {
            return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
        }

        /**
         * Move the directory.
         *
         * @param srcDir  The source directory.
         * @param destDir The destination directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveDir(final File srcDir,
                                      final File destDir) {
            return copyOrMoveDir(srcDir, destDir, true);
        }

        /**
         * Move the directory.
         *
         * @param srcDir   The source directory.
         * @param destDir  The destination directory.
         * @param listener The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveDir(final File srcDir,
                                      final File destDir,
                                      final OnReplaceListener listener) {
            return copyOrMoveDir(srcDir, destDir, listener, true);
        }

        /**
         * Move the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveFile(final String srcFilePath,
                                       final String destFilePath) {
            return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath));
        }

        /**
         * Move the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @param listener     The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveFile(final String srcFilePath,
                                       final String destFilePath,
                                       final OnReplaceListener listener) {
            return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
        }

        /**
         * Move the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveFile(final File srcFile,
                                       final File destFile) {
            return copyOrMoveFile(srcFile, destFile, true);
        }

        /**
         * Move the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @param listener The replace listener.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean moveFile(final File srcFile,
                                       final File destFile,
                                       final OnReplaceListener listener) {
            return copyOrMoveFile(srcFile, destFile, listener, true);
        }

        private static boolean copyOrMoveDir(final File srcDir,
                                             final File destDir,
                                             final boolean isMove) {
            return copyOrMoveDir(srcDir, destDir, new OnReplaceListener() {
                @Override
                public boolean onReplace() {
                    return true;
                }
            }, isMove);
        }

        private static boolean copyOrMoveDir(final File srcDir,
                                             final File destDir,
                                             final OnReplaceListener listener,
                                             final boolean isMove) {
            if (srcDir == null || destDir == null) return false;
            // destDir's path locate in srcDir's path then return false
            String srcPath = srcDir.getPath() + File.separator;
            String destPath = destDir.getPath() + File.separator;
            if (destPath.contains(srcPath)) return false;
            if (!srcDir.exists() || !srcDir.isDirectory()) return false;
            if (destDir.exists()) {
                if (listener == null || listener.onReplace()) {// require delete the old directory
                    if (!deleteAllInDir(destDir)) {// unsuccessfully delete then return false
                        return false;
                    }
                } else {
                    return true;
                }
            }
            if (!createOrExistsDir(destDir)) return false;
            File[] files = srcDir.listFiles();
            for (File file : files) {
                File oneDestFile = new File(destPath + file.getName());
                if (file.isFile()) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false;
                } else if (file.isDirectory()) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false;
                }
            }
            return !isMove || deleteDir(srcDir);
        }

        private static boolean copyOrMoveFile(final File srcFile,
                                              final File destFile,
                                              final boolean isMove) {
            return copyOrMoveFile(srcFile, destFile, new OnReplaceListener() {
                @Override
                public boolean onReplace() {
                    return true;
                }
            }, isMove);
        }

        private static boolean copyOrMoveFile(final File srcFile,
                                              final File destFile,
                                              final OnReplaceListener listener,
                                              final boolean isMove) {
            if (srcFile == null || destFile == null) return false;
            // srcFile equals destFile then return false
            if (srcFile.equals(destFile)) return false;
            // srcFile doesn't exist or isn't a file then return false
            if (!srcFile.exists() || !srcFile.isFile()) return false;
            if (destFile.exists()) {
                if (listener == null || listener.onReplace()) {// require delete the old file
                    if (!destFile.delete()) {// unsuccessfully delete then return false
                        return false;
                    }
                } else {
                    return true;
                }
            }
            if (!createOrExistsDir(destFile.getParentFile())) return false;
            try {
                return writeFileFromIS(destFile, new FileInputStream(srcFile))
                        && !(isMove && !deleteFile(srcFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Delete the directory.
         *
         * @param filePath The path of file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean delete(final String filePath) {
            return delete(getFileByPath(filePath));
        }

        /**
         * Delete the directory.
         *
         * @param file The file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean delete(final File file) {
            if (file == null) return false;
            if (file.isDirectory()) {
                return deleteDir(file);
            }
            return deleteFile(file);
        }

        /**
         * Delete the directory.
         *
         * @param dirPath The path of directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteDir(final String dirPath) {
            return deleteDir(getFileByPath(dirPath));
        }

        /**
         * Delete the directory.
         *
         * @param dir The directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteDir(final File dir) {
            if (dir == null) return false;
            // dir doesn't exist then return true
            if (!dir.exists()) return true;
            // dir isn't a directory then return false
            if (!dir.isDirectory()) return false;
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (!file.delete()) return false;
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) return false;
                    }
                }
            }
            return dir.delete();
        }

        /**
         * Delete the file.
         *
         * @param srcFilePath The path of source file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteFile(final String srcFilePath) {
            return deleteFile(getFileByPath(srcFilePath));
        }

        /**
         * Delete the file.
         *
         * @param file The file.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteFile(final File file) {
            return file != null && (!file.exists() || file.isFile() && file.delete());
        }

        /**
         * Delete the all in directory.
         *
         * @param dirPath The path of directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteAllInDir(final String dirPath) {
            return deleteAllInDir(getFileByPath(dirPath));
        }

        /**
         * Delete the all in directory.
         *
         * @param dir The directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteAllInDir(final File dir) {
            return deleteFilesInDirWithFilter(dir, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            });
        }

        /**
         * Delete all files in directory.
         *
         * @param dirPath The path of directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteFilesInDir(final String dirPath) {
            return deleteFilesInDir(getFileByPath(dirPath));
        }

        /**
         * Delete all files in directory.
         *
         * @param dir The directory.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteFilesInDir(final File dir) {
            return deleteFilesInDirWithFilter(dir, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });
        }

        /**
         * Delete all files that satisfy the filter in directory.
         *
         * @param dirPath The path of directory.
         * @param filter  The filter.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteFilesInDirWithFilter(final String dirPath,
                                                         final FileFilter filter) {
            return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
        }

        /**
         * Delete all files that satisfy the filter in directory.
         *
         * @param dir    The directory.
         * @param filter The filter.
         * @return {@code true}: success<br>{@code false}: fail
         */
        public static boolean deleteFilesInDirWithFilter(final File dir, final FileFilter filter) {
            if (dir == null) return false;
            // dir doesn't exist then return true
            if (!dir.exists()) return true;
            // dir isn't a directory then return false
            if (!dir.isDirectory()) return false;
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (filter.accept(file)) {
                        if (file.isFile()) {
                            if (!file.delete()) return false;
                        } else if (file.isDirectory()) {
                            if (!deleteDir(file)) return false;
                        }
                    }
                }
            }
            return true;
        }

        /**
         * Return the files in directory.
         * <p>Doesn't traverse subdirectories</p>
         *
         * @param dirPath The path of directory.
         * @return the files in directory
         */
        public static List<File> listFilesInDir(final String dirPath) {
            return listFilesInDir(dirPath, false);
        }

        /**
         * Return the files in directory.
         * <p>Doesn't traverse subdirectories</p>
         *
         * @param dir The directory.
         * @return the files in directory
         */
        public static List<File> listFilesInDir(final File dir) {
            return listFilesInDir(dir, false);
        }

        /**
         * Return the files in directory.
         *
         * @param dirPath     The path of directory.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files in directory
         */
        public static List<File> listFilesInDir(final String dirPath, final boolean isRecursive) {
            return listFilesInDir(getFileByPath(dirPath), isRecursive);
        }

        /**
         * Return the files in directory.
         *
         * @param dir         The directory.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files in directory
         */
        public static List<File> listFilesInDir(final File dir, final boolean isRecursive) {
            return listFilesInDirWithFilter(dir, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            }, isRecursive);
        }

        /**
         * Return the files that satisfy the filter in directory.
         * <p>Doesn't traverse subdirectories</p>
         *
         * @param dirPath The path of directory.
         * @param filter  The filter.
         * @return the files that satisfy the filter in directory
         */
        public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                          final FileFilter filter) {
            return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false);
        }

        /**
         * Return the files that satisfy the filter in directory.
         * <p>Doesn't traverse subdirectories</p>
         *
         * @param dir    The directory.
         * @param filter The filter.
         * @return the files that satisfy the filter in directory
         */
        public static List<File> listFilesInDirWithFilter(final File dir,
                                                          final FileFilter filter) {
            return listFilesInDirWithFilter(dir, filter, false);
        }

        /**
         * Return the files that satisfy the filter in directory.
         *
         * @param dirPath     The path of directory.
         * @param filter      The filter.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files that satisfy the filter in directory
         */
        public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                          final FileFilter filter,
                                                          final boolean isRecursive) {
            return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive);
        }

        /**
         * Return the files that satisfy the filter in directory.
         *
         * @param dir         The directory.
         * @param filter      The filter.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files that satisfy the filter in directory
         */
        public static List<File> listFilesInDirWithFilter(final File dir,
                                                          final FileFilter filter,
                                                          final boolean isRecursive) {
            if (!isDir(dir)) return null;
            List<File> list = new ArrayList<>();
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (filter.accept(file)) {
                        list.add(file);
                    }
                    if (isRecursive && file.isDirectory()) {
                        //noinspection ConstantConditions
                        list.addAll(listFilesInDirWithFilter(file, filter, true));
                    }
                }
            }
            return list;
        }

        /**
         * Return the time that the file was last modified.
         *
         * @param filePath The path of file.
         * @return the time that the file was last modified
         */

        public static long getFileLastModified(final String filePath) {
            return getFileLastModified(getFileByPath(filePath));
        }

        /**
         * Return the time that the file was last modified.
         *
         * @param file The file.
         * @return the time that the file was last modified
         */
        public static long getFileLastModified(final File file) {
            if (file == null) return -1;
            return file.lastModified();
        }

        /**
         * Return the charset of file simply.
         *
         * @param filePath The path of file.
         * @return the charset of file simply
         */
        public static String getFileCharsetSimple(final String filePath) {
            return getFileCharsetSimple(getFileByPath(filePath));
        }

        /**
         * Return the charset of file simply.
         *
         * @param file The file.
         * @return the charset of file simply
         */
        public static String getFileCharsetSimple(final File file) {
            int p = 0;
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
                p = (is.read() << 8) + is.read();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            switch (p) {
                case 0xefbb:
                    return "UTF-8";
                case 0xfffe:
                    return "Unicode";
                case 0xfeff:
                    return "UTF-16BE";
                default:
                    return "GBK";
            }
        }

        /**
         * Return the number of lines of file.
         *
         * @param filePath The path of file.
         * @return the number of lines of file
         */
        public static int getFileLines(final String filePath) {
            return getFileLines(getFileByPath(filePath));
        }

        /**
         * Return the number of lines of file.
         *
         * @param file The file.
         * @return the number of lines of file
         */
        public static int getFileLines(final File file) {
            int count = 1;
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[1024];
                int readChars;
                if (LINE_SEP.endsWith("\n")) {
                    while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                        for (int i = 0; i < readChars; ++i) {
                            if (buffer[i] == '\n') ++count;
                        }
                    }
                } else {
                    while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                        for (int i = 0; i < readChars; ++i) {
                            if (buffer[i] == '\r') ++count;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return count;
        }

        /**
         * Return the size of directory.
         *
         * @param dirPath The path of directory.
         * @return the size of directory
         */
        public static String getDirSize(final String dirPath) {
            return getDirSize(getFileByPath(dirPath));
        }

        /**
         * Return the size of directory.
         *
         * @param dir The directory.
         * @return the size of directory
         */
        public static String getDirSize(final File dir) {
            long len = getDirLength(dir);
            return len == -1 ? "" : byte2FitMemorySize(len);
        }

        /**
         * Return the length of file.
         *
         * @param filePath The path of file.
         * @return the length of file
         */
        public static String getFileSize(final String filePath) {
            long len = getFileLength(filePath);
            return len == -1 ? "" : byte2FitMemorySize(len);
        }

        /**
         * Return the length of file.
         *
         * @param file The file.
         * @return the length of file
         */
        public static String getFileSize(final File file) {
            long len = getFileLength(file);
            return len == -1 ? "" : byte2FitMemorySize(len);
        }

        /**
         * Return the length of directory.
         *
         * @param dirPath The path of directory.
         * @return the length of directory
         */
        public static long getDirLength(final String dirPath) {
            return getDirLength(getFileByPath(dirPath));
        }

        /**
         * Return the length of directory.
         *
         * @param dir The directory.
         * @return the length of directory
         */
        public static long getDirLength(final File dir) {
            if (!isDir(dir)) return -1;
            long len = 0;
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        len += getDirLength(file);
                    } else {
                        len += file.length();
                    }
                }
            }
            return len;
        }

        /**
         * Return the length of file.
         *
         * @param filePath The path of file.
         * @return the length of file
         */
        public static long getFileLength(final String filePath) {
            boolean isURL = filePath.matches("[a-zA-z]+://[^\\s]*");
            if (isURL) {
                try {
                    HttpsURLConnection conn = (HttpsURLConnection) new URL(filePath).openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        return conn.getContentLength();
                    }
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return getFileLength(getFileByPath(filePath));
        }

        /**
         * Return the length of file.
         *
         * @param file The file.
         * @return the length of file
         */
        public static long getFileLength(final File file) {
            if (!isFile(file)) return -1;
            return file.length();
        }

        /**
         * Return the MD5 of file.
         *
         * @param filePath The path of file.
         * @return the md5 of file
         */
        public static String getFileMD5ToString(final String filePath) {
            File file = isSpace(filePath) ? null : new File(filePath);
            return getFileMD5ToString(file);
        }

        /**
         * Return the MD5 of file.
         *
         * @param file The file.
         * @return the md5 of file
         */
        public static String getFileMD5ToString(final File file) {
            return bytes2HexString(getFileMD5(file));
        }

        /**
         * Return the MD5 of file.
         *
         * @param filePath The path of file.
         * @return the md5 of file
         */
        public static byte[] getFileMD5(final String filePath) {
            return getFileMD5(getFileByPath(filePath));
        }

        /**
         * Return the MD5 of file.
         *
         * @param file The file.
         * @return the md5 of file
         */
        public static byte[] getFileMD5(final File file) {
            if (file == null) return null;
            DigestInputStream dis = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                MessageDigest md = MessageDigest.getInstance("MD5");
                dis = new DigestInputStream(fis, md);
                byte[] buffer = new byte[1024 * 256];
                while (true) {
                    if (!(dis.read(buffer) > 0)) break;
                }
                md = dis.getMessageDigest();
                return md.digest();
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Return the file's path of directory.
         *
         * @param file The file.
         * @return the file's path of directory
         */
        public static String getDirName(final File file) {
            if (file == null) return "";
            return getDirName(file.getAbsolutePath());
        }

        /**
         * Return the file's path of directory.
         *
         * @param filePath The path of file.
         * @return the file's path of directory
         */
        public static String getDirName(final String filePath) {
            if (isSpace(filePath)) return "";
            int lastSep = filePath.lastIndexOf(File.separator);
            return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
        }

        /**
         * Return the name of file.
         *
         * @param file The file.
         * @return the name of file
         */
        public static String getFileName(final File file) {
            if (file == null) return "";
            return getFileName(file.getAbsolutePath());
        }

        /**
         * Return the name of file.
         *
         * @param filePath The path of file.
         * @return the name of file
         */
        public static String getFileName(final String filePath) {
            if (isSpace(filePath)) return "";
            int lastSep = filePath.lastIndexOf(File.separator);
            return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
        }

        /**
         * Return the name of file without extension.
         *
         * @param file The file.
         * @return the name of file without extension
         */
        public static String getFileNameNoExtension(final File file) {
            if (file == null) return "";
            return getFileNameNoExtension(file.getPath());
        }

        /**
         * Return the name of file without extension.
         *
         * @param filePath The path of file.
         * @return the name of file without extension
         */
        public static String getFileNameNoExtension(final String filePath) {
            if (isSpace(filePath)) return "";
            int lastPoi = filePath.lastIndexOf('.');
            int lastSep = filePath.lastIndexOf(File.separator);
            if (lastSep == -1) {
                return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
            }
            if (lastPoi == -1 || lastSep > lastPoi) {
                return filePath.substring(lastSep + 1);
            }
            return filePath.substring(lastSep + 1, lastPoi);
        }

        /**
         * Return the extension of file.
         *
         * @param file The file.
         * @return the extension of file
         */
        public static String getFileExtension(final File file) {
            if (file == null) return "";
            return getFileExtension(file.getPath());
        }

        /**
         * Return the extension of file.
         *
         * @param filePath The path of file.
         * @return the extension of file
         */
        public static String getFileExtension(final String filePath) {
            if (isSpace(filePath)) return "";
            int lastPoi = filePath.lastIndexOf('.');
            int lastSep = filePath.lastIndexOf(File.separator);
            if (lastPoi == -1 || lastSep >= lastPoi) return "";
            return filePath.substring(lastPoi + 1);
        }

        ///////////////////////////////////////////////////////////////////////////
        // interface
        ///////////////////////////////////////////////////////////////////////////

        public interface OnReplaceListener {
            boolean onReplace();
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////

        private static final char HEX_DIGITS[] =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        private static String bytes2HexString(final byte[] bytes) {
            if (bytes == null) return "";
            int len = bytes.length;
            if (len <= 0) return "";
            char[] ret = new char[len << 1];
            for (int i = 0, j = 0; i < len; i++) {
                ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
                ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
            }
            return new String(ret);
        }

        private static String byte2FitMemorySize(final long byteNum) {
            if (byteNum < 0) {
                return "shouldn't be less than zero!";
            } else if (byteNum < 1024) {
                return String.format(Locale.getDefault(), "%.3fB", (double) byteNum);
            } else if (byteNum < 1048576) {
                return String.format(Locale.getDefault(), "%.3fKB", (double) byteNum / 1024);
            } else if (byteNum < 1073741824) {
                return String.format(Locale.getDefault(), "%.3fMB", (double) byteNum / 1048576);
            } else {
                return String.format(Locale.getDefault(), "%.3fGB", (double) byteNum / 1073741824);
            }
        }

        private static boolean isSpace(final String s) {
            if (s == null) return true;
            for (int i = 0, len = s.length(); i < len; ++i) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        private static boolean writeFileFromIS(final File file,
                                               final InputStream is) {
            OutputStream os = null;
            try {
                os = new BufferedOutputStream(new FileOutputStream(file));
                byte data[] = new byte[8192];
                int len;
                while ((len = is.read(data, 0, 8192)) != -1) {
                    os.write(data, 0, len);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 作    者：wangr on 2017/5/24 14:33
     * 描    述： 日期工具类
     * 修订记录：
     */
    public static class DateKit {

        private static SimpleDateFormat m = new SimpleDateFormat("MM", Locale.CHINA);
        private static SimpleDateFormat d = new SimpleDateFormat("dd", Locale.CHINA);
        private static SimpleDateFormat md = new SimpleDateFormat("MM-dd", Locale.CHINA);
        private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        private static SimpleDateFormat ymdDot = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        private static SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        private static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        private static SimpleDateFormat ymdhmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        private static SimpleDateFormat hm = new SimpleDateFormat("HH:mm", Locale.CHINA);
        private static SimpleDateFormat mdhm = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
        private static SimpleDateFormat mdCN = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        private static SimpleDateFormat mdhmLink = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);


        /**
         * 读取两个日期之间的天数
         *
         * @return
         */
        public static int getDaysBetween(java.util.Date beginDate, java.util.Date endDate) {
            if (beginDate == null || endDate == null) {
                return 0;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beginDate);
            int dStart = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTime(endDate);
            int dEnd = calendar.get(Calendar.DAY_OF_YEAR);
            return Math.abs(dEnd - dStart);
        }

        /**
         * 月[05]
         *
         * @param milliseconds
         * @return
         */

        public static String getM(long milliseconds) {
            return m.format(new java.util.Date(milliseconds));
        }

        /**
         * 日[24]
         *
         * @param milliseconds
         * @return
         */
        public static String getD(long milliseconds) {
            return d.format(new java.util.Date(milliseconds));
        }

        /**
         * 时分[4:36]
         *
         * @param milliseconds
         * @return
         */
        public static String getHm(long milliseconds) {
            return hm.format(new java.util.Date(milliseconds));
        }

        /**
         * 月日[05-14]
         *
         * @param milliseconds
         * @return
         */
        public static String getMd(long milliseconds) {
            return md.format(new java.util.Date(milliseconds));
        }

        /**
         * 月日时分[05-14 14:36]
         *
         * @param milliseconds
         * @return
         */
        public static String getMdhmLink(long milliseconds) {
            return mdhmLink.format(new java.util.Date(milliseconds));
        }

        /**
         * 月日汉字[05月14日]
         *
         * @param milliseconds
         * @return
         */
        public static String getMdCN(long milliseconds) {
            return mdCN.format(new java.util.Date(milliseconds));
        }

        /**
         * 月日时分[05月14日 14:36]
         *
         * @param milliseconds
         * @return
         */
        public static String getMdhm(long milliseconds) {
            return mdhm.format(new java.util.Date(milliseconds));
        }

        /**
         * 年月日[2017-05-24]
         *
         * @param milliseconds
         * @return
         */
        public static String getYmd(long milliseconds) {
            return ymd.format(new java.util.Date(milliseconds));
        }

        /**
         * 年月日[2017.05.24]
         *
         * @param milliseconds
         * @return
         */
        public static String getYmdDot(long milliseconds) {
            return ymdDot.format(new java.util.Date(milliseconds));
        }

        /**
         * 年月日时分[2017-05-14 14:36]
         *
         * @param milliseconds
         * @return
         */
        public static String getYmdhm(long milliseconds) {
            return ymdhm.format(new java.util.Date(milliseconds));
        }

        /**
         * 年月日时分秒[2017-05-14 14:36:29]
         *
         * @param milliseconds
         * @return
         */
        public static String getYmdhms(long milliseconds) {
            return ymdhms.format(new java.util.Date(milliseconds));
        }

        /**
         * 年月日时分秒.毫秒数[2017-05-14 14:36:29.666]
         *
         * @param milliseconds
         * @return
         */
        public static String getYmdhmsS(long milliseconds) {
            return ymdhmss.format(new java.util.Date(milliseconds));
        }

        /**
         * 是否是今天
         *
         * @param milliseconds
         * @return
         */
        public static boolean isToday(long milliseconds) {
            String dest = getYmd(milliseconds);
            String now = getYmd(Calendar.getInstance().getTimeInMillis());
            return dest.equals(now);
        }

        /**
         * 是否是同一天
         *
         * @param aMilliseconds
         * @param bMilliseconds
         * @return
         */
        public static boolean isSameDay(long aMilliseconds, long bMilliseconds) {
            String aDay = getYmd(aMilliseconds);
            String bDay = getYmd(bMilliseconds);
            return aDay.equals(bDay);
        }

        /**
         * 获取年份
         *
         * @param milliseconds
         * @return
         */
        public static int getYear(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            return calendar.get(Calendar.YEAR);
        }

        /**
         * 获取月份
         *
         * @param milliseconds
         * @return
         */
        public static int getMonth(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            return calendar.get(Calendar.MONTH) + 1;
        }

        /**
         * 获取日期
         *
         * @param milliseconds
         * @return
         */
        public static int getDay(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            return calendar.get(Calendar.DAY_OF_MONTH);
        }

        /**
         * 获取月份的天数
         *
         * @param milliseconds
         * @return
         */
        public static int getDaysInMonth(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            switch (month) {
                case Calendar.JANUARY:
                case Calendar.MARCH:
                case Calendar.MAY:
                case Calendar.JULY:
                case Calendar.AUGUST:
                case Calendar.OCTOBER:
                case Calendar.DECEMBER:
                    return 31;
                case Calendar.APRIL:
                case Calendar.JUNE:
                case Calendar.SEPTEMBER:
                case Calendar.NOVEMBER:
                    return 30;
                case Calendar.FEBRUARY:
                    return (year % 4 == 0) ? 29 : 28;
                default:
                    throw new IllegalArgumentException("Invalid Month");
            }
        }

        /**
         * 获取星期,0-周日,1-周一，2-周二，3-周三，4-周四，5-周五，6-周六
         *
         * @param milliseconds
         * @return
         */
        public static int getWeekNO(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }

        /**
         * 获取星期,0-周日,1-周一，2-周二，3-周三，4-周四，5-周五，6-周六
         *
         * @param milliseconds
         * @return
         */
        public static String getWeek(long milliseconds) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.CHINA);
            return sdf.format(new java.util.Date(milliseconds));
        }

        /**
         * 获取当天0点0分0秒
         *
         * @return
         */
        public static long getFirstTimeOfDay(long milliseconds) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 1);
            return calendar.getTimeInMillis();
        }

        /**
         * 获取当天23点59分59秒
         *
         * @return
         */
        public static long getLastTimeOfDay(long milliseconds) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTimeInMillis();
        }

        /**
         * 获取当月第一天的时间（毫秒值）
         *
         * @param milliseconds
         * @return
         */
        public static long getFirstOfMonth(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return getFirstTimeOfDay(calendar.getTimeInMillis());
        }

        /**
         * 获取当月最后一天的时间（毫秒值）
         *
         * @return
         */
        public static long getLastOfMonth(long milliseconds) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.roll(Calendar.DAY_OF_MONTH, -1);
            return getLastTimeOfDay(calendar.getTimeInMillis());
        }

        /**
         * 计算两个时间段之间时间
         * 格式默认 ymdhm
         *
         * @param startTime 开始时间
         * @param endTime   结束时间
         * @return
         */
        public static String calTimeBetween(long startTime, long endTime) {
            //milliseconds
            long different = endTime - startTime;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            StringBuilder sb = new StringBuilder();
            if (elapsedDays > 0) {
                sb.append(elapsedDays).append("天");
            }
            if (elapsedHours > 0) {
                sb.append(elapsedHours).append("小时");
            }
            if (elapsedMinutes > 0) {
                sb.append(elapsedMinutes).append("分钟");
            }
            return sb.toString();
        }


        /**
         * 时间戳格式转换
         */
        private static final String WEEKDAY_NAME[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        /**
         * 微信消息时间格式
         *
         * @param time
         * @return
         */
        public static String getWeChatTime(long time) {
            String result = "";
            Calendar todayCalendar = Calendar.getInstance();
            Calendar otherCalendar = Calendar.getInstance();
            otherCalendar.setTimeInMillis(time);

            String timeFormat = "M月d日 HH:mm";
            String yearTimeFormat = "yyyy年M月d日 HH:mm";
            String am_pm = "";
            int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);
            if (hour >= 0 && hour < 6) {
                am_pm = "凌晨";
            } else if (hour >= 6 && hour < 12) {
                am_pm = "早上";
            } else if (hour == 12) {
                am_pm = "中午";
            } else if (hour > 12 && hour < 18) {
                am_pm = "下午";
            } else if (hour >= 18) {
                am_pm = "晚上";
            }
            timeFormat = "M月d日 " + am_pm + "HH:mm";
            yearTimeFormat = "yyyy年M月d日 " + am_pm + "HH:mm";

            boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
            if (yearTemp) {
                int todayMonth = todayCalendar.get(Calendar.MONTH);
                int otherMonth = otherCalendar.get(Calendar.MONTH);
                //表示是同一个月
                if (todayMonth == otherMonth) {
                    int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                    switch (temp) {
                        case 0:
                            result = getHourAndMin(time);
                            break;
                        case 1:
                            result = "昨天 " + getHourAndMin(time);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                            int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                            //表示是同一周
                            if (dayOfMonth == todayOfMonth) {
                                int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
                                //判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                if (dayOfWeek != 1) {
                                    result = WEEKDAY_NAME[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(time);
                                } else {
                                    result = getTime(time, timeFormat);
                                }
                            } else {
                                result = getTime(time, timeFormat);
                            }
                            break;
                        default:
                            result = getTime(time, timeFormat);
                            break;
                    }
                } else {
                    result = getTime(time, timeFormat);
                }
            } else {
                result = getYearTime(time, yearTimeFormat);
            }
            return result;
        }

        /**
         * 当天的显示时间格式
         *
         * @param time
         * @return
         */
        public static String getHourAndMin(long time) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(new Date(time));
        }

        /**
         * 不同一周的显示时间格式
         *
         * @param time
         * @param timeFormat
         * @return
         */
        public static String getTime(long time, String timeFormat) {
            SimpleDateFormat format = new SimpleDateFormat(timeFormat);
            return format.format(new Date(time));
        }

        /**
         * 不同年的显示时间格式
         *
         * @param time
         * @param yearTimeFormat
         * @return
         */
        public static String getYearTime(long time, String yearTimeFormat) {
            SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
            return format.format(new Date(time));
        }

    }

}
