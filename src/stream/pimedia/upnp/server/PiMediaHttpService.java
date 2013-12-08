package stream.pimedia.upnp.server;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpService;
import org.teleal.common.util.MimeType;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * A http service to retrieve media content by an id. 
 * @author Logan Miller
 * 
 */
public class PiMediaHttpService extends HttpService {

	private Context context;

	public PiMediaHttpService(HttpProcessor proc,
                              ConnectionReuseStrategy connStrategy,
                              HttpResponseFactory responseFactory, Context context) {
		super(proc, connStrategy, responseFactory);
		this.context = context;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.http.protocol.HttpService#doService(org.apache.http.HttpRequest
	 * , org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	@Override
	protected void doService(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		Log.d(getClass().getName(), "Processing HTTP request: "
				+ request.getRequestLine().toString());
		
		// Extract what we need from the HTTP httpRequest
		String requestMethod = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		// Only accept HTTP-GET
		if (!requestMethod.equals("GET") && !requestMethod.equals("HEAD")) {
			Log.d(getClass().getName(), "HTTP request isn't GET or HEAD stop! Method was: " + requestMethod);
			throw new MethodNotSupportedException(requestMethod
					+ " method not supported");
		}
		
		Uri requestUri = Uri.parse(request.getRequestLine().getUri());
		String contentId = requestUri.getQueryParameter("id");

		if (contentId == null || contentId.equals("")) {

			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			StringEntity entity = new StringEntity(
					"<html><body><h1>Access denied</h1></body></html>", "UTF-8");
			response.setEntity(entity);
			Log.d(getClass().getName(), "end doService: Access denied");
			return;
		}
		ContentHolder contentHolder = lookup(contentId);
		if (contentHolder == null) {
			Log.d(getClass().getName(), "Content with id " + contentId
					+ " not found");
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			StringEntity entity = new StringEntity(
					"<html><body><h1>Content with id " + contentId
							+ " not found</h1></body></html>", "UTF-8");
			response.setEntity(entity);
		} else {

			File file = new File(contentHolder.getUri());
			response.setStatusCode(HttpStatus.SC_OK);
			FileEntity body = new FileEntity(file, contentHolder.getMimeType()
					.toString());
			Log.d(getClass().getName(),
					"Return file-Uri: " + contentHolder.getUri() + "Mimetype: "
							+ contentHolder.getMimeType());

			response.setEntity(body);
		}
		Log.d(getClass().getName(), "end doService: ");
	}

	private Context getContext() {
		return context;
	}

	/**
	 * Lookup content in the mediastore
	 * 
	 * @param contentId
	 *            the id of the content
	 * @return the content description
	 */
	private ContentHolder lookup(String contentId) {
		ContentHolder result = null;
		if (contentId == null) {
			return null;
		}
		Log.d(getClass().getName(), "System media store lookup: " + contentId);
		String[] projection = { MediaStore.Files.FileColumns._ID,
				MediaStore.Files.FileColumns.MIME_TYPE,
				MediaStore.Files.FileColumns.DATA };
		String selection = MediaStore.Files.FileColumns._ID + "=?";
		String[] selectionArgs = { contentId };
		Cursor mFilesCursor = getContext().getContentResolver().query(
				MediaStore.Files.getContentUri("external"), projection,
				selection, selectionArgs, null);

		if (mFilesCursor != null) {
			mFilesCursor.moveToFirst();
			while (!mFilesCursor.isAfterLast()) {
				String dataUri = mFilesCursor.getString(mFilesCursor
						.getColumnIndex(MediaStore.Files.FileColumns.DATA));

				String mimeTypeStr = mFilesCursor
						.getString(mFilesCursor
								.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
				MimeType mimeType = MimeType.valueOf("*/*");
				if (mimeTypeStr != null) {
					mimeType = MimeType.valueOf(mimeTypeStr);
				}
				Log.d(getClass().getName(), "Content found: " + mimeType
						+ " Uri: " + dataUri);
				result = new ContentHolder(mimeType, dataUri);
				mFilesCursor.moveToNext();
			}
		} else {
			Log.d(getClass().getName(), "System media store is empty.");
		}
		mFilesCursor.close();
		return result;
	}

	/**
	 * 
	 * ValueHolder for media content.
	 * 
	 */
	static class ContentHolder {
		private String uri;
		private MimeType mimeType;

		public ContentHolder(MimeType mimeType, String uri) {
			this.uri = uri;
			this.mimeType = mimeType;

		}

		/**
		 * @return the uri
		 */
		public String getUri() {
			return uri;
		}

		/**
		 * @return the mimeType
		 */
		public MimeType getMimeType() {
			return mimeType;
		}
	}
}
