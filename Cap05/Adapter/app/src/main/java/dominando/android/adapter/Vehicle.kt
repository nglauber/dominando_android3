package dominando.android.adapter

data class Vehicle(
    var model: String,
    var year: Int,
    var manufacturer: Int, // 0=VW;1=GM;2=Fiat;3=Ford
    var gasoline: Boolean,
    var ethanol: Boolean
)
