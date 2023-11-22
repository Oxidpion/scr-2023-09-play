package controllers

import com.google.inject.Inject
import models.dto.LoginDTO
import models.{Paging, User, UserId}
import play.api.data.{Form, Forms}
import play.api.data.Forms.{email, mapping, nonEmptyText, of, text}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, Cookie, DiscardingCookie}

import scala.language.postfixOps

class IndexController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  // f: Request => Result

  def action1(): Action[AnyContent] = Action{
    Ok
  }

  def action2() = Action{
    Ok("Hello world")
  }

  def action3() = Action{ rc =>
    Ok(s"Action 3 $rc")
  }

  def action4() = Action{ rc =>
    NotFound
  }

  def action5() = Action{ rc =>
    BadRequest
  }

  def action6() = Action{ rc =>
    InternalServerError("ooops")
  }

  def action7() = Action{ rc =>
    Status(488)("Hello 488")
  }

  def action8() = Action{ rc =>
   Redirect(routes.IndexController.action2())
  }

  def action9(id: Int) = Action{
    Ok(s"Hello $id")
  }

  def action10() = Action{
    val textPlain = Ok("<h1>Hello world</h1>")
    val html = Ok("<h1>Hello world</h1>").as(HTML)
    val json = Ok("{'foo': 'Hello world'}").as(JSON)
    json
  }


  // Продолжаем работать с результатом

  def action11() = Action{
    Ok("Hello world")
      .withHeaders(CACHE_CONTROL -> "max-age=3600", ETAG -> "xx")
  }


  // Установка заголовков + cookie

  def action12() = Action{
    Ok("Hello world")
      .discardingCookies(DiscardingCookie("user"))
  }

  //сессия

  def action13 = Action{
    Ok("Hello session").withSession(
      "connected" -> "user@gmail.com"
    )
  }

  def action14 = Action{ rc =>
    rc.session.get("connected").map{ email =>
      Ok("Hello, " + email)
    }.getOrElse{
      Unauthorized("Ooops you are not connected")
    }

//    Ok("Hello session").withSession(rc.session + ("said Hello" -> "yes"))
//    Ok("Hello session").withSession(rc.session - "said Hello")
  }


  // typed params

  def action15(userId: UserId) = Action{
    Ok(userId.toString)
  }

  def action16(paging: Paging) = Action{
    Ok(paging.toString)
  }


  // body parsing

  def action17 = Action{ rc =>
    val body: AnyContent = rc.body
    val textBody: Option[String] = body.asText
    textBody.map{ txt =>
      Ok(txt)
    }.getOrElse(BadRequest("text plain expected"))
  }

  def action18 = Action(parse.text){ rc =>
    Ok(rc.body)
  }

  // json

  def action19 = Action(parse.json[User]){rc =>
    val user: User = rc.body
    Ok(Json.toJson(user))
  }

  def index() = Action{
    Ok(views.html.index())
  }

  val loginForm = Form[LoginDTO](
    mapping(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 6)
    )(LoginDTO.apply)(LoginDTO.unapply)
  )

  def loginPage() = Action{
    Ok(views.html.login(loginForm))
  }

  def loginPageSubmit() = Action{ implicit rc =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      dto =>
        Redirect(routes.IndexController.index())
          .withSession("email" -> dto.email)
    )
  }

  def logOut() = Action{ rc =>
    Redirect(routes.IndexController.index())
      .withSession(rc.session - "email")
  }


}
