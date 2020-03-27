package controllers.admin

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class IndexController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  import views.html.{admin => Views}

  def index() = Action { implicit request =>
    Ok(Views.index())
  }

}
