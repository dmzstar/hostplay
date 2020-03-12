package utils.data

case class ActionMode(value:String)
object ActionMode{
  val edit = ActionMode("edit")
  val create = ActionMode("new")
}
