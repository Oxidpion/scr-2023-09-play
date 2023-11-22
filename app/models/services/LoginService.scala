package models.services

trait LoginService {
  def checkEmail(email: String): Boolean
}

class LoginServiceImpl extends LoginService {
  private val validUserNames = Set("user@gmail.com")
  override def checkEmail(email: String): Boolean = validUserNames.contains(email)
}