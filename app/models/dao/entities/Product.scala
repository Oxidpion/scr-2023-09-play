package models.dao.entities

import models.dao.Money

case class Product(id: String, title: String, description: String, items: List[ProductItem])

case class ProductItem(id: String, price: Money, count: Integer, exists: Boolean)



// уникальный идентификатор (строка)
//цена в копейках (число)
//кол-во (число)
//в наличии (да/нет)
