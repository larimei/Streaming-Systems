import write.MovingItem

data class MovingItemImpl(
    private val name: String,
    private var location: Vector,
    private var value: Int
) : MovingItem {

    private var numberOfMoves = 0

    override fun getName(): String = name

    override fun getLocation(): Vector = location.copy()

    override fun getNumberOfMoves(): Int = numberOfMoves

    override fun getValue(): Int = value

    fun move(vector: Vector) {
        location = location.add(vector)
        numberOfMoves++
    }

    fun changeValue(newValue: Int) {
        value = newValue
    }
}
