package com.novoda.gol.data

data class ListBasedMatrix(private val width: Int, private val height: Int, private val seeds: List<PositionEntity> = listOf()) : CellMatrix {

    private val matrix: List<CellEntity>

    init {
        val width = width
        val height = height
        val cellEntities: MutableList<CellEntity> = MutableList(width * height, { CellEntity(false) })

        for (seed in seeds) {
            cellEntities[width * seed.y + seed.x] = CellEntity(true)
        }

        matrix = cellEntities
    }

    override fun cellAtPosition(x: Int, y: Int) = matrix[y * width + x]

    override fun getWidth() = width

    override fun getHeight() = height

    override fun getSeeds() = seeds

}
