package models.dao.entities

import models.dao.{Currency, Money}

case class Product(id: String = null, title: String, description: String)

case class ProductItem(id: String, priceValue: Int, currency: String, count: Int, exists: Boolean, productId: String = null) {
  def price: Money = Money(priceValue, Currency.withName(currency))
}

//object ProductItem {
//  def tupled
//}


// уникальный идентификатор (строка)
//цена в копейках (число)
//кол-во (число)
//в наличии (да/нет)
