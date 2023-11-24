package models.services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.dao.entities.{Product, ProductItem}
import models.dao.repositories.ProductRepository
import models.dto.{ProductItemDTO, ProductRequest, ProductResponse}
import models.filter.ProductFilter

import java.util.UUID
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps

@ImplementedBy(classOf[ProductServiceImpl])
trait ProductService {

  def create(product: ProductRequest): Future[ProductResponse]

  def search(filter: ProductFilter): Future[List[ProductResponse]]

  def all(): Future[List[ProductResponse]]

  def update(productId: String, product: ProductRequest): Future[ProductResponse]

  def delete(productId: String): Future[Unit]
}

@Singleton
class ProductServiceImpl @Inject()(productRepository: ProductRepository) extends ProductService {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  override def create(dto: ProductRequest): Future[ProductResponse] = {
    val productId = UUID.randomUUID().toString
    val product = Product(id = productId, title = dto.title, description = dto.description)
    val productItems = dto.items.map(toProductItem(productId))
    productRepository.insert(product, productItems).map(_ => ProductResponse.from(product, productItems))
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

  override def search(filter: ProductFilter): Future[List[ProductResponse]] = for {
    products <- productRepository.findByTitle(filter.title)
  } yield products.map(p => ProductResponse.from(p._1, p._2))

  override def all(): Future[List[ProductResponse]] = for {
    products <- productRepository.all()
  } yield products.map(p => ProductResponse.from(p._1, p._2))

  override def update(productId: String, dto: ProductRequest): Future[ProductResponse] = {
    val product = Product(id = productId, title = dto.title, description = dto.description)
    val productItems = dto.items.map(toProductItem(productId))
    productRepository.update(product, productItems).map(_ => ProductResponse.from(product, productItems))
  }

  override def delete(productId: String): Future[Unit] = productRepository.delete(productId)
}
