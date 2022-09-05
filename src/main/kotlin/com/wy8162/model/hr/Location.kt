package com.wy8162.model.hr

import org.jetbrains.exposed.sql.Table

data class Location(
    val locationId: Int,
    val streetAddress: String,
    val postalCode: String,
    val city: String,
    val stateProvince: String,
    val countryId: String
)

object LocationEntity : Table(name = "locations") {
    val locationId = integer("location_id").uniqueIndex("locations_pkey")
    val streetAddress = varchar("street_address", 128)
    val postCode = varchar("postal_code", 32)
    val city = varchar("city", 32)
    val stateProvince = varchar("state_province", 32)
    val countryId = varchar("country_id", 2) references CountryEntity.country_id

    override val primaryKey = PrimaryKey(locationId, name = "locations_pkey")
}
