package no.hvl.dat251.fjelltur.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author Mathias
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
class NotUniqueRuleException : RuntimeException("There is already a rule with this name")
