package hu.bme.aut.netcar.data

import com.google.android.gms.maps.model.LatLng

object DataManager {
    var drivers = arrayListOf<Driver>(
        Driver("Kiss Pista", "BMW", "ABC-123", 4, LatLng(47.482552, 19.075173)),
        Driver("Végh Béla", "Audi", "CBA-456", 3, LatLng(47.483115, 19.048300)),
        Driver("Gipsz Jakab", "Toyota", "FJR-638", 4, LatLng(47.462954, 19.038709)),
        Driver("Lakatos Bendegúz", "Mercedes", "PXA-634", 2, LatLng(47.499779, 19.057450)),
        Driver("Deák Ferenc", "Rolls Royce", "GOD-420", 4, LatLng(47.477391, 19.077706)),
        Driver("Bank Attila", "Volkswagen", "MOP-252", 3, LatLng(47.498795, 19.029154))
    )
}