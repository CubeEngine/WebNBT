package tel.schich.webnbt
import scala.scalajs.js.annotation.{JSImport, JSName}
import scala.scalajs.js.typedarray.Uint8Array
import scalajs.js

/**
  * Created by phillip on 12.02.17.
  */
@js.native
@JSImport("pako", JSImport.Namespace)
object PakoFacade extends js.Object {
  def ungzip(buf: Uint8Array): Uint8Array = js.native
}
