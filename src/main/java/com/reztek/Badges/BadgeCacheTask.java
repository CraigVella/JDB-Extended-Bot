package com.reztek.Badges;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import com.reztek.Base.Taskable;
import com.reztek.Global.GlobalDefs;

/**
 * Badge Cache Cleaner Task
 * <p>Called every 20 minutes, where it checks the {@link GlobalDefs.LOCAL_BADGE_CACHE} to see if there is any badges over 24hrs.
 * After 24hrs the badge image will be deleted. </p>
 * @author Craig Vella
 *
 */
public class BadgeCacheTask extends Taskable{
	
	private final long CACHE_TIMEOUT = 86400000; // 24 hrs
	
	public BadgeCacheTask() {
		setTaskName("Badge Cache Cleaner");
		setTaskDelay(20);
	}

	@Override
	public void runTask() {
		cleanCache();
	}
	
	/**
	 * Checks the {@code GlobalDefs.LOCAL_BADGE_CACHE} for images over 24hrs old and deletes them
	 */
	public void cleanCache() {
		System.out.println("Starting Cache Cleaning Check...");
		File cacheDir = new File(GlobalDefs.LOCAL_BADGE_CACHE);
		if (!cacheDir.isDirectory()) {
			System.out.println("GlobalDefs.LOCAL_BADGE_CACHE is not a Directory!");
			return;
		}
		Date d = new Date();
		for (File f : cacheDir.listFiles()) {
			try {
				BasicFileAttributes ba = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
				if ((d.getTime() - ba.creationTime().toMillis()) >= CACHE_TIMEOUT) {
					System.out.println(f.getName() + " is out of cache date - deleting");
					f.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Cache Cleaning Complete!");
	}
}
