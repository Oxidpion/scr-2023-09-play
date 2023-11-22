package models.dto

import models.dao.{Currency, Money}
import play.api.libs.json.{Json, Reads, Writes};

case class  MoneyDTO(value: Int, currency: String) {
  def toMoney(): Money = Money(value, Currency.withName(currency))
}

object MoneyDTO {
  def from(money: Money): MoneyDTO = {
    MoneyDTO(money.value, money.currency.toString)
  }

  implicit val reads: Reads[MoneyDTO] = Json.reads[MoneyDTO]
  implicit val writes: Writes[MoneyDTO] = Json.writes[MoneyDTO]
}