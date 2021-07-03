package tel.schich.webnbt

import java.nio.ByteBuffer

object RegionParser {

    val SectorSize = 4096
    val ChunkLocationSector = 0
    val ChunkTimestampSector = 1
    val ChunkDataSectorOffset = 2


    def parse(data: ByteBuffer): Seq[Chunk] = {

        for {
            z <- 0 until 32
            x <- 0 until 32
            i = z * 32 + x
        } yield {
            val (sectorOffset, _) = readChunkLocation(data, ChunkLocationSector * SectorSize + 4 * i)
            val timestamp = data.getInt(ChunkTimestampSector * SectorSize + 4 * i)
            if (sectorOffset == 0 && timestamp == 0) MissingChunk(x, z)
            else {
                val byteOffset = SectorSize * sectorOffset
                val size = data.getInt(byteOffset)
                val compressionMode = data.get(byteOffset + 4) & 0xFF match {
                    case 1 => CompressionMode.GZip
                    case 2 => CompressionMode.Zlib
                    case 3 => CompressionMode.Uncompressed
                    case i => CompressionMode.Unknown(i)
                }
                ReadableChunk(x, z, byteOffset + 5, size, compressionMode, timestamp & 0xFFFFFFFFL)
            }
        }
    }

  private def readChunkLocation(buf: ByteBuffer, offset: Int): (Int, Int) = {
      val a = buf.get(offset) & 0xFF
      val b = buf.get(offset + 1).toInt & 0xFF
      val c = buf.get(offset + 2).toInt & 0xFF
      val sectorOffset = ((a << 16) | (b << 8)) | c
      val sectorCount = buf.get(offset + 3)

      (sectorOffset, sectorCount)
  }

    sealed trait Chunk
    case class ReadableChunk(x: Int, z: Int, byteOffset: Int, size: Int, compressionMode: CompressionMode, timestamp: Long) extends Chunk
    case class MissingChunk(x: Int, z: Int) extends Chunk

    sealed trait CompressionMode
    object CompressionMode {
        case object GZip extends CompressionMode
        case object Zlib extends CompressionMode
        case object Uncompressed extends CompressionMode
        case class Unknown(id: Int) extends CompressionMode
    }

}
