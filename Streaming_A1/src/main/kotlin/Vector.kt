class Vector(val x: Int = 0, val y: Int = 0, val z: Int = 0) {

    fun add(other: Vector): Vector {
        val newX = this.x + other.x
        val newY = this.y + other.y
        val newZ = this.z + other.z
        return Vector(newX, newY, newZ)
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}