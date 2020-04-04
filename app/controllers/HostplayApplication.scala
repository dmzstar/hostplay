package controllers

import common.uploads.UploadComponents
import controllers.secs.CheckLoginAction
import hostplay.mvc.JavaConvertersSupport
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import play.api.inject.Modules
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class HostplayApplication @Inject()(
                                   environment: Environment,
                                   configuration: Configuration,
                         cc: ControllerComponents) extends AbstractController(cc) {

  def loadPage() = Action{
    Modules.locate(environment,configuration).foreach( println(_))
    Ok("success")
  }

}
