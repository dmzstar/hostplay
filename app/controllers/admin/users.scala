package controllers.admin.users

import controllers.FlashingCache
import controllers.secs.CheckLoginAction
import hostplay.mvc.JavaConvertersSupport
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class UsersController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport with JavaConvertersSupport {

  import views.html.{admin => Views}

  def index = Action  { implicit request =>
    Ok(Views.users.list())
  }

}
