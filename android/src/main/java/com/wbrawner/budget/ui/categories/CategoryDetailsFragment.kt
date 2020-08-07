package com.wbrawner.budget.ui.categories


import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.transactions.TransactionListFragment
import kotlinx.android.synthetic.main.fragment_category_details.*

/**
 * A simple [Fragment] subclass.
 */
class CategoryDetailsFragment : Fragment() {
    val viewModel: CategoryDetailsViewModel by viewModels()

    override fun onAttach(context: Context) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(viewModel)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_category_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is AsyncState.Loading -> {
                    categoryDetails.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
                is AsyncState.Success -> {
                    categoryDetails.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    val category = state.data.category
                    activity?.title = category.title
                    categoryDescription.text = category.description
                    childFragmentManager.fragments.firstOrNull()?.let {
                        if (it !is TransactionListFragment) return@let
                        it.reloadItems()
                    } ?: run {
                        val transactionsFragment = TransactionListFragment().apply {
                            arguments = Bundle().apply {
                                putLong(EXTRA_BUDGET_ID, category.budgetId)
                                putLong(EXTRA_CATEGORY_ID, category.id!!)
                            }
                        }
                        childFragmentManager.beginTransaction()
                                .replace(R.id.transactionsFragmentContainer, transactionsFragment)
                                .commit()
                    }
                }
                is AsyncState.Error -> {
                    categoryDetails.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(view.context, "Failed to load context", Toast.LENGTH_SHORT).show()
                }
                is AsyncState.Exit -> {
                    findNavController().navigateUp()
                }
            }
        })
        viewModel.getCategory(arguments?.getLong(EXTRA_CATEGORY_ID))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            val bundle = Bundle().apply {
                putLong(EXTRA_CATEGORY_ID, arguments?.getLong(EXTRA_CATEGORY_ID) ?: -1)
            }
            findNavController().navigate(R.id.addEditCategoryActivity, bundle)
        } else if (item.itemId == android.R.id.home) {
            return findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_editable, menu)
    }
}
