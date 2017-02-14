package tel.schich.webnbt

sealed abstract class NbtValue(val tag: NbtTag)
sealed abstract class NbtTag(val id: Byte) {
  def this(id: Int) = this(id.toByte)
}

case class NbtCompound(value: Map[String, NbtValue]) extends NbtValue(NbtCompound)
object NbtCompound extends NbtTag(10) {
  val end: Byte = 0.toByte
}

case class NbtList(tagId: Byte, value: Seq[NbtValue]) extends NbtValue(NbtList)
object NbtList extends NbtTag(9)

case class NbtByteArray(value: Seq[Byte]) extends NbtValue(NbtByteArray)
object NbtByteArray extends NbtTag(7)

case class NbtIntArray(value: Seq[Int]) extends NbtValue(NbtIntArray)
object NbtIntArray extends NbtTag(11)

case class NbtByte(value: Byte) extends NbtValue(NbtByte)
object NbtByte extends NbtTag(1)

case class NbtShort(value: Short) extends NbtValue(NbtShort)
object NbtShort extends NbtTag(2)

case class NbtInt(value: Int) extends NbtValue(NbtInt)
object NbtInt extends NbtTag(3)

case class NbtLong(value: Long) extends NbtValue(NbtLong)
object NbtLong extends NbtTag(4)

case class NbtFloat(value: Float) extends NbtValue(NbtFloat)
object NbtFloat extends NbtTag(5)

case class NbtDouble(value: Double) extends NbtValue(NbtDouble)
object NbtDouble extends NbtTag(6)

case class NbtString(value: String) extends NbtValue(NbtString)
object NbtString extends NbtTag(8)
