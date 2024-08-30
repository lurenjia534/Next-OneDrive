package com.lurenjia534.nextonedrive.ListItem

import com.google.gson.annotations.SerializedName

data class DriveItemResponse(
    val value: List<DriveItem>
)

data class DriveItem(
    val createdBy: UserReference,
    val createdDateTime: String,
    val eTag: String,
    val id: String,
    val lastModifiedBy: UserReference,
    val lastModifiedDateTime: String,
    val name: String,
    val parentReference: ParentReference,
    val webUrl: String,
    val cTag: String,
    val fileSystemInfo: FileSystemInfo,
    val folder: Folder? = null,
    @SerializedName("@microsoft.graph.downloadUrl") val downloadUrl: String? = null, // for download
    val file: File? = null,
    val image: Image? = null,
    val photo: Photo? = null,
    val size: Long
)

data class UserReference(
    val user: User
)

data class User(
    val email: String,
    val id: String,
    val displayName: String
)

data class ParentReference(
    val driveType: String,
    val driveId: String,
    val id: String,
    val name: String,
    val path: String,
    val siteId: String
)

data class FileSystemInfo(
    val createdDateTime: String,
    val lastModifiedDateTime: String
)

data class Folder(
    val childCount: Int
)

data class File(
    val hashes: Hashes,
    val mimeType: String
)

data class Hashes(
    val quickXorHash: String
)

data class Image(
    val height: Int,
    val width: Int
)

data class Photo(
    val alternateTakenDateTime: String
)
