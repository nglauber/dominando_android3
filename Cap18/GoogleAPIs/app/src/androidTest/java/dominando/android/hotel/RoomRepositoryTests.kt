package dominando.android.hotel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.http.Status
import dominando.android.hotel.repository.room.HotelDatabase
import dominando.android.hotel.repository.room.RoomRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomRepositoryTests {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var repo: RoomRepository
    private lateinit var dummyHotel: Hotel

    @Before fun before_each_test() {
        val db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            HotelDatabase::class.java).build()
        repo = RoomRepository(db)
        dummyHotel = Hotel(
            id = 0,
            name = "Hotel Recife",
            address = "Av. Boa Viagem",
            rating = 4.5F
        )
    }
    @Test fun insert_hotel_must_change_id_and_status() {
        repo.save(dummyHotel)
        assertNotEquals(dummyHotel.id, 0)
        assertEquals(dummyHotel.status, Status.INSERT)
    }
    @Test fun insert_the_same_hotel_must_update_the_record() {
        repo.save(dummyHotel)
        val id = dummyHotel.id
        assertNotEquals(id, 0)
        val hotel1 = LiveDataTestUtil.getValue(repo.hotelById(id))
        repo.save(dummyHotel)
        val hotel2 = LiveDataTestUtil.getValue(repo.hotelById(id))
        assertEquals(hotel2.status, Status.UPDATE)
        assertEquals(hotel1.id, hotel2.id)
        assertEquals(hotel1.name, hotel2.name)
        assertEquals(hotel1.address, hotel2.address)
        assertEquals(hotel1.rating, hotel2.rating)
    }
    @Test fun update_all_hotel_fields() {
        repo.save(dummyHotel)
        val hotelId = dummyHotel.id
        val updatedHotel = newHotel()
        updatedHotel.id = hotelId
        repo.save(updatedHotel)
        val loadedHotel = LiveDataTestUtil.getValue(repo.hotelById(hotelId))
        assertEquals(updatedHotel, loadedHotel)
    }
    @Test fun load_all_inserted_hotels() {
        val allHotels = listOf(
            newHotel(),
            newHotel(),
            newHotel()
        )
        allHotels.forEach {
            repo.save(it)
        }
        val returnedList = LiveDataTestUtil.getValue(repo.search("%"))
        assertEquals(allHotels.size, returnedList.size)
        assert(returnedList.containsAll(allHotels))
    }
    @Test fun remove_hotel() {
        repo.save(dummyHotel)
        repo.remove(dummyHotel)
        val returnedHotel = LiveDataTestUtil.getValue(repo.hotelById(dummyHotel.id))
        assertNull(returnedHotel)
    }
    private fun newHotel() = Hotel(
        name = UUID.randomUUID().toString(),
        address = UUID.randomUUID().toString(),
        rating = (Math.random() * 5.0).toFloat()
    )
}