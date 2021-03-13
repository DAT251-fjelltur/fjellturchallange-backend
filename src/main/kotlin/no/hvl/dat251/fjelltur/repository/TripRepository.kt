package no.hvl.dat251.fjelltur.repository

import no.hvl.dat251.fjelltur.model.Trip
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TripRepository : JpaRepository<Trip, String>
