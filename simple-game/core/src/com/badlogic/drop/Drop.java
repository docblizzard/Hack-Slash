package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends ApplicationAdapter {
   private Texture dropImage;
   private Texture bucketImage;
   private Sound dropSound;
   private Music rainMusic;
   private SpriteBatch batch;
   private OrthographicCamera camera;
   private Rectangle bucket;
   private Array<Rectangle> raindrops;
   private long lastDropTime;
   
   @Override
   public void create() {
      // load the images for the droplet and the bucket, 64x64 pixels each
      dropImage = new Texture(Gdx.files.internal("droplet.png"));
      bucketImage = new Texture(Gdx.files.internal("bucket.png"));

      // load the drop sound effect and the rain background "music"
      dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
      rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

      // start the playback of the background music immediately
      rainMusic.setLooping(true);
      rainMusic.play();

      // Camera loading
      camera = new OrthographicCamera();
      camera.setToOrtho(false, 800, 480);
      
      // Sprite
      batch = new SpriteBatch();
      
      // Instantiate bucket 
      bucket = new Rectangle();
      bucket.x = 800 / 2 - 64 / 2;
      bucket.y = 20;
      bucket.width = 64;
      bucket.height = 64;
      
      // Raindrop instantiate
      raindrops = new Array<Rectangle>();
      spawnRaindrop();

   }
   @Override
   public void render() {
      ScreenUtils.clear(0, 0, 0.2f, 1);
      camera.update();
      batch.setProjectionMatrix(camera.combined);
      batch.begin();
      batch.draw(bucketImage, bucket.x, bucket.y);
      
      // Rain Drop rendering
      for(Rectangle raindrop: raindrops) {
          batch.draw(dropImage, raindrop.x, raindrop.y);
          
       }
      if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
      for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
          Rectangle raindrop = iter.next();
          raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
          if(raindrop.y + 64 < 0) iter.remove();
          if(raindrop.overlaps(bucket)) {
              dropSound.play();
              iter.remove();
           }
       }
      
      batch.end();
      inputs();

   }
   // Inputs events
   private void inputs() {
	   if(Gdx.input.isTouched()) {
	          Vector3 touchPos = new Vector3();
	          touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	          camera.unproject(touchPos);
	          bucket.x = touchPos.x - 64 / 2;
	       }
	      if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
	      if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
	      if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
	    	  while ( bucket.y < 200 ) {
	    		  bucket.y += 1 * Gdx.graphics.getDeltaTime();
	    	  }
	      }
	      if(bucket.x < 0) bucket.x = 0;
	      if(bucket.y > 800 - 64 ) bucket.y = 800 - 64;
	   }
   // Spawn Rain drops
   private void spawnRaindrop() {
	      Rectangle raindrop = new Rectangle();
	      raindrop.x = MathUtils.random(0, 800-64);
	      raindrop.y = 480;
	      raindrop.width = 64;
	      raindrop.height = 64;
	      raindrops.add(raindrop);
	      lastDropTime = TimeUtils.nanoTime();
	   }
   //private void RainMove() {
   
   // rest of class omitted for clarity
}