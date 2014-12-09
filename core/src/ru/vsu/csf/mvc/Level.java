package ru.vsu.csf.mvc;

public class Level {

    public static final int SIZE = 40;
    public static final int DOOR_COUNT = 1;
    private int count_door;

    public CellType[][] data;

    public Level() {
        data = new CellType[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++) {
                data[i][j] = CellType.FREE;
            }
        count_door = 0;
    }

    public void createOrDeleteWall(int x, int y) {
        if (data[y][x] != CellType.WALL) {
            if (data[y][x] == CellType.DOOR) count_door--;
            data[y][x] = CellType.WALL;
        }
        else {
            data[y][x] = CellType.FREE;
        }
    }

    public void createWall(int x, int y) {
        if (data[y][x] == CellType.DOOR) count_door--;
        data[y][x] = CellType.WALL;
    }

    public void deleteWall(int x, int y) {
        data[y][x] = CellType.FREE;
    }

    public void createOrDeleteDoor(int x, int y) {
        if ((data[y][x] != CellType.DOOR)&&(count_door < DOOR_COUNT)) {
            data[y][x] = CellType.DOOR;
            count_door++;
        }
        else if ((data[y][x] == CellType.DOOR)&&(count_door == DOOR_COUNT)) {
            data[y][x] = CellType.FREE;
            count_door--;
        }
    }
}
