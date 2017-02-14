package tel.schich.webnbt

trait NbtRenderer[T] {
  def render(compound: NbtCompound): T
}
