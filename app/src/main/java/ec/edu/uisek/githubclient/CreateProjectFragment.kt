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

        binding.btnCreateProject.setOnClickListener {
            val projectName = binding.etProjectName.text.toString()
            if (projectName.isNotBlank()) {
                Toast.makeText(context, getString(R.string.msg_project_created), Toast.LENGTH_SHORT).show()

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