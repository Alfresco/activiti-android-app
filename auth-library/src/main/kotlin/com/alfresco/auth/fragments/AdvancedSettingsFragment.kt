package com.alfresco.auth.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.alfresco.android.aims.R
import com.alfresco.android.aims.databinding.FrAimsAdvancedSettingsBinding
import com.alfresco.auth.activity.LoginViewModel
import com.alfresco.common.FragmentBuilder
import com.alfresco.ui.components.Snackbar

class AdvancedSettingsFragment : DialogFragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private val rootView: View get() = view!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FrAimsAdvancedSettingsBinding>(inflater, R.layout.fr_aims_advanced_settings, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.startEditing()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setHasNavigation(true)
    }

    override fun onStart() {
        super.onStart()
        activity?.title = resources.getString(R.string.auth_settings_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.aims_advanced_settings, menu)

        val item = menu.findItem(R.id.aims_save_settings)
        val action = item.actionView.findViewById<TextView>(R.id.tvSaveSettingsAction)

        action.setOnClickListener {
            Snackbar.make(rootView,
                    Snackbar.STYLE_SUCCESS,
                    R.string.auth_settings_prompt_success_title,
                    R.string.auth_settings_prompt_success_subtitle,
                    Snackbar.LENGTH_LONG).show()
            viewModel.saveConfigChanges()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {
        override val fragmentTag = TAG

        override fun build(args: Bundle): Fragment {
            val fragment = AdvancedSettingsFragment()
            fragment.arguments = args

            return fragment
        }
    }

    companion object {

        val TAG = AdvancedSettingsFragment::class.java.name

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}