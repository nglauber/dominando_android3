package dominando.android.navigationapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnGo.setOnClickListener {
            val args = Bundle().apply {
                putString("full_name", "Nelson Glauber")
                putInt("age", 34)
            }
            Navigation.findNavController(requireActivity(), R.id.navHostFragment)
                .navigate(R.id.action_homeFragment_to_completeFragment, args)
        }
    }

}
