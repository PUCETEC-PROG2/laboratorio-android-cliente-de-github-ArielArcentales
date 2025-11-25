package ec.edu.uisek.githubclient

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
            val isMainScreen = supportFragmentManager.backStackEntryCount == 0
            updateVisibility(isMainScreen)
            if (isMainScreen) fetchRepositories()
        }
    }

    private fun updateVisibility(isMainScreen: Boolean) {
        binding.fabAddProject.visibility = if (isMainScreen) View.VISIBLE else View.GONE
        binding.repoRecyclerView.visibility = if (isMainScreen) View.VISIBLE else View.GONE
        binding.fragmentContainer.visibility = if (isMainScreen) View.GONE else View.VISIBLE
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onDelete = { confirmDeleteRepo(it) },
            onEdit = { openEditFragment(it) }
        )
        binding.repoRecyclerView.adapter = reposAdapter
        binding.repoRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFab() {
        binding.fabAddProject.setOnClickListener {
            openFragment(CreateProjectFragment())
        }
    }

    private fun openEditFragment(repo: Repo) {
        openFragment(CreateProjectFragment.newInstance(repo.name, repo.description ?: "", repo.owner.login))
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchRepositories() {
        RetrofitClient.gitHubApiService.getRepos().enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    response.body()?.let { reposAdapter.updateRepositories(it) }
                } else {
                    showError(response.code())
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("Error de conexión: ${t.message}")
            }
        })
    }

    private fun confirmDeleteRepo(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Repositorio")
            .setMessage("¿Estás seguro de eliminar ${repo.name}?")
            .setPositiveButton("Eliminar") { _, _ -> deleteRepository(repo) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteRepository(repo: Repo) {
        RetrofitClient.gitHubApiService.deleteRepo(repo.owner.login, repo.name).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado")
                    reposAdapter.removeItem(repo)
                } else {
                    showError(response.code())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Error al eliminar")
            }
        })
    }

    private fun showError(code: Int) {
        val msg = when (code) {
            401 -> "Error 401: Token inválido"
            403 -> "Error 403: Sin permisos"
            404 -> "Error 404: No encontrado"
            else -> "Error: $code"
        }
        showMessage(msg)
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
