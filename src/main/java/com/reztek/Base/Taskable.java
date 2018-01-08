package com.reztek.Base;

/**
 * An Abstract class meant to be extended to be used as a Task Object
 * <p>Tasks are objects that run continually at a scheduled interval, they are passed to
 * {@link JDBExtendedBot.GetBot().addTask()} to be added as a task. As explained in that method
 * Tasks are not guaranteed to run exactly on schedule as they are synchronous in nature and 
 * although at most times will run on time could be delayed based on tasks in front of it.
 * <pre>
 * <code>
 *  class MyTask extends Taskable {
 *    public MyTask() {
 *      setTaskName("My Task");
 *      setTaskDelay(5); // Run every 5 minutes
 *    }
 *    
 *    public void runTask() {
 *      System.out.println("I'm Running!");
 *    }
 *  }
 * </code>
 * </pre>
 * @author Craig Vella
 *
 */
public abstract class Taskable {
	protected int __delay = 0;
	private int ___delayCount = 0;
	private String p_TaskName = null;
	
	/**
	 * <b>Must be Overridden</b><br />
	 * Gets called every minute or Minute x Delay set in {@code setTaskDelay()}
	 */
	public abstract void runTask();
	
	/**
	 * <b>Called By Task Engine, NEVER CALL THIS</b>
	 */
	public void __taskTick() {
		if (++___delayCount >= __delay) {
			runTask();
			___delayCount = 0;
		}
	}
	
	/**
	 * Set the TaskName
	 * @param taskName - Name of Task
	 */
	public void setTaskName(String taskName) {
		p_TaskName = taskName;
	}
	
	/**
	 * Get the current Task Delay Count
	 * @return the current minute of delay task is in
	 */
	public int getTaskDelayCount() {
		return ___delayCount;
	}
	
	/**
	 * Get the Task Name
	 * @return String containing Task name
	 */
	public String getTaskName() {
		return p_TaskName;
	}
	
	/**
	 * Set the delay in minutes for the task
	 * @param minute - minutes to delay the task by
	 */
	public void setTaskDelay(int minute) {
		if (minute < 0) return;
		__delay = minute;
	}
	
	/**
	 * Get the delay in minutes for the task
	 * @return the delay in minutes of the task
	 */
	public int getTaskDelay() {
		return __delay;
	}
}
