package com.lurenjia534.nextonedrive.Filefunction

import com.google.gson.annotations.SerializedName

data class CreateFolderRequest(
    val name: String,
    val folder: Map<String,String> = emptyMap(),
    @SerializedName("@microsoft.graph.conflictBehavior") val conflictBehavior: String = "rename"
)
