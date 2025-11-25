package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ec.edu.uisek.githubclient.databinding.FragmentCreateProjectBinding
import ec.edu.uisek.githubclient.models.CreateRepoRequest
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateProjectFragment : Fragment() {

    private var _binding: FragmentCreateProjectBinding? = null
    private val binding get() = _binding!!

    private var isEditMode = false
    private var initialName: String? = null
    private var initialDesc: String? = null

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_DESC = "arg_desc"

        fun newInstance(name: String, description: String): CreateProjectFragment {
            val fragment = CreateProjectFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            args.putString(ARG_DESC, description)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initialName = it.getString(ARG_NAME)
            initialDesc = it.getString(ARG_DESC)
            isEditMode = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEditMode) {
            binding.etProjectName.setText(initialName)
            binding.etProjectDesc.setText(initialDesc)
            binding.btnCreateProject.text = getString(R.string.btn_save)
            binding.etProjectName.isEnabled = false
            binding.etProjectName.alpha = 0.5f
        }

        binding.btnCreateProject.setOnClickListener {
            val projectName = binding.etProjectName.text.toString()
            val projectDesc = binding.etProjectDesc.text.toString()

            if (projectName.isNotBlank()) {
                if (isEditMode) {
                    // Simulación para editar
                    Toast.makeText(context, "Cambios guardados (Simulación)", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    // Llamada real a la API para crear
                    createRepository(projectName, projectDesc)
                }
            } else {
                binding.etProjectName.error = getString(R.string.hint_project_name)
            }
        }
    }

    private fun createRepository(name: String, description: String) {
        binding.btnCreateProject.isEnabled = false // Evitar doble clic
        val request = CreateRepoRequest(name, description)
        val call = RetrofitClient.gitHubApiService.createRepo(request)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                binding.btnCreateProject.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(context, "Proyecto creado en GitHub exitosamente", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Error 401: Verifica tu Token"
                        422 -> "Error 422: El nombre ya existe o es inválido"
                        else -> "Error: ${response.code()}"
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    Log.e("CreateProject", "Error creando repo: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                binding.btnCreateProject.isEnabled = true
                Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("CreateProject", "Fallo en red", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
