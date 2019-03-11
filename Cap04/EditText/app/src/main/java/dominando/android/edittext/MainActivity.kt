package dominando.android.edittext

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtPassword.setOnEditorActionListener { v, actionId, event ->
            if (v == edtPassword && EditorInfo.IME_ACTION_DONE == actionId) {
                registerUser()
            }
            false
        }
        edtCep.addTextChangedListener(object : TextWatcher {
            var isUpdating = false
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Quando o texto é alterado o onTextChange é chamado
                // Essa flag evita a chamada infinita desse método
                if (isUpdating) {
                    isUpdating = false
                    return
                }
                // Ao apagar o texto, a máscara é removida, então o posicionamento
                // do cursor precisa saber se o texto atual tinha ou não máscara
                val hasMask = s.toString().indexOf('.') > -1 ||
                        s.toString().indexOf('-') > -1
                // Remove o '.' e '-' da String
                var str = s.toString().filterNot { it == '.' || it == '-' }
                // Os parâmetros before e count dizem o tamanho
                // anterior e atual da String digitada, se count > before é
                // porque está digitando, caso contrário, está apagando
                if (count > before) {
                    if (str.length > 5) {
                        // Se tem mais de 5 caracteres (sem máscara) coloca o '.' e o '-'
                        str = "${str.substring(0, 2)}.${str.substring(2, 5)}-${str.substring(5)}"
                    } else if (str.length > 2) {
                        // Se tem mais de 2, coloca só o ponto
                        str = "${str.substring(0, 2)}.${str.substring(2)}"
                    }
                    // Seta a flag para evitar chamada infinita
                    isUpdating = true
                    // Seta o novo texto
                    edtCep.setText(str)
                    // Seta a posição do cursor
                    edtCep.setSelection(edtCep.text?.length ?: 0)
                } else {
                    isUpdating = true
                    edtCep.setText(str)
                    // Se estiver apagando posiciona o cursor no local correto
                    // Isso trata a deleção dos caracteres da máscara
                    edtCep.setSelection(Math.max(0,
                        Math.min(if (hasMask) start - before else start, str.length)))
                }
            }
        })
    }
    private fun registerUser() {
        val name = edtName.text.toString()
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()
        var isValid = true
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = getString(R.string.msg_error_email)
            isValid = false
        } else {
            tilEmail.error = null
        }
        if (password != "123") {
            tilPassword.error = getString(R.string.msg_error_password)
            isValid = false
        } else {
            tilPassword.error = null
        }
        if (isValid) {
            Toast.makeText(this,
                getString(R.string.msg_success, name, email),
                Toast.LENGTH_SHORT).show()
        }
    }
}

