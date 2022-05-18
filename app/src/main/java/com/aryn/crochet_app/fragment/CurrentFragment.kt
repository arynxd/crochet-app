package com.aryn.crochet_app.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.aryn.crochet_app.R
import com.aryn.crochet_app.databinding.FragmentCurrentBinding
import com.aryn.crochet_app.db.DBConnection
import com.aryn.crochet_app.db.entity.Part
import com.aryn.crochet_app.db.entity.Project


class AddPartDialog(private val cb: (DialogState) -> Unit) : DialogFragment() {
    data class DialogState(
        var name: String
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val state = DialogState("")

            val text = EditText(this.requireContext()).apply {
                hint = "Name"
            }
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Enter part details")
                .setPositiveButton("Done") { d, i ->
                    state.name = text.text.toString()
                    cb(state)
                }
                .setView(text)

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class CurrentFragment : Fragment() {

    private var _binding: FragmentCurrentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCurrentBinding.inflate(inflater, container, false)

        val dao = DBConnection.getInstance(this.requireContext()).databaseDAO
        Log.i("FRAGMENT", "Loaded current")

        Log.d("CURRENT", "Trying to get the current project")
        val current = dao.getCurrentProject()

        if (current == null) {
            Log.d("CURRENT", "No current project found")
            noProjectError()
        } else {
            Log.d("CURRENT", "Got the current project $current")
            renderProject(current)
        }


        val root: View = binding.root
        return root
    }

    private fun noProjectError() {
        Log.d("CURRENT", "Rendering error")
        binding.projectTitle.visibility = View.INVISIBLE
        binding.partsView.visibility = View.INVISIBLE
        binding.decrementButton.visibility = View.INVISIBLE
        binding.rowCounter.visibility = View.INVISIBLE
        binding.incrementButton.visibility = View.INVISIBLE
        binding.partInfo.visibility = View.INVISIBLE
        binding.addPartFAB.visibility = View.INVISIBLE
        binding.deleteProject.visibility = View.INVISIBLE

        binding.noProjects.visibility = View.VISIBLE
        Log.d("CURRENT", "Rendered error")
    }

    private fun renderRowCount(part: Part) {
        binding.rowCounter.text = part.rowCount.toString()
    }

    private fun renderCurrentPart(part: Part, project: Project) {
        val dao = DBConnection.getInstance(this.requireContext()).databaseDAO

        binding.partName.text = part.name
        renderRowCount(part)

        binding.incrementButton.setOnClickListener {
            val newItem = part.copy(rowCount = part.rowCount + 1)
            dao.updatePartRowCountByID(newItem.id, newItem.rowCount)
            renderRowCount(newItem)
            renderProject(project)
        }

        binding.decrementButton.setOnClickListener {
            val newItem = part.copy(rowCount = part.rowCount - 1)
            dao.updatePartRowCountByID(newItem.id, newItem.rowCount)
            renderRowCount(newItem)
            renderProject(project)
        }
    }

    private fun renderProject(project: Project) {
        binding.projectTitle.visibility = View.VISIBLE
        binding.partsView.visibility = View.VISIBLE
        binding.decrementButton.visibility = View.VISIBLE
        binding.rowCounter.visibility = View.VISIBLE
        binding.incrementButton.visibility = View.VISIBLE
        binding.partInfo.visibility = View.VISIBLE
        binding.addPartFAB.visibility = View.VISIBLE
        binding.deleteProject.visibility = View.VISIBLE

        binding.noProjects.visibility = View.INVISIBLE

        Log.d("CURRENT", "Set the visibilities")

        val dao = DBConnection.getInstance(this.requireContext()).databaseDAO

        binding.deleteProject.setOnClickListener {
            dao.clearAllCurrents()
            dao.deleteProjectById(project.id)
            noProjectError()
        }

        binding.addPartFAB.setOnClickListener {
            val dia = AddPartDialog {

                val part = Part(
                    0,
                    it.name,
                    0,
                    project.id
                )

                val partId = dao.insertNewPart(part)

                dao.updateCurrentPart(project.id, partId)

                renderProject(project)
            }

            dia.show(parentFragmentManager, "add-part")
        }

        var parts = dao.getAllPartsByProjectID(project.id)
        Log.d("CURRENT", "Project has ${parts.size} parts")

        binding.projectTitle.text = project.name

        val curPart = parts.first { it.id == project.currentPart }
        renderCurrentPart(curPart, project)

        binding.partsView.removeAllViews()
        var idx = 1

        val sorted = parts.sortedBy { it.id }.chunked(3)
        val buttons = mutableListOf<Button>()

        for (partRow in sorted) {
            val row = TableRow(this.requireContext())

            for (item in partRow) {
                val view = Button(this.requireContext())

                view.text = idx.toString()

                view.setBackgroundColor(resources.getColor(R.color.md_theme_light_background))
                view.setTextColor(resources.getColor(R.color.md_theme_light_onSurface))

                if (item.id == curPart.id) {
                    view.setBackgroundColor(resources.getColor(R.color.md_theme_light_tertiary))
                    view.setTextColor(resources.getColor(R.color.md_theme_light_onTertiary))
                }

                view.setOnClickListener {
                    dao.updateCurrentPart(project.id, item.id)

                    parts = dao.getAllPartsByProjectID(project.id)
                    renderCurrentPart(item, project)

                    buttons.forEach {
                        it.setBackgroundColor(resources.getColor(R.color.md_theme_light_background))
                        it.setTextColor(resources.getColor(R.color.md_theme_light_onSurface))
                    }


                    it.setBackgroundColor(resources.getColor(R.color.md_theme_light_tertiary))
                    (it as Button).setTextColor(resources.getColor(R.color.md_theme_light_onTertiary))
                }

                row.addView(view)
                buttons.add(view)
                idx++
            }
            binding.partsView.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}