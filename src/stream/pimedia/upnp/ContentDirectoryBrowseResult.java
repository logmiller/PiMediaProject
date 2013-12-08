package stream.pimedia.upnp;

import org.teleal.cling.support.contentdirectory.callback.Browse.Status;
import org.teleal.cling.support.model.DIDLContent;

/**
 * Result of a content directory browsing.
 * This object is used either in synchronous or asynchronous requests.
 * In case of asynchronous requests you have to query the status 
 * in order to know if the request completes.   
 * @author Logan Miller 
 *
 */
public class ContentDirectoryBrowseResult {
	private Status status = Status.NO_CONTENT;
	private DIDLContent result  = null;
	private UpnpFailure upnpFailure;

	/**
	 * default constructor. 
	 *
	 *   
	 */
	public ContentDirectoryBrowseResult() {
		super();
		
	}


	/**
	 * Returns the status of browsing, i.e. LAODING, NO_CONTENT, OK.
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}


	/**
	 * Returns the browsing result.
	 * @return the result
	 */
	public DIDLContent getResult() {
		return result;
	}


	/**
	 * a failure object if anything goes wrong.
	 * @return the upnpFailure
	 */
	public UpnpFailure getUpnpFailure() {
		return upnpFailure;
	}


	/**
	 * Set the status of browsing
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}


	/**
	 * @param result the result to set
	 */
	public void setResult(DIDLContent result) {
		this.result = result;
	}


	/**
	 * @param upnpFailure the upnpFailure to set
	 */
	public void setUpnpFailure(UpnpFailure upnpFailure) {
		this.upnpFailure = upnpFailure;
	}

}