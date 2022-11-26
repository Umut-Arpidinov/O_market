package kg.o.internlabs.omarket.presentation.ui.fragments.registration

import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import kg.o.internlabs.core.base.BaseFragment
import kg.o.internlabs.omarket.databinding.FragmentRegistrationFilledFieldsBinding

class RegistrationFragmentFilledFields : BaseFragment<FragmentRegistrationFilledFieldsBinding,
        RegistrationViewModel>() {
    override val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this)[RegistrationViewModel::class.java]
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentRegistrationFilledFieldsBinding {
        return FragmentRegistrationFilledFieldsBinding.inflate(inflater)
    }


}