package no.hvl.dat251.fjelltur.dto

import no.hvl.dat251.fjelltur.model.Account

data class AccountCreationRequest(
  val username: String,
  val password: String,
  val photoUrl: String?,
)

data class LoginRequest(
  val username: String,
  val password: String,
)

data class RegisteredAccountResponse(
  val id: String,
  val username: String,
  val photoUrl: String,
  val score: Float,
  val disabled: Boolean,
  val permissions: Set<String>
)

fun Account.toResponse(): RegisteredAccountResponse = RegisteredAccountResponse(id.toString(), username, photoUrl ?: "", score, disabled, authorities)
