package dominando.android.hotel.repository.http

interface Status {
    companion object {
        const val OK = 0
        const val INSERT = 1
        const val UPDATE = 2
        const val DELETE = 3
    }
}