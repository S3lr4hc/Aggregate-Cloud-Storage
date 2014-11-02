package main;

/**
 * Supports the basic functions for a generic remote drive application. Provides
 * a blueprint for a selection of operations that allows a user to interact with
 * their cloud storage account from a desktop application.
 * 
 * @author Fusein
 * @date March 17, 2014
 * @version 2.0
 */
public interface RemoteDrive {
	
 	/**
	 * Returns the nice name of the underlying service provider.
	 * 
	 * @return The nice name of the underlying service provider (e.g. Google Drive).
	 */
	public String getServiceNiceName();
	
	/**
	 * Returns the username of the associated account.
	 * 
	 * @return The username of the associated account (e.g. superdude301).
	 */
	public String getUsername();

	/**
	 * Creates the authentication URL that the user can visit to find their
	 * authentication token.
	 * 
	 * @return The authentication token that was generated by the service
	 */
	public String generateAuthURL();

	/**
	 * Completes the OAuth2.0 authentication process by validating the user's
	 * authentication token with the server.
	 * 
	 * @param authTok
	 *            The user's auto generated authentication token, passed as a
	 *            string.
	 * @return True if the authentication succeeds, false otherwise.
	 */
	public boolean finalizeAuth(String authToken);
	
	/**
	 * Returns the OAuth2.0 authentication token used to access the associated account.
	 * 
	 * @return The OAuth2.0 auth token.
	 */
	public String getAuthToken();
	
	/**
	 * Sets the OAuth2.0 authentication token used to access the associated account.
	 * 
	 * @return The OAuth 2.0 auth token.
	 */
	public void setAuthToken(String newToken);

	/**
	 * Returns the root folder.
	 * 
	 * @return The root folder.
	 */
	public RemoteFolder getRootFolder();
	
	/**
     * Get the total available size of the current account on the current service
     *
     * @return  The total available size in bytes
     */
    public double getTotalSize();

    /**
     * Get the current amount of used space for the current account on the current service
     *
     * @return  The amount of currently used space in bytes
     */
    public double getUsedSize();
}
