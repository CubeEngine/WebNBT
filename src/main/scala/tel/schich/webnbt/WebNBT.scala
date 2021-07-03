package tel.schich.webnbt

import org.scalajs.dom
import org.scalajs.dom.{document, html, window}
import tel.schich.webnbt.RegionParser.{CompressionMode, MissingChunk, ReadableChunk}

import java.nio.{ByteBuffer, ByteOrder}
import scala.scalajs.js.typedarray.{ArrayBuffer, Int8Array, TA2AB, Uint8Array}

object WebNBT {

  private val RegionFilePattern = ".*\\.mc[ar]".r

  def main(args: Array[String]): Unit = {
    window.addEventListener("DOMContentLoaded", onReady)
  }

  private def onReady(e: dom.Event): Unit = {
    println("ready")
    val input = document.querySelector(".input").asInstanceOf[html.Element]
    val output = document.querySelector(".output").asInstanceOf[html.TextArea]

    def dragEnd(e: dom.DragEvent): Unit = {
      input.className = input.className.replaceAll("targeted", "").trim
    }

    def dragBegin(e: dom.DragEvent): Unit = {
      input.className += " targeted"
    }

    document.addEventListener("dragenter", dragBegin)
    document.addEventListener("dragleave", dragEnd)
    document.addEventListener("drop", dragEnd)

    input.addEventListener("dragover", { e: dom.DragEvent =>
      e.preventDefault()
    })
    input.addEventListener("drop", { e: dom.DragEvent =>
      e.preventDefault()
      val files = e.dataTransfer.files
      if (files.length == 1) {
        val reader = new dom.FileReader()
        val file = files(0)
        reader.readAsArrayBuffer(file)
        reader.onload = { _ =>
          val fileContent = reader.result.asInstanceOf[ArrayBuffer]
          val bytes = new Uint8Array(fileContent, 0, fileContent.byteLength)

          val data: NbtCompound = file.name match {
            case RegionFilePattern() =>
              val buf = toBuf(bytes)
              val chunks = RegionParser.parse(buf)
              NbtCompound(chunks.flatMap {
                case ReadableChunk(x, z, byteOffset, size, compressionMode, _) =>
                  val name = s"x=$x,z=$z"
                  compressionMode match {
                    case CompressionMode.GZip =>
                      NbtParser.parse(toBuf(PakoFacade.inflate(bytes.subarray(byteOffset, byteOffset + size))))
                        .map(data => (name, data))
                    case CompressionMode.Zlib =>
                      NbtParser.parse(toBuf(PakoFacade.inflateRaw(bytes.subarray(byteOffset, byteOffset + size))))
                        .map(data => (name, data))
                    case CompressionMode.Uncompressed =>
                      NbtParser.parse(buf.position(byteOffset).limit(size))
                        .map(data => (name, data))
                    case CompressionMode.Unknown(i) =>
                      Some((name, NbtString(s"Unknown compression: ${i}")))
                  }
                case MissingChunk(_, _) => None
              }.toMap)
            case _ =>
              val buf =
                if (bytes(0) == 0x1F && bytes(1) == 0x8B) toBuf(PakoFacade.inflate(bytes))
                else toBuf(bytes)
              NbtParser.parse(buf).getOrElse(NbtCompound(Map("error" -> NbtString("Failed to parse NBT!"))))
          }

          val json = new JsonNbtRenderer()
          output.value = json.render(data)
        }
      } else {
        window.alert("Please drop one NBT file (compressed or uncompressed, big endian).")
      }
    })
  }

  private def toBuf(buf: Uint8Array) =
    ByteBuffer.wrap(new Int8Array(buf.buffer).toArray).order(ByteOrder.BIG_ENDIAN)
}
