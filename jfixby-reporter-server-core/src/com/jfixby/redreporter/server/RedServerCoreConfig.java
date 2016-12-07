
package com.jfixby.redreporter.server;

import com.jfixby.cmns.db.api.DataBase;
import com.jfixby.redreporter.server.api.ServerCoreConfig;

public class RedServerCoreConfig implements ServerCoreConfig {

	private DataBase mySQL;
	private String bucketName;

	@Override
	public void setDataBase (final DataBase mySQL) {
		this.mySQL = mySQL;
	}

	@Override
	public void setS3BucketName (final String bucketName) {
		this.bucketName = bucketName;
	}

	@Override
	public DataBase getDataBase () {
		return this.mySQL;
	}

	@Override
	public String getBucketName () {
		return this.bucketName;
	}

}
