package dominando.android.hotel

import dominando.android.hotel.form.HotelValidator
import dominando.android.hotel.model.Hotel
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test

class HotelValidatorTests {
    private val validator by lazy {
        HotelValidator()
    }
    private val validInfo = Hotel(
        name = "Ritz Lagoa da Anta",
        address = "Av. Brigadeiro Eduardo Gomes de Brito, 546, Maceió/AL",
        rating = 4.9f
    )
    @Test fun should_validate_info_for_a_valid_hotel() {
        // Junit4 Style
        assertTrue(validator.validate(validInfo))
        // AssertJ Style
        assertThat(validator.validate(validInfo)).isTrue()
    }
    @Test fun should_not_validate_info_without_a_hotel_name() {
        val missingName = validInfo.copy(name = "")
        assertThat(validator.validate(missingName)).isFalse()
    }
    @Test fun should_not_validate_info_without_a_hotel_address() {
        val missingAddress = validInfo.copy(address = "")
        assertThat(validator.validate(missingAddress)).isFalse()
    }
    @Test fun should_not_validate_info_when_name_is_outside_accepted_size() {
        val nameTooShort = validInfo.copy(name = "I")
        assertThat(validator.validate(nameTooShort)).isFalse()
    }
    @Test fun should_not_validate_info_when_address_is_outside_accepted_size() {
        val bigAddress =
            "Av. Brigadeiro Eduardo Gomes de Brito, 546 - " +
                    "Lagoa da Anta, Maceió - AL, 57038-230"
        val addressTooLong = validInfo.copy(address = bigAddress)
        assertThat(validator.validate(addressTooLong)).isFalse()
    }
}

