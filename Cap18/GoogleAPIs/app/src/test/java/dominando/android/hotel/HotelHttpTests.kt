package dominando.android.hotel

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dominando.android.hotel.auth.Auth
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository
import dominando.android.hotel.repository.http.HotelHttp
import dominando.android.hotel.repository.http.HotelHttpApi
import dominando.android.hotel.repository.http.Status
import dominando.android.hotel.repository.imagefiles.FindHotelPicture
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class HotelHttpTests {

    private val photoGallery = mock<FindHotelPicture>()
    private val hotelRepository = mock<HotelRepository>()
    private val auth = mock<Auth>()
    private lateinit var hotelHttp: HotelHttp
    private lateinit var mockWebServer: MockWebServer

    @Before fun before_each_test() {
        mockWebServer = MockWebServer()
        val remote = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HotelHttpApi::class.java)
        hotelHttp = HotelHttp(remote, hotelRepository, photoGallery, auth)
    }
    @After fun after_each_test() {
        mockWebServer.shutdown()
    }
    @Test fun should_synchronize_local_hotel_with_remote_server() {
        // Given
        val hotel = Hotel(
            name = "Ritz Recife",
            rating = 5.0f
        )
        authmanager_must_return_a_valid_user()
        local_hotel_storage_have_one_item(hotel)
        server_should_receive_data_from_hotel()
        // When
        hotelHttp.synchronizeWithServer()
        // Then
        local_storage_should_have_hotel_status_as_updated(hotel)
    }
    private fun local_storage_should_have_hotel_status_as_updated(hotel: Hotel) {
        assertThat(hotel.status).isEqualTo(Status.OK)
        assertThat(hotel.serverId).isEqualTo(HOTEL_SERVER_ID)
        verify(hotelRepository).update(hotel)
    }
    private fun server_should_receive_data_from_hotel() {
        mockWebServer.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody("{\"id\":$HOTEL_SERVER_ID}")
            }
        )
        mockWebServer.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(
                    """
                            [
                                {
                                    "id":$HOTEL_SERVER_ID,
                                    "name":"Ritz Recife"
                                }
                            ]
                            """
                )
            }
        )
    }
    private fun local_hotel_storage_have_one_item(hotel: Hotel) {
        whenever(hotelRepository.pending())
            .thenReturn(listOf(hotel))
    }
    private fun authmanager_must_return_a_valid_user() {
        whenever(auth.getUserId())
            .thenReturn("nglauber")
    }

    companion object {
        val HOTEL_SERVER_ID = Random().nextLong()
    }
}
