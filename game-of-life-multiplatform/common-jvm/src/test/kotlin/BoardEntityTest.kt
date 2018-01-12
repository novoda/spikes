import com.novoda.gol.data.PositionEntity
import com.novoda.gol.data.ListBasedMatrix
import com.novoda.gol.data.SimulationBoardEntity
import org.junit.Test
import kotlin.test.assertEquals

class BoardEntityTest {

    /*
        00000        00000
        00X00        00000
        00X00   -->  0XXX0
        00X00        00000
        00000        00000
     */
    @Test
    fun testBlinkerToHorizontal() {
        val originCellMatrix = ListBasedMatrix(width = 5, height = 5, seeds = listOf(
                PositionEntity(2, 1),
                PositionEntity(2, 2),
                PositionEntity(2, 3)))
        val originBoard = SimulationBoardEntity(originCellMatrix)

        val expectedCellMatrix = ListBasedMatrix(width = 5, height = 5, seeds = listOf(
                PositionEntity(1, 2),
                PositionEntity(2, 2),
                PositionEntity(3, 2)))
        val expectedBoard = SimulationBoardEntity(expectedCellMatrix)

        val nextIteration = originBoard.nextIteration()

        assertEquals(expectedBoard, nextIteration)
    }

    /*
        00000        00000
        00000        00X00
        0XXX0   -->  00X00
        00000        00X00
        00000        00000
     */
    @Test
    fun testBlinkerToVertical() {
        val originConfiguration = ListBasedMatrix(width = 5, height = 5, seeds = listOf(
                PositionEntity(1, 2),
                PositionEntity(2, 2),
                PositionEntity(3, 2)))
        val originBoard = SimulationBoardEntity(originConfiguration)

        val expectedConfiguration = ListBasedMatrix(width = 5, height = 5, seeds = listOf(
                PositionEntity(2, 1),
                PositionEntity(2, 2),
                PositionEntity(2, 3)))
        val expectedBoard = SimulationBoardEntity(expectedConfiguration)

        val nextIteration = originBoard.nextIteration()

        assertEquals(expectedBoard, nextIteration)
    }
}
