
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;

import com.jfixby.cmns.api.desktop.DesktopSetup;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.aws.api.AWS;
import com.jfixby.cmns.aws.api.S3FileSystem;
import com.jfixby.cmns.aws.api.S3FileSystemConfig;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class TestS3Bucket {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();

		final S3FileSystemConfig aws_specs = AWS.getS3().newFileSystemConfig();
		aws_specs.setAccessKeyID(CONFIG.S3_RR1_ACCESS_KEY);
		aws_specs.setSecretKeyID(CONFIG.S3_RR1_SECRET_KEY);
		aws_specs.setBucketName(CONFIG.S3_BUCKET_NAME);//
		final S3FileSystem S3 = AWS.getS3().newFileSystem(aws_specs);
		try {
			S3.ROOT().listAllChildren().print("remote");
		} catch (final Throwable e) {
			L.e("FAIL");
			e.printStackTrace();
		}
	}

}
