package dev.cappee.treble.model

data class Data(
    val title: Int,
    val descriptions: Array<Int>,
    val values: Array<String>,
    val button: Pair<Int, Int>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (!descriptions.contentEquals(other.descriptions)) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = descriptions.contentHashCode()
        result = 31 * result + values.contentHashCode()
        return result
    }
}
