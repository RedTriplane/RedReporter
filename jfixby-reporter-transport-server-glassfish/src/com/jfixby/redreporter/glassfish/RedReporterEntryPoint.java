
package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.io.Buffer;
import com.jfixby.cmns.api.io.BufferInputStream;
import com.jfixby.cmns.api.io.GZipInputStream;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.OutputStream;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.redreporter.api.DeviceRegistration;
import com.jfixby.redreporter.server.api.ReporterServer;

public class RedReporterEntryPoint extends AbstractEntryPoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 8584178804364883918L;
	private static final long MAX_BYTES_TO_READ = 1024 * 1024;
	static long request = 0;
	private String session_id;

	@Override
	void processRequest (final String session_id, final ServletInputStream client_to_server_stream,

		final HashMap<String, String> client_to_server_headers, final ServletOutputStream server_to_client_stream,
		final HashMap<String, String> server_to_client_headers) {

		L.d("---[" + (request++) + "]-----------------------------------");

		L.d("session_id", session_id);
		this.session_id = session_id;
		L.d("instance_id", this.instance_id());
		final Map<String, String> inputHeaders = Collections.newMap(client_to_server_headers);
		inputHeaders.print("inputHeaders");

		final Map<String, String> outputHeaders = Collections.newMap(server_to_client_headers);
		outputHeaders.print("outputHeaders");

		try {
			final String len = inputHeaders.get("content-length");
			if (len == null) {
				this.sayHello(server_to_client_stream);
				return;
			}
			if ("0".equals(len)) {
				this.sayHello(server_to_client_stream);
				return;
			}
			final InputStream is = IO.newInputStream( () -> client_to_server_stream);
			is.open();

			final ByteArray bytes = IO.readMax(is, MAX_BYTES_TO_READ);
			final ByteArray data;
			{
				final Buffer buffer = IO.newBuffer(bytes);
				final BufferInputStream bis = IO.newBufferInputStream(buffer);
				bis.open();
				{
					final GZipInputStream gzip = IO.newGZipStream(bis);
					gzip.open();
					data = IO.readMax(gzip, MAX_BYTES_TO_READ);
					gzip.close();
				}
				bis.close();
			}

			final Message message = IO.deserialize(Message.class, data);
			is.close();

			this.processMessage(message);

			final OutputStream os = IO.newOutputStream( () -> server_to_client_stream);
			os.open();

			final ByteArray responceBytes = IO.serialize(message);
			final ByteArray compressedResponse = IO.compress(responceBytes);
			os.write(compressedResponse);
			os.close();
		} catch (final Throwable e) {
			L.e(e);
		}

	}

	private void processMessage (final Message message) {
		message.print();
		message.values.put("instance_id", this.instance_id());
		message.values.put("session_id", this.session_id);

		final ID deviceID = ReporterServer.invoke().newDeviceID(this.instance_id(), this.session_id, "" + request);
		final DeviceRegistration devReg = ReporterServer.registerDevice(deviceID);
		devReg.print("register device");

	}

	final String start;
	{
		this.start = System.currentTimeMillis() + "";
	}

	private String timestamp () {
		return this.start;
	}

	String instance_id;

	private String instance_id () {
		if (this.instance_id == null) {
			try {
				final String url_string = "http://169.254.169.254/latest/meta-data/instance-id";
				final HttpURL url = Http.newURL(url_string);
				final HttpConnection connect = Http.newConnection(url);
				connect.open();
				final HttpConnectionInputStream is = connect.getInputStream();
				is.open();
				final ByteArray data = is.readAll();
				is.close();
				connect.close();
				this.instance_id = JUtils.newString(data);
			} catch (final Exception e) {
				L.d(e);
				this.instance_id = "no_id";
			}
		}
		return this.instance_id;
	}

	private void sayHello (final ServletOutputStream server_to_client_stream) throws IOException {
		server_to_client_stream.write(("Service is operating normally " + new Date()).getBytes());
	}

	public static final void main (final String[] arg) {

	}

}