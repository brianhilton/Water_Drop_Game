package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class MyGdxGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private BitmapFont font;
	private Array<Sound> goofyAhhArray;
	private int counter;


	private int score;
	private void spawnRainDrop() {
		counter = 0;
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, (800 - 64));
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void create () {
		counter = 0;
		score = 0;
		dropImage = new Texture(Gdx.files.internal("drop.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		font = new BitmapFont();

		// Make bucket
		bucket = new Rectangle();
		bucket.x = (800 / 2) - (64 / 2);
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		// Make goofy
		goofyAhhArray = new Array<Sound>();
		goofyAhhArray.add(Gdx.audio.newSound(Gdx.files.internal("goofy1.wav")));
		goofyAhhArray.add(Gdx.audio.newSound(Gdx.files.internal("goofy2.wav")));
		goofyAhhArray.add(Gdx.audio.newSound(Gdx.files.internal("goofy3.wav")));
		goofyAhhArray.add(Gdx.audio.newSound(Gdx.files.internal("goofy4.wav")));

		raindrops = new Array<Rectangle>();
		spawnRainDrop();


	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();

		// Draw bucket and raindrops
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}

		// Write score
		font.draw(batch, "Score:" + score, 5, 460);

		if (score < 10)
			font.draw(batch, "Collect more Waters!", 5, 440);
		else
			font.draw(batch, "Wet!", 5, 440);

		batch.end();

		// Mouse input handler
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - (64 / 2);
		}

		// WASD key input handler
		if(Gdx.input.isKeyPressed(Input.Keys.A)) bucket.x -= 400 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.D)) bucket.x += 400 * Gdx.graphics.getDeltaTime();

		// Make sure bucket does not go out of bounds/frame
		if(bucket.x < 0)
			bucket.x = 0;
		if(bucket.x > (800 - 64))
			bucket.x = (800 - 64);

		// Time to spawn rain drop
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000)
			counter++;
		if(counter > 30)
			spawnRainDrop();

		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();

			// Raindrop falls
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

			// Bounds checking drop
			if (raindrop.y + 64 < 0)
				iter.remove();

			// Collision checking (raindrop and bucket)
			if (raindrop.overlaps(bucket)) {
				goofyAhhArray.get(ThreadLocalRandom.current().nextInt(0, 3 + 1)).play();
				iter.remove();
				++score;
			}


		}


	}
	
	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		batch.dispose();
	}
}
