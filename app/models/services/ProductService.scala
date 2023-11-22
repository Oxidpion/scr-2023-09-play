package models.services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.entities.Product
import models.dao.repositories.ProductRepository
import models.dto.{ProductRequest, ProductResponse}
import models.filter.ProductFilter

@ImplementedBy(classOf[ProductServiceImpl])
trait ProductService {

  def create(product: ProductRequest): ProductResponse

  def search(filter: ProductFilter): List[ProductResponse]

  def all(): List[ProductResponse]

  def update(productId: String, product: ProductRequest): ProductResponse

  def delete(productId: String): Unit
}

@Singleton
class ProductServiceImpl @Inject()(productRepository: ProductRepository) extends ProductService {
  override def create(dto: ProductRequest): ProductResponse = {
    val product = Product(null, dto.title, dto.description, dto.items.map(_.toProductItem()))
    val result = productRepository.insert(product)
    ProductResponse.from(result)
  }

  override def search(filter: ProductFilter): List[ProductResponse] =
    productRepository.findAll(filter.getFilter()).map(ProductResponse.from)

  override def all(): List[ProductResponse] = productRepository.findAll(_ => true).map(ProductResponse.from)

  override def update(productId: String, dto: ProductRequest): ProductResponse = {
    val product = Product(productId, dto.title, dto.description, dto.items.map(_.toProductItem()))
    productRepository.update(product)
    ProductResponse.from(product)
  }

  override def delete(productId: String): Unit = productRepository.delete(productId)
}
