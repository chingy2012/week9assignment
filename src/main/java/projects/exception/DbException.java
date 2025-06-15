package projects.exception;


public class DbException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	 /**
	   * Create an exception with a message. The message is stored in the parent class.
	   * 
	   * @param message The message.
	   */
	public DbException(String message) {
		super(message);
	}
	  /**
	   * Create an exception with a cause. The cause is stored in the parent class.
	   * 
	   * @param cause The cause.
	   */
	public DbException(Throwable cause) {
		super(cause);
	}
	 /**
	   * Create an exception with a message and a cause.
	   * 
	   * @param message The message.
	   * @param cause The cause.
	   */
	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

}
