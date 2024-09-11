package com.lurenjia534.nextonedrive.Filefunction

import com.google.gson.annotations.SerializedName
import com.lurenjia534.nextonedrive.ListItem.ParentReference

data class CreateFolderRequest(
    val name: String,
    val folder: Map<String,String> = emptyMap(),
    @SerializedName("@microsoft.graph.conflictBehavior") val conflictBehavior: String = "rename",
    val parentReference: ParentReference? = null  // 新增父文件夹引用
)
