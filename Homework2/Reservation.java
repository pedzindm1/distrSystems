
/**
 * A Seat-Person in the reservation system
 *
 */
public class Reservation {

	/**
	 * A Seat
	 */
	private int _seat;
	/**
	 * A Person
	 */
	private String _person = "";

	/**
	 * Returns the seat
	 */
	public int getSeat() {
		return _seat;
	}

	/**
	 * Returns the person
	 */
	public String getPerson() {
		return _person;
	}

	/**
	 * sets the person to the seat
	 */
	public void setPerson(String personName) {
		_person = personName;
	}

	/**
	 * USED FOR DEBUGGING
	 * 
	 * @return String of Reservation
	 */
	public String getStatus() {
		return _seat + ":" + _person;
	}

	/**
	 * Creates a seat
	 * 
	 * @param seat
	 */
	public Reservation(int seat) {
		_seat = seat;
	}

}
