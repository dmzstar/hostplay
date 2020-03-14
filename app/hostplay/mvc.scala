package hostplay.mvc

trait JavaConvertersSupport{

  import scala.jdk.CollectionConverters._
  implicit def jlistToScalaList[T](list:java.util.List[T]):List[T] = list.asScala.toList

  class DataList[T](val list:List[T])
  object DataList{
    implicit def to[T](list:java.util.List[T]) = new DataList(list)
  }

}
