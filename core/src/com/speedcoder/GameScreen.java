package com.speedcoder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by ilker on 09.08.2018.
 */
public class GameScreen implements Screen {
    final CodeSpeed codeSpeed;
    private final TextField textField;
    Stage stage;

    private Label[] labels;
    private Label meter;
    private Label nextCodeBar;

    private final int bufferSize = 8;

    private GameLogic gameLogic;

    private long lastAdded;
    private long lastEntered;
    private long gameStarted = 0;

    private boolean isGameOver = false;
    private boolean isGameStarted = false;

    private String lastText = "";

    public GameScreen(final CodeSpeed codeSpeed) {
        this.codeSpeed = codeSpeed;

        int width= Gdx.graphics.getWidth(), height= Gdx.graphics.getHeight();

        stage=new Stage(new StretchViewport(width,height));
        Gdx.input.setInputProcessor(stage);

        gameLogic = new GameLogic(codeSpeed.codesFile);

        Table table= new Table();

        table.setFillParent(true);
        table.align(Align.topLeft);
        table.padLeft(10);
        table.padTop(32);

        labels=new Label[bufferSize];
        for(int i =0; i<bufferSize; ++i){
            labels[i]=new Label("//Text will appear here",codeSpeed.skin);
            labels[i].setAlignment(Align.left);
            table.add(new Label(String.format("%2d.",i+1),codeSpeed.skin, "lightWhite"));
            table.add(labels[i]).left().row();
        }

        Table childTable = new Table();
        childTable.setFillParent(true);
        childTable.align(Align.topLeft);
        childTable.padTop(272);
        childTable.padLeft(10);

        meter = new Label("Welcome",codeSpeed.skin);
        meter.setAlignment(Align.left);
        childTable.add(meter).left().row();

        nextCodeBar = new Label("Game will start when you type \"start\" and hit enter.",codeSpeed.skin);
        childTable.add(nextCodeBar).left();


        textField = new TextField("",codeSpeed.skin);
        textField.setPosition(0,0);
        textField.setWidth(Gdx.graphics.getWidth());

        textField.addListener(new InputListener(){

            @Override
            public boolean keyUp(InputEvent event, int keycode){
                String newText = textField.getText();
                if(keycode == Input.Keys.BACKSPACE){
                    gameLogic.backspaced(Utils.difference(newText,lastText));
                }
                lastText = newText;
                return super.keyUp(event,keycode);
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if((int)character<32 || (int)character==127)return super.keyTyped(event, character);
                ++gameLogic.keysTyped;
                return super.keyTyped(event, character);
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    submitText(textField.getText());
                    textField.setText("");
                }
                return super.keyDown(event, keycode);
            }
        });

        stage.addActor(textField);
        stage.setKeyboardFocus(textField);

        stage.addActor(table);
        stage.addActor(childTable);

        //stage.setDebugAll(true);

        stage.getBatch().setShader(codeSpeed.shader);
    }


    private void submitText(String text) {
        if(!isGameStarted && text.equals("start")){
            startGame();
            return;
        }
        for(Label l :labels){
            String s = l.getText().toString();
            if(s.length()==0 || s.charAt(0)== '/')continue;
            if(s.replaceAll(" ","").equals(text.replaceAll(" ",""))){
                l.setText("");
                //double charsPerMin = 60000*s.length()/(double)(TimeUtils.millis()-lastEntered);
                //meter.setText("CPM: "+ charsPerMin);
                lastEntered=TimeUtils.millis();
                ++gameLogic.correctLines;
                checkIfEmpty();
                return;
            }
        }
        if(text.startsWith("exit") || text.equals("Gdx.app.exit();"))gameOver();
        else ++gameLogic.wrongLines;
    }

    private void startGame() {
        for(Label l: labels)l.setText("");
        lastAdded = TimeUtils.millis();
        lastEntered = lastAdded;
        addNewCode();
        gameStarted = TimeUtils.millis();
        isGameStarted = true;
    }

    private void checkIfEmpty(){
        for(Label l: labels){
            String s = l.getText().toString();
            if(!(s.length()==0 || s.charAt(0)== '/'))return;
        }
        addNewCode();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        System.out.println(Gdx.graphics.getFramesPerSecond());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);

        codeSpeed.shaderController.drawShader(stage);
        //stage.draw();

        if(!isGameOver && isGameStarted) {
            meter.setText(String.format("CPM: %.2f",60000.0f*gameLogic.keysTyped/(double)(TimeUtils.millis() - gameStarted)));
            
            int elapsedTime = (int) (TimeUtils.millis() - lastAdded);
            nextCodeBar.setText(String.format("Next Code: %.2f%%", 100 * elapsedTime / (float) gameLogic.getDelay()));
            if (elapsedTime > gameLogic.getDelay()) {
                addNewCode();
            }
        }
    }

    private void addNewCode() {
        for(Label l : labels){
            if(l.getText().length()==0 || l.getText().charAt(0) == '/'){
                l.setText(gameLogic.getRandomCode());
                lastAdded = TimeUtils.millis();
                return;
            }
        }
        gameOver();
        nextCodeBar.setText("Game Over.");
    }

    private void gameOver() {
        isGameOver = true;
        gameLogic.elapsedTime = TimeUtils.millis() - gameStarted;
        stage.addAction(Actions.sequence(Actions.fadeOut(1f), new RunnableAction(){
            @Override
            public void run() {
                codeSpeed.setScreen(new GameOverScreen(codeSpeed, gameLogic));
                GameScreen.this.dispose();
            }
        }));
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
