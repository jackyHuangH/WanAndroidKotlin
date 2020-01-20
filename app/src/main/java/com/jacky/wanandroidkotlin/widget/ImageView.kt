package com.jacky.wanandroidkotlin.widget

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.Serializable

@SuppressLint("ParcelCreator")
@Parcelize
open class ImageSource(
    @Type.Scope
    protected open val sourceType: Int = Type.INVALID,
    protected open val uriSource: Uri? = null,
    protected open val resSource: Int? = null,
    protected open val pathSource: String? = null,
    protected open val fileSource: File? = null,
    protected open val urlSource: String? = null
) : Serializable, Parcelable {

    fun get(): Any? = when (sourceType) {
        Type.INVALID -> null
        Type.RES_ID -> resSource
        Type.PATH -> pathSource
        Type.URL -> urlSource
        Type.FILE -> fileSource
        Type.URI -> uriSource
        else -> null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> match(): T? = when (sourceType) {
        Type.INVALID -> null
        Type.RES_ID -> resSource
        Type.PATH -> pathSource
        Type.URL -> urlSource
        Type.FILE -> fileSource
        Type.URI -> uriSource
        else -> null
    } as? T

    fun type() = sourceType

    protected constructor(@NotNull source: String, isOnline: Boolean = false) : this(
        sourceType = if (isOnline) Type.URL else Type.PATH,
        urlSource = if (isOnline) source else null,
        pathSource = if (!isOnline) source else null
    )

    override fun toString(): String {
        val sb = StringBuilder("ImageSourceInfo{")
        sb.append("sourceType=").append(sourceType)
        sb.append(", resSource=").append(resSource)
        sb.append(", pathSource='").append(pathSource).append('\'')
        sb.append(", urlSource='").append(urlSource).append('\'')
        sb.append(", fileSource=").append(fileSource)
        sb.append(", uriSource=").append(uriSource)
        sb.append('}')
        return sb.toString()
    }

    interface Type {

        @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
        @IntDef(INVALID, PATH, URL, FILE, URI, RES_ID)
        annotation class Scope

        @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
        @IntDef(INVALID, PATH, FILE, URI, RES_ID)
        annotation class LocalScope

        @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
        @IntDef(INVALID, URL)
        annotation class RemoteScope

        companion object {

            const val INVALID = 0//无效资源
            const val RES_ID = 1// Res资源
            const val PATH = 2// 文件
            const val URL = 3// 网络
            const val FILE = 4// 文件
            const val URI = 5// 统一资源标识
        }
    }
}

@SuppressLint("ParcelCreator")
@Parcelize
data class LocalImageSource(
    @Type.LocalScope
    override val sourceType: Int = Type.INVALID,
    override val uriSource: Uri? = null,
    override val resSource: Int? = null,
    override val pathSource: String? = null,
    override val fileSource: File? = null

) : ImageSource(
    sourceType = sourceType,
    resSource = resSource,
    pathSource = pathSource,
    fileSource = fileSource,
    uriSource = uriSource
), Serializable, Parcelable {

    constructor(@DrawableRes source: Int?) : this(Type.RES_ID, resSource = source)

    constructor(@NotNull source: Uri?) : this(Type.URI, uriSource = source)

    constructor(@NotNull source: String?) : this(Type.PATH, pathSource = source)

    constructor(@NotNull source: File?) : this(Type.FILE, fileSource = source)
}

@SuppressLint("ParcelCreator")
@Parcelize
class RemoteImageSource(
    @Type.RemoteScope
    override val sourceType: Int = Type.INVALID,
    override val urlSource: String? = null,
    val urlId: String? = null
) : ImageSource(sourceType = sourceType, urlSource = urlSource), Serializable, Parcelable {
    constructor(@NotNull source: String?, @Nullable urlId: String? = null) : this(
        Type.URL,
        source,
        urlId
    )
}


private fun Any?.toImageSource(): ImageSource {
    return when {
        this == null -> ImageSource()
        this is ImageSource -> this
        this is String -> {
            if (File(this).exists()) LocalImageSource(this)
            else RemoteImageSource(this)
        }
        this is File -> LocalImageSource(this)
        this is Int -> LocalImageSource(this)
        this is Uri -> LocalImageSource(this)
        else -> ImageSource()
    }
}
