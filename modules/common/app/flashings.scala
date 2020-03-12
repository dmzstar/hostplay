package controllers

import javax.inject.{Inject, Singleton}
import play.api.cache.{AsyncCacheApi, NamedCache}
import play.api.mvc.Request

import scala.reflect.ClassTag
import javax.inject.Inject
import akka.util.ByteString
import play.api.Logging
import play.api.http.Status
import play.api.libs.streams.Accumulator
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class FlashingCache @Inject()(@NamedCache("flashing-cache") cache:AsyncCacheApi){

  case class CacheValue(ticket:Int=0,value:collection.mutable.Map[String,Any])

  def set(key:String,value:Any)(implicit request:RequestHeader) = {
    //println("setFlash >>> " + request.flash.get("cacheToken"))
    val vvv = cache.sync.getOrElseUpdate(ckey(key)){
      val map = collection.mutable.Map.empty[String,Any]
      map.put(key,value)
      CacheValue(0,map)
    }
    println("========================= >>> set flashingCache : " + vvv)
  }

  def get[T: ClassTag](key:String)(implicit request:RequestHeader):Option[T] = {

    /**
    request.flash.get("cacheToken") match {
      case Some(c) => println("============== >>> getMap")
    }
    */


    request.flash.get("cacheToken") match {

      case Some(c) => cache.sync.get(ckey(key)) match {

        case Some(v) => Some((v.asInstanceOf[CacheValue].value.get(key).asInstanceOf[T]))
        case None => {
          println("caching >>>>>> ")
          cache.sync.remove(ckey(key));None
        }

      }

      case _ => None

    }



  }

  def remove = ()

  private def ckey(key:String)(implicit request:RequestHeader) = request.session.get("csrfToken") + "-" + key


}


class FlashingCacheFilter @Inject()(implicit ec: ExecutionContext,flashing:FlashingCache) extends EssentialFilter with Logging {
    def apply(nextFilter: EssentialAction) = new EssentialAction {

      def apply(requestHeader: RequestHeader) = {


        val accumulator: Accumulator[ByteString, Result] = nextFilter(requestHeader)
        accumulator


      }

    }
}