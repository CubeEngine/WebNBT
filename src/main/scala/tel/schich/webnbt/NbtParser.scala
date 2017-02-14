package tel.schich.webnbt

import java.nio.ByteOrder.BIG_ENDIAN
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.charset.StandardCharsets

import scala.annotation.tailrec
import scala.scalajs.js.annotation.JSExport

@JSExport
object NbtParser {
  def parse(data: Array[Byte]): Option[NbtCompound] = {
    getValue(ByteBuffer.wrap(data).order(BIG_ENDIAN), NbtCompound.id) match {
      case c: NbtCompound => Some(c)
      case _ => None
    }
  }

  private def getValue(data: ByteBuffer, tag: Byte): NbtValue = {
    tag match {
      case NbtCompound.id =>
        NbtCompound(getCompound(data, Map.empty))
      case NbtList.id =>
        val tag = data.get()
        val len = data.getInt()
        val buf = Array.ofDim[NbtValue](len)
        for (i <- 0 until len) {
          buf(i) = getValue(data, tag)
        }
        NbtList(tag, buf)
      case NbtByteArray.id =>
        val len = data.getInt()
        val buf = Array.ofDim[Byte](len)
        NbtByteArray(buf)
      case NbtIntArray.id =>
        val len = data.getInt()
        val buf = Array.ofDim[Int](len)
        for (i <- 0 until len) {
          buf(i) = data.getInt()
        }
        NbtIntArray(buf)
      case NbtByte.id =>
        NbtByte(data.get())
      case NbtShort.id =>
        NbtShort(data.getShort())
      case NbtInt.id =>
        NbtInt(data.getInt)
      case NbtLong.id =>
        NbtLong(data.getLong())
      case NbtFloat.id =>
        NbtFloat(data.getFloat)
      case NbtDouble.id =>
        NbtDouble(data.getDouble())
      case NbtString.id =>
        NbtString(getString(data))
    }
  }

  @tailrec
  private def getCompound(data: ByteBuffer, compound: Map[String, NbtValue]): Map[String, NbtValue] = {
    if (!data.hasRemaining) compound
    else {
      val tag = data.get()
      if (tag == NbtCompound.end) compound
      else {
        val name = getString(data)
        val value = getValue(data, tag)
        getCompound(data, compound + (name -> value))
      }
    }
  }

  private def getString(data: ByteBuffer): String = {
    val len = data.getShort()
    val buf = Array.ofDim[Byte](len)
    data.get(buf)
    new String(buf, StandardCharsets.UTF_8)
  }

}
