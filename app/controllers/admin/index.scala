package controllers.admin

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class IndexController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  import views.html.blogs.{articles => Views}

  def index() = Action { implicit request =>
    Ok(Views.list())
  }

  def index2() = Action { implicit request =>
    Ok(Views.list())
  }

}
