package com.lurenjia534.nextonedrive.Filefunction

import com.google.gson.annotations.SerializedName
import com.lurenjia534.nextonedrive.ListItem.ParentReference
import okhttp3.RequestBody
// CreateFolderRequest.kt
data class CreateFolderRequest(
    val name: String,
    val folder: Map<String,String> = emptyMap(),
    @SerializedName("@microsoft.graph.conflictBehavior") val conflictBehavior: String = "rename",
    val parentReference: ParentReference? = null  // 新增父文件夹引用
)


data class UploadFileRequest(
    val parentId: String,
    val filename: String,
    val fileContent: RequestBody
)

data class CreateLinkRequest(
    val type: String,  // "view", "edit", or "embed"
    val scope: String? = null  // "anonymous" or "organization"
)

data class PermissionResponse(
    val id: String,
    val roles: List<String>,
    val link: Link
)

data class Link(
    val type: String,
    val scope: String?,
    val webUrl: String,
    val webHtml: String? = null // 仅在 embed 类型下可用
)