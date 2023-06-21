package com.utad.Fit4U_GymApp_App.ui.notes

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.utad.Fit4U_GymApp_App.R
import com.utad.Fit4U_GymApp_App.databinding.FragmentAllNotesBinding
import com.utad.Fit4U_GymApp_App.ui.adapter.NoteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AllNotesFragment:Fragment(R.layout.fragment_all_notes) {

    private var _binding: FragmentAllNotesBinding? = null
    val binding: FragmentAllNotesBinding?
        get() = _binding

    private lateinit var noteAdapter:NoteAdapter
    private val noteViewModel:NoteViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllNotesBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding!!.customToolBar)

        binding?.newNoteFab?.setOnClickListener {
            findNavController().navigate(R.id.action_allNotesFragment_to_newNoteFragment)
        }
        montarRecyclerView()
        subscribeANotas()
        montarSwipeLayout()
        noteViewModel.syncNotas()

    }

    private fun montarRecyclerView(){

        noteAdapter = NoteAdapter()
        noteAdapter.setOnItemClickListener {
            Log.d("click","Dame duro")
            val accion = AllNotesFragmentDirections.actionAllNotesFragmentToNewNoteFragment(it)
            findNavController().navigate(accion)
        }

        binding?.noteRecyclerView?.apply {
            adapter = noteAdapter
            layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)


            ItemTouchHelper(itemTouchHelperCallback)
                .attachToRecyclerView(this)

        }
    }

    private fun subscribeANotas() = lifecycleScope.launch{
        noteViewModel.notas.collect {
            noteAdapter.notas = it.filter { localNote ->
                localNote.noteTitle?.contains(noteViewModel.buscarQuery,true) == true ||
                        localNote.desription?.contains(noteViewModel.buscarQuery,true) == true
            }
        }
    }

    private fun montarSwipeLayout(){
        binding?.swipeRefeeshLayout?.setOnRefreshListener {
            noteViewModel.syncNotas {
                binding?.swipeRefeeshLayout?.isRefreshing = false
            }
        }
    }

    val itemTouchHelperCallback = object :ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val posicion = viewHolder.layoutPosition
            val nota = noteAdapter.notas[posicion]
            noteViewModel.borrarNota(nota.noteId)
            Snackbar.make(
                requireView(),
                "Nota borrada satisfactoriamente",
                Snackbar.LENGTH_LONG
            ).apply {
                setAction(
                    "Undo"
                ) {
                    noteViewModel.deshacerBorrar(nota)
                }

                show()
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX /2 , dY, actionState, isCurrentlyActive)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

        val item = menu.findItem(R.id.search)
        val vistaBusqueda = item.actionView as SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                noteViewModel.buscarQuery = ""
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                noteViewModel.buscarQuery = ""
                return true
            }
        })

        vistaBusqueda.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    buscarNotas(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    buscarNotas(it)
                }
                return true
            }
        })
    }

    private fun buscarNotas(query:String) = lifecycleScope.launch {
        noteViewModel.buscarQuery = query
        noteAdapter.notas = noteViewModel.notas.first().filter {
            it.noteTitle?.contains(query,true) == true ||
                    it.desription?.contains(query,true) == true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.account -> {
                findNavController().navigate(R.id.action_allNotesFragment_to_userInfoFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}