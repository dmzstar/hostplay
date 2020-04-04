package controllers.adminboot

import javax.inject._
import play.api.http.HttpErrorHandler
import play.api.mvc.{AbstractController, Call, ControllerComponents}

@Singleton
class Assets @Inject() (
                         errorHandler: HttpErrorHandler,
                         assetsMetadata: controllers.AssetsMetadata
                       ) extends controllers.AssetsBuilder(errorHandler, assetsMetadata){

}




@Singleton
class A2Controller @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def a2 = Action {
    Ok("a2")
  }

}



