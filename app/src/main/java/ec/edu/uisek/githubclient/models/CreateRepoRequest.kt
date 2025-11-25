package ec.edu.uisek.githubclient.models

data class CreateRepoRequest(
    val name: String,
    val description: String?
)
