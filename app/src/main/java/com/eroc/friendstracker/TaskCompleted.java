package com.eroc.friendstracker;

/**
 * Created by Pol on 30/11/2015.
 */
public interface TaskCompleted {
    // Define data you like to return from AysncTask
    public void onTaskComplete(Integer result);
}