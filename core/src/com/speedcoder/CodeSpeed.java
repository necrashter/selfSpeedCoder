package com.speedcoder;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CodeSpeed extends Game {
	public SpriteBatch batch;

	public Skin skin;

	public FileHandle codesFile;

	public ShaderProgram shader;
	public ShaderProgram brightPassShader;
	public ShaderProgram noiseShader;

	public ShaderController shaderController;

	@Override
	public void create () {
		batch = new SpriteBatch();
		batch.enableBlending();

		skin= new Skin(Gdx.files.internal("skin.json"));

		codesFile= Gdx.files.internal("codes");

		//FileHandle dictionary= Gdx.files.internal("dictionary.txt");
		//LanguageProcessing.readWords(dictionary.readString());

		String vertexShader = Gdx.files.internal("vertex1.glsl").readString();
		String fragmentShader = Gdx.files.internal("fragment1.glsl").readString();

		shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
		shader.pedantic = false;

		brightPassShader = new ShaderProgram(vertexShader, Gdx.files.internal("brightPassFragment.glsl").readString());
		brightPassShader.pedantic = false;

		noiseShader = new ShaderProgram(vertexShader, Gdx.files.internal("noiseFragment.glsl").readString());
		noiseShader.pedantic= false;

		//batch.setShader(shader);

		shaderController= new ShaderController(this);


		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose () {
		batch.dispose();
		skin.dispose();
	}
}
