package com.activiti.android.app.fragments.account.aims

import android.os.Bundle
import android.view.*
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.activiti.android.app.R
import com.activiti.android.app.activity.AIMSWelcomeViewModel
import com.activiti.android.app.databinding.FrAimsAdvancedSettingsBinding
import com.activiti.android.ui.fragments.AlfrescoFragment
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder
import com.google.android.material.textfield.TextInputLayout

class AIMSAdvancedSettingsFragment : AlfrescoFragment() {

    private val viewModel: AIMSWelcomeViewModel by activityViewModels()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.aims_advanced_settings, menu)

        val item = menu.findItem(R.id.aims_save_settings)
        val action = item.actionView.findViewById<TextView>(R.id.tvSaveSettingsAction)

        action.setOnClickListener {
            Toast.makeText(activity, "Settings Saved Successfully", Toast.LENGTH_LONG).show()
            viewModel.saveConfigChanges()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    class Builder : AlfrescoFragmentBuilder {

        constructor(activity: FragmentActivity) : super(activity) {
            extraConfiguration = Bundle()
        }

        constructor(activity: FragmentActivity, configuration: Map<String, Object>) : super(activity, configuration)

        override fun createFragment(bundle: Bundle) = newInstancebyTemplate(bundle)
    }

    companion object {

        val TAG = AIMSAdvancedSettingsFragment::class.java.name

        fun newInstancebyTemplate(args: Bundle): AIMSAdvancedSettingsFragment {
            val fragment = AIMSAdvancedSettingsFragment()
            fragment.arguments = args

            return fragment
        }

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}