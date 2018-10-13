package com.speedcoder;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CodeSpeed extends Game {
	public SpriteBatch batch;

	public Skin skin;

	public FileHandle codesFile;


	@Override
	public void create () {
		batch = new SpriteBatch();

		skin= new Skin(Gdx.files.internal("skin.json"));

		codesFile= Gdx.files.internal("codes");

		//FileHandle dictionary= Gdx.files.internal("dictionary.txt");
		//LanguageProcessing.readWords(dictionary.readString());

		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose () {
		batch.dispose();
		skin.dispose();
	}
}
