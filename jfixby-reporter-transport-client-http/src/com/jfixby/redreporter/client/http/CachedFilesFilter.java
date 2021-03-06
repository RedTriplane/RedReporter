
package com.jfixby.redreporter.client.http;

import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FileFilter;

public class CachedFilesFilter implements FileFilter {
	protected static final String FILE_NAME_SUFFIX = ".log";

	@Override
	public boolean fits (final File element) {
		return element.getName().endsWith(FILE_NAME_SUFFIX);

	}

}
