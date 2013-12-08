package stream.pimedia.upnp;

import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;

import android.util.Log;

/**
 * ActionCallback for content directory browsing. 
 * Connect an instance of this class to a MediaServer-Service.
 * After calling run you will browse the MediaServer-Directory asynchronously 
 * @author Logan Miller  
 *
 */
public class ContentDirectoryBrowseActionCallback extends Browse {	
	private ContentDirectoryBrowseResult browsingResult;
	

	
	public ContentDirectoryBrowseActionCallback(Service<?, ?> service, String objectID,
			BrowseFlag flag, String filter, long firstResult, Long maxResults, ContentDirectoryBrowseResult browsingResult,
			SortCriterion... orderBy) {
		super(service, objectID, flag, filter, firstResult, maxResults, orderBy);
		this.browsingResult = browsingResult;

	}

	
	

	/* (non-Javadoc)
	 * @see org.teleal.cling.support.contentdirectory.callback.Browse#receivedRaw(org.teleal.cling.model.action.ActionInvocation, org.teleal.cling.support.model.BrowseResult)
	 */
	@Override
	public boolean receivedRaw(ActionInvocation actionInvocation,
			BrowseResult browseResult) {
		// TODO Auto-generated method stub
		Log.d(this.getClass().getName(), "RAW-Result: " + browseResult.getResult());
		return super.receivedRaw(actionInvocation, browseResult);
	}




	@Override
	public void received(ActionInvocation actionInvocation, DIDLContent didl) {		
		this.browsingResult.setResult(didl);
	}
	

	@Override
	public void updateStatus(Status status) {
		this.browsingResult.setStatus(status);
	}

	@Override
	public void failure(ActionInvocation invocation, UpnpResponse operation,
			String defaultMsg) {
		this.browsingResult.setUpnpFailure(new UpnpFailure(invocation, operation, defaultMsg));

	}

	public Status getStatus() {
		return this.browsingResult.getStatus();
	}


	/**
	 * @return the result
	 */
	public DIDLContent getResult() {
		return this.browsingResult.getResult();
	}


	/**
	 * @return the upnpFailure
	 */
	public UpnpFailure getUpnpFailure() {
		return this.browsingResult.getUpnpFailure();
	}

}