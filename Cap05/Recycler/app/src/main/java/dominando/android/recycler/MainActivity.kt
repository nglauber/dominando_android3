package dominando.android.recycler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var messages = mutableListOf<Message>()
    private var adapter = MessageAdapter(messages, this::onMessageItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lastCustomNonConfigurationInstance.let { savedMessages ->
            if (savedMessages is MutableList<*>) {
                messages.addAll(savedMessages.filterIsInstance(Message::class.java))
            }
        }
        initRecyclerView()
        fabAdd.setOnClickListener {
            addMessage()
        }
    }
    override fun onRetainCustomNonConfigurationInstance(): Any {
        return messages
    }

    private fun initRecyclerView() {
        rvMessages.adapter = adapter
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(pos: Int): Int {
                return if (pos == 0) 2 else 1
            }
        }
        rvMessages.layoutManager = layoutManager
        initSwipeGesture()
    }
    private fun addMessage() {
        val message = Message(
            edtTitle.text.toString(),
            edtText.text.toString()
        )
        messages.add(message)
        adapter.notifyItemInserted(messages.lastIndex)
        edtTitle.text?.clear()
        edtText.text?.clear()
        edtTitle.requestFocus()
    }
    private fun onMessageItemClick(message: Message) {
        val s = "${message.title}\n${message.text}"
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
    private fun initSwipeGesture() {
        val swipe = object : ItemTouchHelper.SimpleCallback(
            0, // Posições permitidas para mover a view. zero = nenhuma
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Posições de swipe
        ) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean = false // Não permite mover itens

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                messages.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipe)
        itemTouchHelper.attachToRecyclerView(rvMessages)
    }
}
