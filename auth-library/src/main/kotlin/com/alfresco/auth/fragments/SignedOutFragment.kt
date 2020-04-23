package com.alfresco.auth.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.alfresco.android.aims.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Helper class to create screen-dependant dialog
 */
class SignedOutFragment private constructor() {
    companion object {
        @JvmStatic
        val TAG: String = SignedOutBottomSheet::class.java.name

        @JvmStatic
        fun with(context: Context, adapter: SignedOutAdapter): DialogFragment {
            return if (context.resources.getBoolean(R.bool.isTablet)) {
                SignedOutDialog(adapter)
            } else {
                SignedOutBottomSheet(adapter)
            }
        }

        @JvmStatic
        fun accountViewWith(fr: Fragment, container: ViewGroup, title: String, subtitle: String): View {
            val view = fr.layoutInflater.inflate(R.layout.layout_auth_account_row, container, false)

            view.findViewById<TextView>(R.id.title).text = title
            view.findViewById<TextView>(R.id.subtitle).text = subtitle
            view.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_person)

            return view
        }
    }
}

interface SignedOutAdapter {
    fun onSignInButtonClicked(fragment: DialogFragment, view: View)
    fun onAddAccountButtonClicked(fragment: DialogFragment, view: View)
    fun numberOfAccounts(): Int
    fun viewForAccount(fragment: DialogFragment, container: ViewGroup, index: Int): View
}

class SignedOutBottomSheet() : BottomSheetDialogFragment() {
    private var adapter: SignedOutAdapter? = null
    private val viewModel: SignedOutFragmentViewModel by viewModels()

    constructor(adapter: SignedOutAdapter) : this() {
        this.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_signed_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (adapter != null) {
            viewModel.adapter = adapter!!
        } else {
            adapter = viewModel.adapter
        }

        onViewCreated(this, view, viewModel.adapter)
    }

    fun with(adapter: SignedOutAdapter) : SignedOutBottomSheet {
        this.adapter = adapter
        return this
    }
}

class SignedOutDialog() : AppCompatDialogFragment() {
    private var adapter: SignedOutAdapter? = null
    private val viewModel: SignedOutFragmentViewModel by viewModels()

    constructor(adapter: SignedOutAdapter) : this() {
        this.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_signed_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (adapter != null) {
            viewModel.adapter = adapter!!
        } else {
            adapter = viewModel.adapter
        }

        onViewCreated(this, view, viewModel.adapter)
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.let {
            it.setLayout(resources.getDimension(R.dimen.auth_dialog_max_width).toInt(), it.attributes.height)
        }
    }
}

/**
 * Used as a life-cycle aware holder for the adapter
 */
class SignedOutFragmentViewModel : ViewModel() {
    lateinit var adapter: SignedOutAdapter
}

private fun onViewCreated(self: AppCompatDialogFragment, view: View,  adapter: SignedOutAdapter) {

    // Disable dismissing the dialog
    self.isCancelable = false

    // Add button interaction
    val signInButton = view.findViewById<Button>(R.id.sign_in_button)
    signInButton.setOnClickListener {
        adapter.onSignInButtonClicked(self, it)
    }

    val addAccountButton = view.findViewById<ViewGroup>(R.id.add_account_button)
    addAccountButton.setOnClickListener {
        adapter.onAddAccountButtonClicked(self, it)
    }

    // Build item list
    val container = view.findViewById<LinearLayout>(R.id.accounts_container)
    val section = view.findViewById<LinearLayout>(R.id.accounts_section)
    val count = adapter.numberOfAccounts()

    if (count <= 0) {
        section.visibility = View.GONE
    }

    for (i in 0 until count) {
        container.addView(adapter.viewForAccount(self, container, i))
    }
}
