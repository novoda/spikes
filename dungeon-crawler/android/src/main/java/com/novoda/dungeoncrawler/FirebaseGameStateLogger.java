package com.novoda.dungeoncrawler;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.Types;
import com.yheriatovych.reductor.Dispatcher;
import com.yheriatovych.reductor.Middleware;
import com.yheriatovych.reductor.Store;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FirebaseGameStateLogger implements Middleware<Redux.GameState> {

    private final List<String> frames;
    private final Moshi moshi = new Moshi.Builder()
            .add(new GameStateAdapter())
            .build();
    private final JsonAdapter<Redux.GameState> adapter = moshi.adapter(Redux.GameState.class);
    private final Type type = Types.newParameterizedType(List.class, String.class);
    private final JsonAdapter<List<String>> framesAdapter = moshi.adapter(type);

    private Stage lastStage;

    FirebaseGameStateLogger() {
        this.frames = new ArrayList<>();
    }

    @Override
    public Dispatcher create(Store<Redux.GameState> store, Dispatcher nextDispatcher) {
        return action -> {
            Redux.GameState state = store.getState();
//            new Thread(() -> jsonize(state, lastStage)).start();
            jsonize(state);
            log(state);
            nextDispatcher.dispatch(action);
            lastStage = state.stage;
        };
    }

    private void jsonize(Redux.GameState state) {
        String json = adapter.toJson(state);
        frames.add(json);
    }

    private void log(Redux.GameState state) {
        Stage stage = state.stage;
        if (!stage.equals(lastStage)) {
            if (stage == Stage.GAME_OVER || stage == Stage.GAME_COMPLETE) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                long timeStamp = System.currentTimeMillis();
                final List<String> tmpFrames = new ArrayList<>(frames);
                database.child("currentGamerTag").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        database.child("games").child(String.valueOf(timeStamp)).child("frames")
                                .setValue(framesAdapter.toJson(tmpFrames));
                        database.child("games").child(String.valueOf(timeStamp)).child("gamerTag")
                                .setValue(dataSnapshot.getValue());

                        frames.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // do nothing
                    }
                });
            }
            if (stage == Stage.SCREENSAVER || lastStage == Stage.SCREENSAVER && stage == Stage.PLAY) {
                frames.clear();
            }
        }
    }

    public static class GameStateAdapter {

        // GameStateJson is the ideal way to store it
        // GameState is our performance class with the static enemy collections

        @FromJson
        @SuppressWarnings("unused")
        public Redux.GameState gameStateFromJson(GameStateJson gameStateJson) {
            Redux.GameState gameState = new Redux.GameState();

            // I'm worried about threading...
            // this is another thread, so the static collection
            // could be read before we have pasted to it
            gameState.particlePool = gameStateJson.particlePool;
            gameState.enemyPool = gameStateJson.enemyPool;
            gameState.enemySpawnerPool = gameStateJson.enemySpawnerPool;
            gameState.lavaPool = gameStateJson.lavaPool;
            gameState.conveyorPool = gameStateJson.conveyorPool;

            gameState.boss = gameStateJson.boss;
            gameState.frameTime = gameStateJson.frameTime;
            gameState.lastInputTime = gameStateJson.lastInputTime;
            gameState.attackStartedTime = gameStateJson.attackStartedTime;
            gameState.attacking = gameStateJson.attacking;
            gameState.levelNumber = gameStateJson.levelNumber;
            gameState.playerPositionModifier = gameStateJson.playerPositionModifier;
            gameState.playerPosition = gameStateJson.playerPosition;
            gameState.stageStartTime = gameStateJson.stageStartTime;
            gameState.stage = gameStateJson.stage;
            gameState.lives = gameStateJson.lives;

            return gameState;
        }

        @ToJson
        @SuppressWarnings("unused")
        public GameStateJson gameStateToJson(Redux.GameState gameState) {
            GameStateJson gameStateJson = new GameStateJson();

            // I'm worried about threading...
            // this is another thread, so the static collection
            // could be updated before we have copied it
            gameStateJson.particlePool = gameState.particlePool;
            gameStateJson.enemyPool = gameState.enemyPool;
            gameStateJson.enemySpawnerPool = gameState.enemySpawnerPool;
            gameStateJson.lavaPool = gameState.lavaPool;
            gameStateJson.conveyorPool = gameState.conveyorPool;

            gameStateJson.boss = gameState.boss;
            gameStateJson.frameTime = gameState.frameTime;
            gameStateJson.lastInputTime = gameState.lastInputTime;
            gameStateJson.attackStartedTime = gameState.attackStartedTime;
            gameStateJson.attacking = gameState.attacking;
            gameStateJson.levelNumber = gameState.levelNumber;
            gameStateJson.playerPositionModifier = gameState.playerPositionModifier;
            gameStateJson.playerPosition = gameState.playerPosition;
            gameStateJson.stageStartTime = gameState.stageStartTime;
            gameStateJson.stage = gameState.stage;
            gameStateJson.lives = gameState.lives;

            return gameStateJson;
        }

    }

    static class GameStateJson {

        List<Particle> particlePool;
        List<Enemy> enemyPool;
        List<EnemySpawner> enemySpawnerPool;
        List<Lava> lavaPool;
        List<Conveyor> conveyorPool;

        Boss boss;
        long frameTime;
        long lastInputTime;

        long attackStartedTime;
        boolean attacking;

        int levelNumber;
        int playerPositionModifier;
        int playerPosition;
        long stageStartTime;
        Stage stage;
        int lives;
    }

}
