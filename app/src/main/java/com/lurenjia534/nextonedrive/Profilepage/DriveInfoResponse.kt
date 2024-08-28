data class DriveInfoResponse(
    val createdDateTime: String,
    val description: String?,
    val id: String,
    val lastModifiedDateTime: String,
    val name: String,
    val webUrl: String?,
    val driveType: String,
    val createdBy: CreatedBy,
    val lastModifiedBy: LastModifiedBy,
    val owner: Owner,
    val quota: Quota
)

data class CreatedBy(
    val user: User
)

data class LastModifiedBy(
    val user: User
)

data class Owner(
    val user: User
)

data class User(
    val displayName: String,
    val email: String? = null,
    val id: String? = null
)

data class Quota(
    val deleted: Long,
    val remaining: Long,
    val state: String,
    val total: Long,
    val used: Long
)
