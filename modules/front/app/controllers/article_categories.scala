package controllers

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api._
import play.api.mvc._

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class CategoriesRouter @Inject() (controller: Categories) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => controller.list
  }
}

@Singleton
class Categories @Inject()(
                         cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

    def list = Action{ implicit request =>
        Ok("yes")
    }

  def show = Action{ implicit request =>
    Ok("yes")
  }

}
