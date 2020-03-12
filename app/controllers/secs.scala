package controllers.secs

import akka.util.ByteString
import controllers.FlashingCache
import javax.inject.Inject
import play.api.Logging
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
