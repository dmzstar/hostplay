package controllers.secs


import java.util.Optional

import com.google.inject.{AbstractModule, Provides}
import javax.inject.{Inject, Singleton}
import org.pac4j.core.authorization.authorizer.{IsAuthenticatedAuthorizer, RequireAnyRoleAuthorizer}
import org.pac4j.core.client.Clients
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.config.Config
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.UsernamePasswordCredentials
import org.pac4j.core.exception.http.RedirectionAction
import org.pac4j.core.matching.matcher.PathMatcher
import org.pac4j.core.profile.UserProfile
import org.pac4j.core.profile.creator.ProfileCreator
import org.pac4j.core.redirect.RedirectionActionBuilder
import org.pac4j.http.client.indirect.FormClient
import org.pac4j.play.scala.{DefaultSecurityComponents, SecurityComponents}
import org.pac4j.play.{CallbackController, LogoutController}
import org.pac4j.play.store.{PlayCacheSessionStore, PlaySessionStore}
import play.api.http.{DefaultHttpFilters, EnabledFilters}
import play.api.{Configuration, Environment, Logging}
import play.api.libs.streams.Accumulator
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

class CheckLoginAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
   {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
   request.session.get("loginUser") match {
     case Some(name) => block(request)
     case _ => Future.successful(Forbidden("Authentication failed!"))
   }
  }
}

/**
class SecFilters @Inject()(implicit ec: ExecutionContext) extends EssentialFilter with Logging {
  def apply(nextFilter: EssentialAction) = new EssentialAction {

    def apply(requestHeader: RequestHeader) = {

      println("==========>>> " + requestHeader.uri)
      if(requestHeader.uri.startsWith("/admin")){
        requestHeader.session.get("loginUser").map{ v =>
          nextFilter(requestHeader)
        }
      }

      //val accumulator: Accumulator[ByteString, Result] = nextFilter(requestHeader)
      //accumulator


    }

  }
}*/



class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SecurityModel init.")

  //val baseUrl = configuration.get[String]("baseUrl")

  val baseUrl = ""

  override def configure(): Unit = {

    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SecurityModule configure!!!")

    bind(classOf[PlaySessionStore]).to(classOf[PlayCacheSessionStore])

    // callback
    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/?defaulturlafterlogout")
    callbackController.setMultiProfile(true)
    bind(classOf[CallbackController]).toInstance(callbackController)

    // logout
    val logoutController = new LogoutController()
    logoutController.setDefaultUrl("/")
    bind(classOf[LogoutController]).toInstance(logoutController)

    // security components used in controllers
    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])

  }

  import org.pac4j.http.client.indirect.FormClient
  import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator

  class MyPwdAuthor extends SimpleTestUsernamePasswordAuthenticator{

    override def validate(credentials: UsernamePasswordCredentials, context: WebContext): Unit = {
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> MyPwdAuthorValidate")
      super.validate(credentials, context)
    }

  }

  @Provides
  def provideFormClient:FormClient = {

    println(s">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> provideFormClient")

    val client = new FormClient("/loginPage", new MyPwdAuthor)
    client.setName("FormClient")
    client.setUsernameParameter("username")
    client.setPasswordParameter("password")
    /**
    client.setRedirectionActionBuilder(new RedirectionActionBuilder {
      override def getRedirectionAction(context: WebContext): Optional[RedirectionAction] = {

      }
    })
     */
    //client.setCallbackUrl("/callback?client_name=FormClient")
    client

  }

  var i = 0;

  /**
   * bugs? why called multiple times?
   * @param formClient
   * @return
   */
  @Provides
  def provideConfig(formClient:FormClient):Config = {

    println(s">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> provideConfig ${i}")
    i += 1

    val clients = new Clients("/callback",formClient,new AnonymousClient())
    val config = new Config(clients)

    config.addAuthorizer("isAuthenticated", new IsAuthenticatedAuthorizer[UserProfile]())
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer[UserProfile]("ROLE_ADMIN"))
    //config.addAuthorizer("custom", new CustomAuthorizer)
    //config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/loginAction$"))
    //config.setHttpActionAdapter(new DemoHttpActionAdapter())
    config

  }


}



/**
@Singleton
class HoesLoginController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {



}
*/


import javax.inject.Inject
import org.pac4j.play.filters.SecurityFilter
import play.api.http.HttpFilters

class Filters @Inject() (
                          defaultFilters: EnabledFilters,
                          securityFilter: SecurityFilter
                        ) extends DefaultHttpFilters(defaultFilters.filters :+ securityFilter: _*){

  println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Init SecurityFilter")

}
