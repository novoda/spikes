package com.novoda.gol

actual class View {

    actual fun render(boardEntity: BoardEntity) {
        for (y in 0 until boardEntity.getHeight()) {
            for (x in 0 until boardEntity.getWidth()) {
                val cellAtPosition = boardEntity.cellAtPosition(x, y)
                if (cellAtPosition.isAlive) {
                    print("X")
                } else {
                    print(" ")
                }

            }
            print("\n")
        }

        print("exit: Stop Simulation\n")
        print("next: Next Iteration\n")
    }
}