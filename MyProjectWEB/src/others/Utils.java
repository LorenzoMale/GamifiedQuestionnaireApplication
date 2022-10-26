package others;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

public class Utils {

	public static byte[] readImage(InputStream imageInputStream) throws IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];// image can be maximum of 4MB
		int bytesRead = -1;

		try {
			while ((bytesRead = imageInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			byte[] imageBytes = outputStream.toByteArray();
			return imageBytes;
		} catch (IOException e) {
			throw e;
		}

	}
	
	public static Date yesterday() {
		return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	}
}
