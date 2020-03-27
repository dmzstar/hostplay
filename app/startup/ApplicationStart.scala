package startup

import scala.concurrent.Future
import javax.inject._
import play.api.inject.ApplicationLifecycle

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationStart @Inject()(lifecycle: ApplicationLifecycle) {

  println("==================>>> onApplicationStart")

  // Shut-down hook
  lifecycle.addStopHook { () =>
    Future.successful(())
  }
  //...
}


@Singleton
class ApplicationStartInitData @Inject()(lifecycle: ApplicationLifecycle) {

  println("==================>>> onApplicationStartInitData")

  // Shut-down hook
  lifecycle.addStopHook { () =>
    Future.successful(())
  }
  //...
}


import com.google.inject.AbstractModule

class StartupModule extends AbstractModule {
  override def configure() = {
    bind(classOf[ApplicationStart]).asEagerSingleton()
    bind(classOf[ApplicationStartInitData]).asEagerSingleton()
  }
}
