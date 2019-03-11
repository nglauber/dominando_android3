package dominando.android.hotel.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.*
import dominando.android.hotel.common.SingleLiveEvent
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

class HotelListViewModel(
    private val repository: HotelRepository
) : ViewModel() {
    var hotelIdSelected: Long = -1
    private val searchTerm = MutableLiveData<String>()
    private val hotels = Transformations.switchMap(searchTerm) { term ->
        repository.search("%$term%")
    }
    private val inDeleteMode = MutableLiveData<Boolean>().apply {
        value = false
    }
    private val selectedItems = mutableListOf<Hotel>()
    private val selectionCount = MutableLiveData<Int>()
    private val selectedHotels = MutableLiveData<List<Hotel>>().apply {
        value = selectedItems
    }
    private val deletedItems = mutableListOf<Hotel>()
    private val showDeletedMessage = SingleLiveEvent<Int>()
    private val showDetailsCommand = SingleLiveEvent<Hotel>()
    fun isInDeleteMode(): LiveData<Boolean> = inDeleteMode

    fun getSearchTerm(): LiveData<String>? = searchTerm

    fun getHotels(): LiveData<List<Hotel>>? = hotels

    fun selectionCount(): LiveData<Int> = selectionCount

    fun selectedHotels(): LiveData<List<Hotel>> = selectedHotels

    fun showDeletedMessage(): LiveData<Int> = showDeletedMessage

    fun showDetailsCommand(): LiveData<Hotel> = showDetailsCommand

    fun selectHotel(hotel: Hotel) {
        if (inDeleteMode.value == true) {
            toggleHotelSelected(hotel)
            if (selectedItems.size == 0) {
                inDeleteMode.value = false
            } else {
                selectionCount.value = selectedItems.size
                selectedHotels.value = selectedItems
            }
        } else {
            showDetailsCommand.value = hotel
        }
    }
    private fun toggleHotelSelected(hotel: Hotel) {
        val existing = selectedItems.find { it.id == hotel.id }
        if (existing == null) {
            selectedItems.add(hotel)
        } else {
            selectedItems.removeAll { it.id == hotel.id }
        }
    }
    fun search(term: String = "") {
        searchTerm.value = term
    }
    fun setInDeleteMode(deleteMode: Boolean) {
        if (!deleteMode) {
            selectionCount.value = 0
            selectedItems.clear()
            selectedHotels.value = selectedItems
            showDeletedMessage.value = selectedItems.size
        }
        inDeleteMode.value = deleteMode
    }

    fun deleteSelected() {
        repository.remove(*selectedItems.toTypedArray())
        deletedItems.clear()
        deletedItems.addAll(selectedItems)
        setInDeleteMode(false)
        showDeletedMessage.value = deletedItems.size
    }
    fun undoDelete() {
        if (deletedItems.isNotEmpty()) {
            for (hotel in deletedItems) {
                hotel.id = 0L
                repository.save(hotel)
            }
        }
    }
}
