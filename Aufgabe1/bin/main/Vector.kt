class Vector(private val x: Int = 0, private val y: Int = 0, private val z: Int = 0) {

    fun add(other: Vector): Vector {
        val newX = this.x + other.x
        val newY = this.y + other.y
        val newZ = this.z + other.z
        return Vector(newX, newY, newZ)
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    override fun equals(other: Any?): Boolean {
        return other is Vector && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    fun copy(): Vector {
        return Vector(x, y, z)
    }
}