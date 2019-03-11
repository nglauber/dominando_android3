package dominando.android.hotel.di

import dominando.android.hotel.details.HotelDetailsPresenter
import dominando.android.hotel.details.HotelDetailsView
import dominando.android.hotel.form.HotelFormPresenter
import dominando.android.hotel.form.HotelFormView
import dominando.android.hotel.list.HotelListPresenter
import dominando.android.hotel.list.HotelListView
import dominando.android.hotel.repository.HotelRepository
import dominando.android.hotel.repository.sqlite.SQLiteRepository
import org.koin.dsl.module.module

val androidModule = module {
    single { this }
    single {
        SQLiteRepository(ctx = get()) as HotelRepository
    }
    factory { (view: HotelListView) ->
        HotelListPresenter(
            view,
            repository = get()
        )
    }
    factory { (view: HotelDetailsView) ->
        HotelDetailsPresenter(
            view,
            repository = get()
        )
    }
    factory { (view: HotelFormView) ->
        HotelFormPresenter(
            view,
            repository = get()
        )
    }
}
