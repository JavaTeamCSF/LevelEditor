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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class LevelEditor extends ApplicationAdapter {

    /*private static final int MARGIN_BOTTOM = 50;
    private static final int MARGIN_LEFT = 100;*/ /** - если с полями*/
    private static final int CELL_SIZE = 15;
    private boolean first;
    private boolean del;

    SpriteBatch batch;

    Level level;
    HashMap<CellType, TextureRegion> textures;

	@Override
	public void create () {
		batch = new SpriteBatch();
        level = new Level();
        first = true;
        textures = new HashMap<CellType, TextureRegion>() {{
            put(CellType.FREE, new TextureRegion(new Texture(Gdx.files.internal("clear.png"))));
            put(CellType.WALL, new TextureRegion(new Texture(Gdx.files.internal("wall.png"))));
            put(CellType.DOOR, new TextureRegion(new Texture(Gdx.files.internal("door.png"))));
        }
        };
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                screenY = Gdx.graphics.getHeight() - screenY;

                /*screenX -= MARGIN_LEFT;
                screenY -= MARGIN_BOTTOM;*/ /** - если с полями*/

                screenX /= CELL_SIZE;
                screenY /= CELL_SIZE;

                if (button == MouseEvent.NOBUTTON) //если левая кнопка мыши
                    level.createOrDeleteWall(screenX, screenY); //рисуем/удаляем стену
                if (button == MouseEvent.BUTTON1) //если правая кнопка мыши
                    level.createOrDeleteDoor(screenX, screenY); //рисуем/удаляем дверь

                return true;
            }

            @Override
            public boolean touchDragged (int screenX, int screenY, int pointer){
                screenY = Gdx.graphics.getHeight() - screenY;


                screenX /= CELL_SIZE;
                screenY /= CELL_SIZE;

                if (first) {
                    first = false;
                    if (level.data[screenY][screenX] == CellType.WALL) del = true;
                }

                if (del) {
                    level.deleteWall(screenX, screenY);
                }
                else {
                    level.createWall(screenX, screenY);
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

                if ((character == 's')||(character == 'ы')) {
                    try {
                        BufferedWriter b = new BufferedWriter(new FileWriter(new File("level.mvcl")));

                        for (int i = level.data.length - 1; i >= 0; i--) {
                            System.out.print(i + " - ");
                            for (int j = 0; j < level.data[0].length; j++) {
                                System.out.print(level.data[i][j].ordinal());
                                b.write(level.data[i][j].ordinal());
                            }
                            System.out.println();
                        }

                        b.flush();
                        b.close();

                    } catch (IOException e) {
                        e.printStackTrace();
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
