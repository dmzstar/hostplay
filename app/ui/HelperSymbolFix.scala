package ui

import javax.inject.{Inject, Singleton}
import org.webjars.play.WebJarsUtil

object HelperSymbolFix {

  implicit def tupStringToSym(tuple: Tuple2[String,String]) = (Symbol(tuple._1) , tuple._2)
  implicit def tupAnyValToSym(tuple: Tuple2[String,AnyVal]) = (Symbol(tuple._1) , tuple._2)

}



object WebUtil{
  var webjars:WebJarsUtil = _
}
