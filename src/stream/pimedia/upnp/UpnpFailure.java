package stream.pimedia.upnp;

import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;


/**
 * Value holder for upnp failure information
 * @author Logan Miller
 * 
 */
public class UpnpFailure {
	private ActionInvocation invocation;
	private UpnpResponse response;
	private String defaultMsg;

	/**
	 * constructor.
	 * 
	 * @param invocation
	 *            the ActionInvocation
	 * @param response
	 *            the Upnp response
	 * @param defaultMsg
	 *            a default message
	 */
	public UpnpFailure(ActionInvocation invocation, UpnpResponse response,
			String defaultMsg) {
		super();
		this.invocation = invocation;
		this.response = response;
		this.defaultMsg = defaultMsg;
	}

	/**
	 * @return the invocation
	 */
	public ActionInvocation getInvocation() {
		return invocation;
	}

	/**
	 * @return the operation
	 */
	public UpnpResponse getOperation() {
		return response;
	}

	/**
	 * @return the defaultMsg
	 */
	public String getDefaultMsg() {
		return defaultMsg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UpnpFailure ["
				+ (invocation != null ? "invocation=" + invocation + ", " : "")
				+ (response != null ? "response=" + response + ", " : "")
				+ (defaultMsg != null ? "defaultMsg=" + defaultMsg : "") + "]";
	}

}
