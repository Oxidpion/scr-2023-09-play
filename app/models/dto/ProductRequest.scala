package models.dto

import models.dao.entities.{Product, ProductItem}
import play.api.libs.json.{Json, Reads, Writes}

case class ProductRequest(title: String, description: String, items: List[ProductItemDTO])

object ProductRequest {
  implicit val reads: Reads[ProductRequest] = Json.reads[ProductRequest]
}

case class ProductResponse(id: String, title: String, description: String, items: List[ProductItemDTO])

object ProductResponse {
  def from(from: Product, items: List[ProductItem]): ProductResponse = ProductResponse(
    from.id,
    from.title,
    from.description,
    items.map(ProductItemDTO.from)
  )

  implicit val writes: Writes[ProductResponse] = Json.writes[ProductResponse]
}

case class ProductItemDTO(id: String, price: MoneyDTO, count: Int, exists: Boolean) {

  def toProductItem(productId: String): ProductItem = ProductItem(
    id = id, priceValue = price.value, currency = price.currency, count = count, exists = exists, productId)
}

object ProductItemDTO {

  def from(from: ProductItem): ProductItemDTO = ProductItemDTO(
    from.id,
    MoneyDTO.from(from.price),
    from.count,
    from.exists
  )

  implicit val reads: Reads[ProductItemDTO] = Json.reads[ProductItemDTO]
  implicit val writes: Writes[ProductItemDTO] = Json.writes[ProductItemDTO]
}