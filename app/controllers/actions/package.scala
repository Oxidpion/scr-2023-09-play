package controllers

import controllers.Assets.Forbidden
import models.services.LoginService
import play.api.mvc.{ActionBuilder, ActionFilter, ActionTransformer, Request, WrappedRequest}

import scala.concurrent.Future

package object actions {
  class UserRequest[A](val email: Option[String], request: Request[A]) extends WrappedRequest[A](request)
  object UserAction extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest]{
    def transform[A](request: Request[A]) = Future.successful(
      new UserRequest(request.session.get("email"), request)
    )
  }

  class PermissionCheckAction(loginService: LoginService) extends ActionFilter[UserRequest]{


    def filter[A](input: UserRequest[A]) = Future.successful(
      if(!input.email.exists(e => loginService.checkEmail(e)))
        Some(Forbidden)
      else None
    )
  }

  def authAction(loginService: LoginService): ActionBuilder[UserRequest] =
    UserAction andThen new PermissionCheckAction(loginService)
}
