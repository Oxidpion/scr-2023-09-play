package controllers

import com.google.inject.Inject
import models.dto.ProductRequest
import models.filter.ProductFilter
import models.services.ProductService
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class ProductController @Inject()(productService: ProductService) extends Controller {


  def create() = Action.async(parse.json[ProductRequest]) { rc =>
    productService.create(rc.body).map { result =>
      Ok(Json.toJson(result))
    }
  }

  def update(productId: String) = Action.async(parse.json[ProductRequest]) { rc =>
    productService.update(productId, rc.body).map { result =>
      Ok(Json.toJson(result))
    }
  }

  def delete(productId: String) = Action.async {
    productService.delete(productId).map { _ =>
      NoContent
    }
  }

  def search(filter: Option[ProductFilter]) = Action.async {
    filter.map(productService.search).getOrElse(productService.all()).map { result =>
      Ok(Json.toJson(result))
    }
  }
}
