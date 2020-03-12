package controllers.member

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}


@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index = Action{
    Ok(views.html.member.index())
  }

}