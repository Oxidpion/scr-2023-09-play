package models.filter

import models.dao.entities.Product
import play.api.mvc.QueryStringBindable

case class ProductFilter(title: String) {
  def getFilter()(product: Product): Boolean = product.title == title
}

object ProductFilter {

  implicit val queryBindable: QueryStringBindable[ProductFilter] = new QueryStringBindable[ProductFilter] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ProductFilter]] = for {
      titleSeq <- params.get("title")
      title <- titleSeq.headOption
    } yield Right(ProductFilter(title))


    override def unbind(key: String, value: ProductFilter): String = "title=" + value.title
  }
}
