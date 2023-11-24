package models.dao.schema

import models.dao.entities.{Product, ProductItem}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

class ProductSlickSchema(tag: Tag) extends Table[Product](tag, "Product") {

  def id = column[String]("id", O.PrimaryKey)

  def title = column[String]("title")

  def description = column[String]("description")

  override def * : ProvenShape[Product] = (id, title, description) <> (Product.tupled, Product.unapply)
}

class ProductItemSlickSchema(tag: Tag) extends Table[ProductItem](tag, "ProductItem") {

  def id = column[String]("id", O.PrimaryKey)

  def price = column[Int]("price")

  def currency = column[String]("currency")

  def count = column[Int]("count")

  def exists = column[Boolean]("exists")

  def productId = column[String]("productId")

  override def * : ProvenShape[ProductItem] = (id, price, currency, count, exists, productId) <>
    (ProductItem.tupled, ProductItem.unapply)
}
