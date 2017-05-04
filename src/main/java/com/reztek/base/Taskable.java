package com.reztek.base;

import com.reztek.SGAExtendedBot;

public abstract class Taskable {
	protected int __delay = 0;
	private int ___delayCount = 0;
	private String p_TaskName = null;
	private SGAExtendedBot p_bot = null;
	
	public Taskable(SGAExtendedBot bot) {
		p_bot = bot;
	}
	
	public abstract void runTask();
	
	public void __taskTick() {
		if (++___delayCount >= __delay) {
			runTask();
			___delayCount = 0;
		}
	}
	
	public void setTaskName(String taskName) {
		p_TaskName = taskName;
	}
	
	public int getTaskDelayCount() {
		return ___delayCount;
	}
	
	public String getTaskName() {
		return p_TaskName;
	}
	
	public void setTaskDelay(int minute) {
		if (minute < 0) return;
		__delay = minute;
	}
	
	public int getTaskDelay() {
		return __delay;
	}
	
	public SGAExtendedBot getBot() {
		return p_bot;
	}
}
