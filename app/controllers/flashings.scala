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
import scala.util.{Failure, Success}

@Singleton
class FlashingCache @Inject()(@NamedCache("flashing-cache") cache:AsyncCacheApi){

  case class CacheValue(var ticket:Int=0,value:collection.mutable.Map[String,Any]){
    def dirty = ticket += 1
  }

  def set(key:String,value:Any)(implicit request:RequestHeader) = {

    val _key = ckey()
    if(true) {
      println("========================= >>> set flashingCache " + _key + ":" + value)
    }


    val vvv = cache.sync.getOrElseUpdate(ckey()){
      val map = collection.mutable.Map.empty[String,Any]
      map.put(key,value)
      CacheValue(0,map)
    }
    vvv.value.put(key,value)
    println("========================= >>> set flashingCache : " + vvv)

  }

  def get[T: ClassTag](key:String)(implicit request:RequestHeader):Option[T] = {

    cache.sync.get(ckey()) match {

      case Some(v) => Some((v.asInstanceOf[CacheValue].value.get(key).asInstanceOf[T]))
      case None => {
        println("caching >>>>>> ")
        None
      }

    }

    /**
    cache.sync.get(ckey(key)) match {

      case Some(v) => Some((v.asInstanceOf[CacheValue].value.get(key).asInstanceOf[T]))
      case None => {
        println("caching >>>>>> ")
        cache.sync.remove(ckey(key));
        None
      }

    }
    */

    /**
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
    */


  }

  def clear()(implicit request:RequestHeader) = {
    cache.remove(ckey())
    println(get(ckey()))
  }

  def isEmpty(implicit request:RequestHeader):Boolean = {
    println(ckey())
      getCache match {
        case Some(v) => {
          println("isEmpty " + v)
          false
        }
        case _ => true
      }
  }

  def isInit(implicit request:RequestHeader) = {

    getCache match {
      case Some(v) => if (getCache.get.ticket == 0) true else false
      case _ => false
    }

  }

  def dirty(implicit request:RequestHeader) = {
    getCache.get.dirty
  }

  private def getCache(implicit request:RequestHeader) = cache.sync.get[CacheValue](ckey())
  private def ckey()(implicit request:RequestHeader):String = request.session.get("csrfToken").getOrElse("")


}


class FlashingCacheFilter @Inject()(implicit ec: ExecutionContext,flashingCache:FlashingCache) extends EssentialFilter with Logging {
    def apply(nextFilter: EssentialAction) = new EssentialAction {

      def apply(requestHeader: RequestHeader) = {

        val accumulator: Accumulator[ByteString, Result] = nextFilter(requestHeader)
        accumulator.map( result => {
          implicit val request = requestHeader
          //println(flashing.get[String]("ff"))
          //The key setted means the flashCached has been set a value

          //
          if(!flashingCache.isEmpty){

              println(">>>>>>>>>>>>>>>>>>>>>> flashCache is not empty")

              if(flashingCache.isInit){
                println(">>>>>>>>>>>>>>>>>>>>>> flashCache init status set flashing flag.")
                result.flashing("setted" -> "true")
                flashingCache.dirty
              }else{

                if(request.flash.isEmpty){
                  println(">>>>>>>>>>>>>>>>>>>>>> Flashing is empty, clear flashingCache.")
                  flashingCache.clear()
                }

              }

          }else{

            println(">>>>>>>>>>>>>>>>>>>>>> flashCache is empty")

          }

          result

        })

      }

    }
}