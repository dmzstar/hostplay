package controllers.adminboot

import javax.inject._

import play.api.http.HttpErrorHandler

@Singleton
class Assets @Inject() (
                         errorHandler: HttpErrorHandler,
                         assetsMetadata: controllers.AssetsMetadata
                       ) extends controllers.AssetsBuilder(errorHandler, assetsMetadata){

}
