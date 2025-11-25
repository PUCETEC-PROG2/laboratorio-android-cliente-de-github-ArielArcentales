package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupFab()
        fetchRepositories()
        
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                binding.fabAddProject.hide()
                binding.fragmentContainer.visibility = View.VISIBLE
                binding.repoRecyclerView.visibility = View.GONE
            } else {
                binding.fabAddProject.show()
                binding.fragmentContainer.visibility = View.GONE
                binding.repoRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onDelete = { repo ->
                reposAdapter.removeItem(repo)
                Toast.makeText(this, "Repositorio eliminado (Solo visualmente)", Toast.LENGTH_SHORT).show()
            },
            onEdit = { repo ->
                openEditFragment(repo)
            }
        )
        
        binding.repoRecyclerView.apply {
            adapter = reposAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupFab() {
        binding.fabAddProject.setOnClickListener {
            val fragment = CreateProjectFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun openEditFragment(repo: Repo) {
        val fragment = CreateProjectFragment.newInstance(repo.name, repo.description ?: "")
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchRepositories() {
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Error 401: Verifica tu Token de GitHub"
                        403 -> "Error 403: Acceso denegado"
                        404 -> "Error 404: No encontrado"
                        else -> "Error: ${response.code()}"
                    }
                    Log.e("MainActivity", errorMsg)
                    showMessage(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e("MainActivity", "Error de conexión", t)
                showMessage("Error de conexión: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}