package common.utils

case class ActionMode(value:String)

object ActionMode{
  val edit = ActionMode("edit")
  val create = ActionMode("new")
}
