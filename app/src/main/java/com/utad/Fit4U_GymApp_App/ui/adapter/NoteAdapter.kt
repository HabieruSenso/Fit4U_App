package com.utad.Fit4U_GymApp_App.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.utad.Fit4U_GymApp_App.R
import com.utad.Fit4U_GymApp_App.data.local.models.LocalNote
import com.utad.Fit4U_GymApp_App.databinding.ItemNoteBinding

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val binding:ItemNoteBinding):RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<LocalNote>(){
        override fun areItemsTheSame(oldItem: LocalNote, newItem: LocalNote): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: LocalNote, newItem: LocalNote): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffUtil)
    var notas:List<LocalNote>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val nota = notas[position]
        holder.binding.apply {
            noteText.isVisible = nota.noteTitle != null
            noteDescription.isVisible = nota.desription != null

            nota.noteTitle?.let {
                noteText.text = it
            }
            nota.desription?.let {
                noteDescription.text = it
            }

            noteSync.setBackgroundResource(
                if (nota.connected) R.drawable.ic_baseline_cloud_done_24
                else R.drawable.ic_baseline_cloud_queue_24
            )

            root.setOnClickListener {
                onItemClickListener?.invoke(nota)
            }

        }
    }

    private var onItemClickListener: ((LocalNote)-> Unit)? = null
    fun setOnItemClickListener(listener: (LocalNote)->Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return notas.size

    }
}