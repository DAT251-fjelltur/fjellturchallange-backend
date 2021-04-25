package no.hvl.dat251.fjelltur.repository

import no.hvl.dat251.fjelltur.entity.GPSLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GPSLocationRepository : JpaRepository<GPSLocation, Long>
