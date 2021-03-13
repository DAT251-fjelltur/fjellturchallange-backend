package no.hvl.dat251.fjelltur.model

import no.hvl.dat251.fjelltur.dto.TripId
import org.hibernate.annotations.GenericGenerator
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OrderBy

@Entity
class Trip {

  @field:Id
  @field:GeneratedValue(generator = "system-uuid")
  @field:GenericGenerator(name = "system-uuid", strategy = "uuid")
  @field:Column(unique = true)
  var id: TripId = TripId("")

  @field:OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @field:JoinColumn(name = "trip_id")
  @field:OrderBy("recordedAt ASC")
  var locations: MutableList<GPSLocation> = mutableListOf()

  var ongoing: Boolean = true

  @field:OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @field:JoinColumn(name = "trip_id")
  var participants: MutableSet<Account> = mutableSetOf()
}
