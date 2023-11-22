package controllers

import com.google.inject.Inject
import models.dto.ProductRequest
import models.filter.ProductFilter
import models.services.ProductService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class ProductController @Inject()(productService: ProductService) extends Controller {

  def create() = Action(parse.json[ProductRequest]) { rc =>
    val result = productService.create(rc.body)
    Ok(Json.toJson(result))
  }

  def update(productId: String) = Action(parse.json[ProductRequest]) { rc =>
    val result = productService.update(productId, rc.body)
    Ok(Json.toJson(result))
  }

  def delete(productId: String) = Action {
    productService.delete(productId)
    NoContent
  }

  def search(filter: Option[ProductFilter]) = Action {
    val result = filter.map(productService.search).getOrElse(productService.all())
    Ok(Json.toJson(result))
  }
}
