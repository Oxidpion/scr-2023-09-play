package models.dao.repositories

import com.google.inject.{ImplementedBy, Singleton}
import models.dao.entities.Product

import scala.collection._
import scala.util.Random

@ImplementedBy(classOf[ProductRepositoryMemory])
trait ProductRepository {
  def findAll(condition: Product => Boolean): List[Product]
  def list(): List[Product] = findAll(_ => true)
  def insert(product: Product): Product
  def update(product: Product): Unit
  def delete(id: String): Unit
}


@Singleton
class ProductRepositoryMemory extends ProductRepository {
  private val products = mutable.HashMap.empty[String, Product]

  override def insert(product: Product): Product = {
    val productId = Random.nextInt(1000).toString
    products.put(productId, product)
    Product(productId, product.title, product.description, product.items)
  }

  override def findAll(condition: Product => Boolean): List[Product] =
    products.values.filter(condition).toList

  override def update(product: Product): Unit = products.put(product.id, product)

  override def delete(productId: String): Unit = products.remove(productId)
}