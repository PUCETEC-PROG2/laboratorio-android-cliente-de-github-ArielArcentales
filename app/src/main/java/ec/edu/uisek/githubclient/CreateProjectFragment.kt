package ec.edu.uisek.githubclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ec.edu.uisek.githubclient.databinding.FragmentCreateProjectBinding

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

            if (projectName.isNotBlank()) {
                val msg = if (isEditMode) "Cambios guardados (Simulación)" else "Proyecto creado (Simulación)"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                binding.etProjectName.error = getString(R.string.hint_project_name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}