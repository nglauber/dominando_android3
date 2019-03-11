package dominando.android.hotel.repository.imagefiles

import dominando.android.hotel.model.Hotel

interface FindHotelPicture {
    fun pictureFile(hotel: Hotel): PictureToUpload
}
