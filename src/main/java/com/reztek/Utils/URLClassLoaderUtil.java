package com.reztek.Utils;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class URLClassLoaderUtil extends URLClassLoader {

	public URLClassLoaderUtil(URL[] urls) {
		super(urls);
	}

	private static final Class<?>[] parameters = new Class<?>[] { URL.class };

	public void addJarURL(URL u) throws Exception {
		try {

			URLClassLoader sysLoader = (URLClassLoader) ClassLoader
					.getSystemClassLoader();
			URL urls[] = sysLoader.getURLs();
			for (int i = 0; i < urls.length; i++) {
				if (urls[i].toString().equalsIgnoreCase(u
						.toString())) {
					return;
				}
			}
			Class<URLClassLoader> sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Error, could not add URL to system classloader"
							+ e.getMessage());
		}

	}
}
