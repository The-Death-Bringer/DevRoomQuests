package me.dthb.drq.quest;

public class QuestProgress {

    private final int goal;
    private double progress;

    public QuestProgress(int goal) {
        this.goal = goal;
    }

    public int goal() {
        return goal;
    }

    public double progress() {
        return progress;
    }

    public boolean isComplete() {
        return progress >= goal;
    }

    public void increaseProgress(double progress) {
        this.progress += progress;
    }

}
