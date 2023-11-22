package models.dao

import models.dao.Currency._

case class Money(value: Integer, currency: Currency)

object Money {
  def kopeiks(value: Integer): Unit = Money(value, Currency.RUB)
}
