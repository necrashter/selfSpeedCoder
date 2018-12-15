package com.speedcoder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by ilker on 09.08.2018.
 */
public class MainMenuScreen implements Screen {
    final CodeSpeed codeSpeed;
    private final TextField textField;
    Stage stage;

    public MainMenuScreen(final CodeSpeed codeSpeed) {
        this.codeSpeed = codeSpeed;

        int width= Gdx.graphics.getWidth(), height= Gdx.graphics.getHeight();

        stage=new Stage(new StretchViewport(width,height));
        Gdx.input.setInputProcessor(stage);

        Table table= new Table();

        table.setFillParent(true);
        table.align(Align.topLeft);
        table.padLeft(10);
        table.padTop(32);

        Label caption = new Label("SELF CODE TYPE SPEED",codeSpeed.skin,"caption");
        table.add(caption).align(Align.topLeft).padBottom(64).row();

        Label label = new Label("Please select an option:",codeSpeed.skin);
        table.add(label).left().row();

        final TextButton start = new TextButton("1. [Start the game]",codeSpeed.skin);
        start.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                codeSpeed.setScreen(new GameScreen(codeSpeed));
                MainMenuScreen.this.dispose();
            }
        });
        table.add(start).left().row();

        TextButton exit = new TextButton("2. [Exit]",codeSpeed.skin);
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exit).left().row();

        final TextButton[] options= new TextButton[]{start,exit};
        //stage.setDebugAll(true);

        textField = new TextField("",codeSpeed.skin);
        textField.setPosition(0,0);
        textField.setWidth(Gdx.graphics.getWidth());
        textField.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                String s = textField.getText();
                if(keycode == Input.Keys.ENTER){
                    for(int i=0; i<options.length; ++i){
                        if(s.startsWith(Integer.toString(i+1))){
                            InputEvent e = new InputEvent();
                            e.setType(InputEvent.Type.touchDown);
                            options[i].fire(e);
                            e.setType(InputEvent.Type.touchUp);
                            options[i].fire(e);
                        }
                    }
                }else {
                    for(int i=0; i<options.length; ++i){
                        if(s.startsWith(Integer.toString(i+1))){
                            InputEvent e = new InputEvent();
                            e.setType(InputEvent.Type.enter);
                            e.setPointer(-1);
                            options[i].fire(e);
                        } else if(options[i].isOver()){
                            InputEvent e = new InputEvent();
                            e.setType(InputEvent.Type.exit);
                            e.setPointer(-1);
                            options[i].fire(e);
                        }
                    }
                }
                return super.keyDown(event, keycode);
            }
        });

        stage.addActor(textField);
        stage.setKeyboardFocus(textField);


        stage.addActor(table);

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
    }

    @Override
    public void resize(int widtho, int heighto) {
        int width=widtho,height=heighto;
        if(heighto>600){width=(600*widtho)/heighto;height=600;}
        textField.setWidth(width);
        stage.getViewport().setWorldSize(width,height);
        stage.getViewport().setScreenSize(width,height);
        stage.getViewport().update(widtho,heighto,true);
        codeSpeed.shaderController.resize(width,height);
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
