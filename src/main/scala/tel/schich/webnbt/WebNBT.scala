package tel.schich.webnbt

import org.scalajs.dom.{document, window}
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

@JSExport
object WebNBT extends JSApp {
  override def main(): Unit = {
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
        reader.readAsArrayBuffer(files(0))
        reader.onload = { _ =>
          val fileContent = reader.result.asInstanceOf[ArrayBuffer]
          val bytes = new Uint8Array(fileContent, 0, fileContent.byteLength)
          val decompressed =
            if (bytes(0) == 0x1F && bytes(1) == 0x8B) PakoFacade.ungzip(bytes)
            else bytes

          val buf = Array.ofDim[Byte](decompressed.byteLength)
          for (i <- 0 until decompressed.byteLength) {
            buf(i) = decompressed(i).toByte
          }

          NbtParser.parse(buf) match {
            case Some(tree) =>
              val json = new JsonNbtRenderer()
              output.value = json.render(tree)
            case None =>
              window.alert("Failed to parse the file as NBT.")
          }
        }
      } else {
        window.alert("Please drop one NBT file (compressed or uncompressed, big endian).")
      }
    })
  }
}
