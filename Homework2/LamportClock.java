import java.io.Serializable;

public class LamportClock implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8551220727635934285L;
	private int _timestamp=0;

	/**
	 * @return the _timestamp
	 */
	public int getTimestamp() {
		return _timestamp;
	}

	/**
	 * @param _timestamp the _timestamp to set
	 */
	public void setTimestamp(int _timestamp) {
		this._timestamp = _timestamp;
	}
	
	/**
	 * Creates a Lamport Clock on value
	 * @param _timestamp the _timestamp to set
	 */
	public LamportClock(int timestamp) {
		_timestamp= timestamp;
		
	}
	/**
	 * Creates a default Lamport Clock on value

	 */
	public LamportClock() {
		_timestamp=0;
	}

	@Override
	public String toString() {
		return String.valueOf(_timestamp);
	}
	
	/*
	 * Hash so that we can utilize via contains in the server class
	 */
	@Override
	public int hashCode() {
		return _timestamp;
	}

	public void receiveMessageAction(LamportClock messageClock) {
		int newClock = Math.max(_timestamp,messageClock.getTimestamp());
		_timestamp= newClock+1;
	}
	
	public void sendMessageAction() {
		_timestamp= _timestamp+1;

	}

}
