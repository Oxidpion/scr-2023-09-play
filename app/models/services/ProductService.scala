package models.services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.entities.{Product, ProductItem}
import models.dao.repositories.ProductRepository
import models.dto.{ProductItemDTO, ProductRequest, ProductResponse}
import models.filter.ProductFilter

import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps

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
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  override def create(dto: ProductRequest): ProductResponse = {
    val productId = UUID.randomUUID().toString
    val product = Product(id = productId, title = dto.title, description = dto.description)
    val productItems = dto.items.map(toProductItem(productId))
    Await.result(productRepository.insert(product, productItems), 5 seconds)
    ProductResponse.from(product, productItems)
  }

  private def toProductItem(productId: String)(item: ProductItemDTO) =
    ProductItem(
      id = item.id,
      priceValue = item.price.value,
      currency = item.price.currency,
      count = item.count,
      exists = item.exists,
      productId = productId
    )

  override def search(filter: ProductFilter): List[ProductResponse] = {
    val result = for {
      products <- productRepository.findByTitle(filter.title)
    } yield products.map(p => ProductResponse.from(p._1, p._2))
    Await.result(result, 5 seconds)
  }

  override def all(): List[ProductResponse] = {
    val result = for {
      products <- productRepository.all()
    } yield products.map(p => ProductResponse.from(p._1, p._2))
    Await.result(result, 5 seconds)
  }

  override def update(productId: String, dto: ProductRequest): ProductResponse = {
    val product = Product(id = productId, title = dto.title, description = dto.description)
    val productItems = dto.items.map(toProductItem(productId))
    val result = productRepository.update(product, productItems)
    Await.result(result, 5 seconds)
    ProductResponse.from(product, productItems)
  }

  override def delete(productId: String): Unit = Await.result(productRepository.delete(productId), 5 seconds)
}
