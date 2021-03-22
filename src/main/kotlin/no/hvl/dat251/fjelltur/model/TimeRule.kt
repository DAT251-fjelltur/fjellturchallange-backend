package no.hvl.dat251.fjelltur.model

import no.hvl.dat251.fjelltur.dto.TIMERULE
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(TIMERULE)
class TimeRule : Rule() {
  @field:Column
  var minTime: Int? = 0
}
