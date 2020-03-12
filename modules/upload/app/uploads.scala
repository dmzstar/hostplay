package common.uploads

import java.nio.file.Paths

import javax.inject._
import play.api.{Configuration, Environment, Mode}
import com.google.inject.AbstractModule


@Singleton
class UploadComponents @Inject()(env:Environment,
                                 config:Configuration) {

  lazy val baseDir:String = {

    val path:String = if(env.mode == Mode.Dev){
      env.rootPath.getPath + "/public/uploads/"
    }else{
      config.get[String]("web.upload.dir")
    }

    val dir = Paths.get(path).toFile
    if(!dir.exists()){
      dir.mkdirs()
    }

    dir.getAbsolutePath

  }

  println("========================>>>UploadComponents init :: " + baseDir)

}

class UploadModule extends AbstractModule {
  override def configure() = {
    bind(classOf[UploadComponents]).asEagerSingleton()
  }
}
