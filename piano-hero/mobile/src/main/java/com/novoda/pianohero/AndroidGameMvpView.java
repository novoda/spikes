package com.novoda.pianohero;

class AndroidGameMvpView implements GameMvp.View {

    private final GameScreen gameScreen;
    private final Speaker speaker;
    private final ScoreDisplayer scoreDisplayer;

    AndroidGameMvpView(GameScreen gameScreen, Speaker speaker, ScoreDisplayer scoreDisplayer) {
        this.gameScreen = gameScreen;
        this.speaker = speaker;
        this.scoreDisplayer = scoreDisplayer;
    }

    @Override
    public void play(Sound sound) {
        if (sound.isOfSilence()) {
            stopSound();
        } else {
            startSound(sound.frequency());
        }
    }

    private void stopSound() {
        speaker.stop();
    }

    private void startSound(double midi) {
        speaker.stop();
        speaker.start(midi);
    }

    @Override
    public void showClock(ClockViewModel viewModel) {
        gameScreen.showClock(viewModel);
    }

    @Override
    public void showGameStarted(GameStartViewModel viewModel) {
        gameScreen.showGameStart(viewModel);
    }

    @Override
    public void show(GameInProgressViewModel viewModel) {
        gameScreen.show(viewModel);
        scoreDisplayer.display(viewModel.getScore());
    }

    @Override
    public void showSong(SongStartViewModel viewModel) {
        gameScreen.showSongStart(viewModel);
    }

    @Override
    public void showSongComplete(SongCompleteViewModel viewModel) {
        gameScreen.showSongComplete(viewModel);
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        gameScreen.showGameComplete(viewModel);
        scoreDisplayer.clearScore();
    }
}
