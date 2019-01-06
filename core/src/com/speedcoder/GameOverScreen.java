package com.speedcoder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.LinkedHashMap;

/**
 * Created by ilker on 16.08.2018.
 */
public class GameOverScreen implements Screen {
    final CodeSpeed codeSpeed;
    private final TextField textField;
    Stage stage;

    private int menu = 0;
    private boolean downPressed=false,upPressed=false;
    private boolean downScheduled=false, upScheduled=false;
    private Table statTable;

    public GameOverScreen(final CodeSpeed codeSpeed,GameLogic gameLogic) {
        this.codeSpeed = codeSpeed;

        int width= Gdx.graphics.getWidth(), height= Gdx.graphics.getHeight();

        stage=new Stage(new StretchViewport(width,height));
        Gdx.input.setInputProcessor(stage);

        Table table= new Table();

        table.setFillParent(true);
        table.align(Align.topLeft);
        table.padLeft(10);
        table.padTop(32);

        statTable = new Table();
        statTable.setFillParent(true);
        statTable.align(Align.topLeft);
        statTable.padLeft(10);
        statTable.padTop(32);
        statTable.padBottom(64);

        Label caption = new Label("GAME ANALYSIS",codeSpeed.skin,"caption");
        table.add(caption).align(Align.topLeft).padBottom(64).row();

        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("Time Elapsed: %.2f seconds\n", gameLogic.elapsedTime/1000.0f));
        analysis.append(String.format("Lines Entered: %d wrong, %d correct, %d total, %.2f error ratio\n", gameLogic.wrongLines,
                gameLogic.correctLines, gameLogic.correctLines + gameLogic.wrongLines, gameLogic.wrongLines / (float)(gameLogic.wrongLines+ gameLogic.correctLines)));
        analysis.append(String.format("Chars typed: %d\n",gameLogic.keysTyped));
        int backspaces = gameLogic.getBackspaceCount();
        analysis.append(String.format("Backspace count: %d\n",backspaces));

        Label analysisLabel = new Label(analysis.toString(),codeSpeed.skin,"lightWhite");
        table.add(analysisLabel).left().row();
        analysis.append(String.format("\tBackspaced char ratio: %.2f\n", backspaces/(float)gameLogic.keysTyped));
        analysis.append("Most Backspaced Characters:\n");
        LinkedHashMap<Character,Integer> mostBackspaced = gameLogic.sortBackspacedChars();
        for(char c:mostBackspaced.keySet()){
            analysis.append(String.format("\t'%c', %d times\n",c,mostBackspaced.get(c)));
        }


        Label statsLabel = new Label(analysis.toString(),codeSpeed.skin,"lightWhite");
        ScrollPane scrollPane = new ScrollPane(statsLabel,codeSpeed.skin);
        statTable.add(scrollPane).left().row();
        Timer.Task scrollUpTask = new Timer.Task() {
            @Override
            public void run() {
                if(!upPressed){upScheduled=false;return;}
                InputEvent e = new InputEvent();
                e.setType(InputEvent.Type.scrolled);
                e.setScrollAmount(-1);
                scrollPane.fire(e);
                Timer.schedule(this,.1f);
            }
        };
        Timer.Task scrollDownTask = new Timer.Task() {
            @Override
            public void run() {
                if(!downPressed){downScheduled=false;return;}
                InputEvent e = new InputEvent();
                e.setType(InputEvent.Type.scrolled);
                e.setScrollAmount(1);
                scrollPane.fire(e);
                Timer.schedule(this,.1f);
            }
        };
        statTable.setX(99999f);

        Label label = new Label("Please select an option:",codeSpeed.skin);
        table.add(label).left().row();

