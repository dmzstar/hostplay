package models

import java.util.Optional

import io.ebean.{Ebean, Finder, Model, Query}
import javax.persistence.Id

import scala.reflect.ClassTag

trait ModelTrait extends Model{

  import scala.collection.immutable.List
  import scala.collection.JavaConverters._
  //import scala.language.implicitConversions._

  type JList[T] = java.util.List[T]

  implicit def scalaLongToJavaLong(value:Long) = java.lang.Long.parseLong(value.toString)
  //implicit def listToJavaList[T](list:List[T]) = list.asJavaCollection

  @Id
  var id:java.lang.Long = _

}

trait EntityObject[T]{

  def query()(implicit tag:ClassTag[T]):Query[T] = QO[T].create

  implicit def toScalaOpt(optional: Optional[T])(implicit tag:ClassTag[T]) = if(optional.isPresent) Option[T](optional.get()) else None

}

case class QueryObject[T](){


  def finder()(implicit tag:ClassTag[T]) = new Finder[Long, T](tag.runtimeClass.asInstanceOf[Class[T]])

}

case class QO[T](){
  def create()(implicit tag:ClassTag[T]) = Ebean.find(tag.runtimeClass.asInstanceOf[Class[T]])
}
