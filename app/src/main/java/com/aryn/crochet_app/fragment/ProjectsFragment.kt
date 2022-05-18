package com.aryn.crochet_app.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.aryn.crochet_app.databinding.FragmentProjectsBinding
import com.aryn.crochet_app.db.DBConnection
import com.aryn.crochet_app.db.entity.Part
import com.aryn.crochet_app.db.entity.Project
import kotlin.random.Random


class AddProjectFragment(private val cb: (DialogState) -> Unit) : DialogFragment() {
    data class DialogState(
        var name: String,
        var hookSize: Float
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val state = DialogState("", 2f)

            val text = EditText(this.requireContext()).apply {
                hint = "Name"
            }

            val size = EditText(this.requireContext()).apply {
                hint = "Hook size"
            }

            val view = LinearLayout(this.requireContext())
            view.addView(text)
            view.addView(size)

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Enter project details")
                .setPositiveButton("Done") { d, i ->
                    state.name = text.text.toString()
                    state.hookSize = size.text.toString().toFloat()

                    cb(state)
                }
                .setView(view)

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}


class ProjectsFragment : Fragment() {

    private var _binding: FragmentProjectsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProjectsBinding.inflate(inflater, container, false)

        Log.i("FRAGMENT", "Loaded projects")

        binding.addProjectFAB.setOnClickListener { v ->

            val dia = AddProjectFragment {
                val proj = Project(
                    0,
                    it.name,
                    it.hookSize,
                    false,
                    0
                )

                val projId = DBConnection.getInstance(v.context).databaseDAO.insertNewProject(proj)

                val defaultPart = Part(
                    0,
                    "main",
                    0,
                    projId
                )

                val partId = DBConnection.getInstance(v.context).databaseDAO.insertNewPart(defaultPart)

                DBConnection.getInstance(v.context).databaseDAO.updateCurrentPart(projId, partId)
                renderProjects()
            }

            dia.show(parentFragmentManager, "add-part")
        }


        val root: View = binding.root
        renderProjects()

        return root
    }

    private fun renderProjects() {
        val dao = DBConnection.getInstance(this.requireContext()).databaseDAO
        Log.d("PROJECTS", "Starting (re)load of projects")
        val projects = dao.getAllProjects()
        Log.d("PROJECTS", "Finished load of projects, rendering")

        if (projects.isEmpty()) {
            binding.noProjects.visibility = View.VISIBLE
            binding.projectsView.visibility = View.INVISIBLE
        } else {
            binding.noProjects.visibility = View.INVISIBLE
            binding.projectsView.visibility = View.VISIBLE
            binding.projectsView.removeAllViews()

            projects.forEach { p ->
                val view = LinearLayout(this.requireContext())
                view.setPadding(0, 0, 0, 25)

                val textView = TextView(this.requireContext())
                textView.text = p.name
                textView.textSize = 24f
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                textView.setPadding(0, 0, 20, 0)
                textView.maxLines = 1
                textView.ellipsize = TextUtils.TruncateAt.END

                val openButton = Button(this.requireContext())
                openButton.text = "OPEN"
                openButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
                openButton.textSize = 12f

                openButton.setOnClickListener {
                    dao.clearAllCurrents()
                    dao.updateCurrentProject(p.id, true)
                }

                view.addView(openButton)
                view.addView(textView)

                binding.projectsView.addView(view)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}