        final TextButton start = new TextButton("1. [Restart]",codeSpeed.skin);
        start.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                codeSpeed.setScreen(new GameScreen(codeSpeed));
                GameOverScreen.this.dispose();
            }
        });
        table.add(start).left().row();

        TextButton stat = new TextButton("2. [Stats]",codeSpeed.skin);
        stat.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.addAction(Actions.moveBy(-table.getWidth(),0,0.4f,Interpolation.sine));
                statTable.setX(Gdx.graphics.getWidth());
                statTable.addAction(Actions.moveBy(-Gdx.graphics.getWidth(),0,0.4f,Interpolation.sine));
                menu = 1;
            }
        });
        table.add(stat).left().row();

        TextButton reverseStat = new TextButton("1. [Return]",codeSpeed.skin);
        reverseStat.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.addAction(Actions.moveBy(table.getWidth(),0,0.4f,Interpolation.sine));
                statTable.addAction(Actions.moveBy(Gdx.graphics.getWidth(),0,0.4f,Interpolation.sine));
                menu = 0;
            }
        });
        statTable.add(reverseStat).left().row();

        TextButton exit = new TextButton("3. [Exit]",codeSpeed.skin);
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exit).left().row();

        final TextButton[][] options= new TextButton[][]{{start,stat,exit},{reverseStat}};
        //stage.setDebugAll(true);

        textField = new TextField("",codeSpeed.skin);
        textField.setPosition(0,0);
        textField.setWidth(Gdx.graphics.getWidth());
        textField.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                String s = textField.getText();
                if(keycode == Input.Keys.ENTER){
                    for(int i=0; i<options[menu].length; ++i){
                        if(s.startsWith(Integer.toString(i+1))){
                            InputEvent e = new InputEvent();
                            e.setType(InputEvent.Type.exit);
                            e.setPointer(-1);
                            options[menu][i].fire(e);
                            e.setType(InputEvent.Type.touchDown);
                            options[menu][i].fire(e);
                            e.setType(InputEvent.Type.touchUp);
                            options[menu][i].fire(e);
                        }
                    }
                    textField.setText("");
                }else {
                    for(int i=0; i<options[menu].length; ++i) {
                        if (s.startsWith(Integer.toString(i + 1))) {
                            InputEvent e = new InputEvent();
                            e.setType(InputEvent.Type.enter);
                            e.setPointer(-1);
                            options[menu][i].fire(e);
                        } else if (options[menu][i].isOver()) {
                            InputEvent e = new InputEvent();
                            e.setType(InputEvent.Type.exit);
                            e.setPointer(-1);
                            options[menu][i].fire(e);
                        }
                    }
                }
                if(keycode == Input.Keys.UP)upPressed=false;
                else if (keycode == Input.Keys.DOWN)downPressed=false;
                return super.keyUp(event, keycode);
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(menu==1) {
                    if (keycode == Input.Keys.UP) {
                        InputEvent e = new InputEvent();
                        e.setType(InputEvent.Type.scrolled);
                        e.setScrollAmount(-1);
                        scrollPane.fire(e);
                        if(!upScheduled)Timer.schedule(scrollUpTask,0.6f);
                        upScheduled=true;
                        upPressed=true;
                    }else if (keycode == Input.Keys.DOWN) {
                        InputEvent e = new InputEvent();
                        e.setType(InputEvent.Type.scrolled);
                        e.setScrollAmount(1);
                        scrollPane.fire(e);
                        if(!downScheduled)Timer.schedule(scrollDownTask,0.6f);
                        downScheduled=true;
                        downPressed=true;
                    }
                }
                return super.keyDown(event, keycode);
            }
        });

        stage.addActor(textField);
        stage.setKeyboardFocus(textField);


        stage.addActor(table);
        stage.addActor(statTable);

        stage.getRoot().setColor(1,1,1,0);
        stage.addAction(Actions.fadeIn(1f));

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);

        codeSpeed.shaderController.drawShader(stage);

        //stage.draw();
    }

    @Override
    public void resize(int widtho, int heighto) {
        int width=widtho,height=heighto;
        if(heighto>600){width=(600*widtho)/heighto;height=600;}
        textField.setWidth(width);
        stage.getViewport().setWorldSize(width,height);
        stage.getViewport().setScreenSize(width,height);
        stage.getViewport().update(widtho,heighto,true);
        codeSpeed.shaderController.resize(width, height);

        statTable.setX(Gdx.graphics.getWidth());
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}