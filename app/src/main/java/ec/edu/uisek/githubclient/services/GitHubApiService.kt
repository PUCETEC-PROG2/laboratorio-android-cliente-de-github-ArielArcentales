package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.CreateRepoRequest
import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GitHubApiService {

    @GET("/user/repos")
    fun getRepos(): Call<List<Repo>>

    @POST("/user/repos")
    fun createRepo(@Body repo: CreateRepoRequest): Call<Repo>

    @DELETE("/repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<Void>

    @PATCH("/repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: CreateRepoRequest
    ): Call<Repo>

}