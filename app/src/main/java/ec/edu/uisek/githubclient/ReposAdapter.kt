package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class RepoViewHolder(
    private val binding: FragmentRepoItemBinding,
    private val onDelete: (Repo) -> Unit,
    private val onEdit: (Repo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description ?: "El repositorio no tiene descripción"
        binding.repoLanguage.text = repo.language ?: "Lenguaje no especificado"

        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)

        binding.btnDelete.setOnClickListener {
            onDelete(repo)
        }

        binding.btnEdit.setOnClickListener {
            onEdit(repo)
        }
    }
}

class ReposAdapter(
    private val onDelete: (Repo) -> Unit,
    private val onEdit: (Repo) -> Unit
) : RecyclerView.Adapter<RepoViewHolder>() {

    private var repositories: MutableList<Repo> = mutableListOf()

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding, onDelete, onEdit)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepos: List<Repo>) {
        repositories = newRepos.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(repo: Repo) {
        val index = repositories.indexOf(repo)
        if (index != -1) {
            repositories.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}