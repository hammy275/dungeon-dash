package net.blf02.dungeondash.utils;

import org.bukkit.scheduler.BukkitTask;

public class TaskWithAfter {

    public BukkitTask task;
    public Runnable after;

    public TaskWithAfter(BukkitTask task, Runnable after) {
        this.task = task;
        this.after = after;
    }
}
