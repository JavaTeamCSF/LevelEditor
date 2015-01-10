package ru.vsu.csf.mvc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import javafx.scene.input.MouseButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.peer.MouseInfoPeer;
import java.io.*;
import java.util.HashMap;

public class LevelEditor extends ApplicationAdapter {

    /*private static final int MARGIN_BOTTOM = 50;
    private static final int MARGIN_LEFT = 100;*/ /** - если с полями*/
    private static final int CELL_SIZE = 16;
    private boolean first;      //флаг начала dragging, при первом вызове - true, затем - false, чтобы установить добавление/удаление стены в зависимости от первого выбранного сектора
    private boolean del;        //флаг, устанавливающий удаление/добавление стены
    private Point firstPoint;   //координаты точки начала dragging
    private boolean lmb;        //флаг на нажатую левую кнопку мыши

    SpriteBatch batch;

    Level level;
    HashMap<CellType, TextureRegion> textures;

	@Override
	public void create () {
		batch = new SpriteBatch();
        level = new Level();
        first = true;
        firstPoint = new Point();
        lmb = false;
        textures = new HashMap<CellType, TextureRegion>() {{
            put(CellType.FREE, new TextureRegion(new Texture(Gdx.files.internal("floor.png"))));
            put(CellType.WALL, new TextureRegion(new Texture(Gdx.files.internal("wall.png"))));
            put(CellType.DOOR, new TextureRegion(new Texture(Gdx.files.internal("door_ver.png"))));
        }
        };
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                firstPoint.setLocation(screenX, screenY);
                screenY = Gdx.graphics.getHeight() - screenY;


                /*screenX -= MARGIN_LEFT;
                screenY -= MARGIN_BOTTOM;*/ /** - если с полями*/

                screenX /= CELL_SIZE;
                screenY /= CELL_SIZE;

                if (button == MouseEvent.NOBUTTON) { //если левая кнопка мыши
                    lmb = true;
                    level.createOrDeleteWall(screenX, screenY); //рисуем/удаляем стену
                }
                if (button == MouseEvent.BUTTON1) { //если правая кнопка мыши
                    lmb = false;
                    level.createOrDeleteDoor(screenX, screenY); //рисуем/удаляем дверь
                }
                if (button == MouseEvent.BUTTON2)
                    lmb = false;

                return true;
            }


            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer ){

                /**проверка на выход за пределы первой выбранной секции, чтобы избежать конфликта с touchDown, и на нажатую левую кнопку мыши*/
                if (((Math.abs(screenX - firstPoint.x) > CELL_SIZE)||(Math.abs(screenY - firstPoint.y) > CELL_SIZE))&&lmb) {

                    screenY = Gdx.graphics.getHeight() - screenY;


                    screenX /= CELL_SIZE;
                    screenY /= CELL_SIZE;

                    if (first) {
                        first = false;
                        if (level.data[screenY][screenX] == CellType.WALL) del = true;
                    }

                    if (del) {
                        level.deleteWall(screenX, screenY);
                    } else {
                        level.createWall(screenX, screenY);
                    }
                }
                return true;
            }

            @Override
            public boolean touchUp (int screenX, int screenY, int pointer, int button){
                first = true;
                del = false;

                return true;
            }

            @Override
            public boolean keyTyped(char character) {

                /**сохранение уровня в файл*/
                if ((character == 's')||(character == 'ы')) {
                    try {
                        File f = new File("level_1.txt");
                        PrintWriter out = new PrintWriter(f.getAbsoluteFile());

                        for (int i = level.data.length - 1; i >= 0; i--) {
                            System.out.print(i + " - ");
                            for (int j = 0; j < level.data[0].length; j++) {
                                System.out.print(level.data[i][j].ordinal());
                                out.print(level.data[i][j].ordinal());
                            }
                            System.out.println();
                            out.print("\n");
                        }

                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                /**загрузка уровня из файла*/
                if ((character == 'f')||(character == 'а')) {
                    BufferedReader reader = null;
                    try {
                        File file = new File("level_1.txt");
                        reader = new BufferedReader(new FileReader(file));

                        String[] elements = new String[40];
                        int temp;
                        for (int i = elements.length - 1; i >= 0; i--) {
                            elements[i] = reader.readLine();
                            for (int j = 0; j < 40; j++) {
                                temp = Character.getNumericValue(elements[i].charAt(j));
                                System.out.print(temp);
                                switch (temp) {
                                    case 0:
                                        level.data[i][j] = CellType.FREE; //если элемент матрицы = 0, то сектор - пустой
                                        break;
                                    case 1:
                                        level.data[i][j] = CellType.WALL; //если элемент матрицы = 1, то сектор - стена
                                        break;
                                    case 2:
                                        level.data[i][j] = CellType.DOOR; //если элемент матрицы = 2, то сектор - дверь (выход)
                                        break;
                                    default:
                                        level.data[i][j] = CellType.FREE;
                                        break;
                                }
                            }
                            System.out.print("\n");
                        }
                    }
                    catch (IOException e) {e.printStackTrace();}
                    finally {
                        assert reader != null;
                        try {
                            reader.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }



                return true;
            }
        });
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

        for (int i = 0; i < level.data.length; i++)
            for (int j = 0; j < level.data[0].length; j++) {
                /*batch.draw(textures.get(level.data[i][j]),
                        MARGIN_LEFT + j*CELL_SIZE,
                        MARGIN_BOTTOM + i*CELL_SIZE,
                        CELL_SIZE, CELL_SIZE);*/ /** - если с полями*/
                batch.draw(textures.get(level.data[i][j]),
                        j*CELL_SIZE,
                        i*CELL_SIZE,
                        CELL_SIZE, CELL_SIZE);
            }

        batch.end();
	}
}
