package com.speedcoder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;

public class ShaderController {
    private FrameBuffer downFrameBuffer, frameBuffer, frameBuffer1;
    private TextureRegion downFrameBufferRegion, frameBufferRegion , frameBufferRegion1;
    private static CodeSpeed codeSpeed;

    private Texture emptyTexture;

    private long start;

    private float alphaMultiplier = .5f;

    private int width,height;
    public ShaderController(CodeSpeed codeSpeed){
        width=Gdx.graphics.getWidth(); height=Gdx.graphics.getHeight();
        this.codeSpeed=codeSpeed;
        downFrameBuffer = new FrameBuffer(Pixmap.Format.RGB565, (width/2), (height/2), false);
        downFrameBufferRegion = new TextureRegion(downFrameBuffer.getColorBufferTexture());
        downFrameBufferRegion.flip(false, true);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, (width), (height), false);
        frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        frameBufferRegion.flip(false, true);

        frameBuffer1 = new FrameBuffer(Pixmap.Format.RGB565, (width), (height), false);
        frameBufferRegion1 = new TextureRegion(frameBuffer1.getColorBufferTexture());
        frameBufferRegion1.flip(false, true);

        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        emptyTexture = new Texture(pixmap);

        start=TimeUtils.millis();

        codeSpeed.batch.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_DST_ALPHA);
    }

    public void drawShader(Stage stage){
        alphaMultiplier =(float) (.1f*Math.sin((Math.PI * TimeUtils.millis() /1500f)) + .1f);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        codeSpeed.batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        frameBuffer1.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        codeSpeed.batch.setShader(codeSpeed.noiseShader);
        codeSpeed.batch.begin();
        codeSpeed.noiseShader.setUniformf("u_time",((TimeUtils.millis()-start)/1000f));
        codeSpeed.batch.draw(emptyTexture,0,0,width,height);
        codeSpeed.batch.end();
        stage.draw();
        frameBuffer1.end();

        codeSpeed.batch.setShader(null);
        codeSpeed.batch.begin();
        codeSpeed.batch.draw(frameBufferRegion1,0,0,width,height);
        codeSpeed.batch.end();

        downFrameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        codeSpeed.batch.setShader(codeSpeed.brightPassShader);
        codeSpeed.batch.begin();
        codeSpeed.batch.draw(frameBufferRegion1,0,0,width,height);
        codeSpeed.batch.end();
        downFrameBuffer.end();

        codeSpeed.batch.setShader(codeSpeed.shader);

        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        codeSpeed.batch.begin();
        codeSpeed.shader.setUniformi("horizontal",0);
        codeSpeed.shader.setUniformf("alpha",1f);
        codeSpeed.batch.draw(downFrameBufferRegion,0,0,width,height);
        codeSpeed.batch.end();
        frameBuffer.end();

        codeSpeed.batch.begin();
        //codeSpeed.batch.setBlendFunction(Gdx.gl20.GL_DST_COLOR, Gdx.gl20.GL_ONE);
        codeSpeed.shader.setUniformi("horizontal",1);
        codeSpeed.shader.setUniformf("alpha", alphaMultiplier);
        codeSpeed.batch.draw(frameBufferRegion,0,0,width,height);
        codeSpeed.batch.end();


        //codeSpeed.batch.setShader(null);
        //codeSpeed.batch.begin();
        //codeSpeed.batch.setBlendFunction(Gdx.gl20.GL_DST_COLOR, Gdx.gl20.GL_ONE);
        //codeSpeed.batch.draw(frameBufferRegion,0,0);
        //codeSpeed.batch.end();

        codeSpeed.batch.setShader(null);
        stage.getBatch().setShader(null);
    }

    public void resize(int width, int height) {
        System.out.println(String.format("Resized %d,%d",width,height));
        this.width = width; this.height= height;
        codeSpeed.shader.begin();
        codeSpeed.shader.setUniformf("u_resolution",(float)width,(float)height);
        codeSpeed.shader.end();
        codeSpeed.noiseShader.begin();
        codeSpeed.noiseShader.setUniformf("u_resolution",(float)width,(float)height);
        codeSpeed.noiseShader.end();

        downFrameBuffer = new FrameBuffer(Pixmap.Format.RGB565, (width), (height), false);
        downFrameBufferRegion = new TextureRegion(downFrameBuffer.getColorBufferTexture());
        downFrameBufferRegion.flip(false, true);
        
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, (width), (height), false);
        frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        frameBufferRegion.flip(false, true);

        frameBuffer1 = new FrameBuffer(Pixmap.Format.RGB565, (width), (height), false);
        frameBufferRegion1 = new TextureRegion(frameBuffer1.getColorBufferTexture());
        frameBufferRegion1.flip(false, true);
    }
}
