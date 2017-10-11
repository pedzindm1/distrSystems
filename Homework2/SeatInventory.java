import java.util.ArrayList;

/**
 * Creates the reservation system
 *
 */
public class SeatInventory {

	/**
	 * List of seats that contains the name and seat number
	 */
	ArrayList<Reservation> _listOfSeats;

	/**
	 * Returns the seatList
	 * 
	 * @return Seat Inventory
	 */
	public ArrayList<Reservation> getSeatList() {
		return _listOfSeats;
	}

	/**
	 * Creates the seat inventory with the numbers of Seats from input
	 * 
	 * @param numberOfSeats
	 */
	public SeatInventory(int numberOfSeats) {
		_listOfSeats = new ArrayList<Reservation>();
		for (int i = 1; i <= numberOfSeats; i++) {
			Reservation seat = new Reservation(i);
			_listOfSeats.add(seat);
		}
	}

	/**
	 * Reserves a seat in the inventory and responses to the client
	 * 
	 * @param personName
	 * @return String response for Client
	 */
	public String ReserveSeat(String personName) {
		int openSeats = 0;
		int firstOpenSeatIndex = -1;
		boolean isAlreadyBooked = false;

		for (int i = 0; i < _listOfSeats.size(); i++) {
			if (_listOfSeats.get(i).getPerson() == "") {
				if (firstOpenSeatIndex == -1) {
					firstOpenSeatIndex = i;
				}
				openSeats++;
			} else if (_listOfSeats.get(i).getPerson().equalsIgnoreCase(personName)) {
				isAlreadyBooked = true;
				break;
			}
		}

		if (isAlreadyBooked) {
			return "Seat already booked against the name provided";
		} else if (openSeats == 0) {
			return "Sold out - No seat available";
		} else {
			_listOfSeats.get(firstOpenSeatIndex).setPerson(personName);
			return "Seat assigned to you is " + _listOfSeats.get(firstOpenSeatIndex).getSeat();
		}

	}

	/**
	 * Reserves a particular seat in the inventory and responses to the client
	 * 
	 * @param personName
	 * @return String response for Client
	 */
	public String ReserveThatSeat(String personName, int SeatNum) {
		int openSeats = 0;
		boolean isAlreadyBooked = false;

		for (int i = 0; i < _listOfSeats.size(); i++) {
			if (_listOfSeats.get(i).getPerson() == "") {
				openSeats++;
			} else if (_listOfSeats.get(i).getPerson().equalsIgnoreCase(personName)) {
				isAlreadyBooked = true;
				break;
			}
		}
		if (isAlreadyBooked) {
			return "Seat already booked against the name provided";
		} else if (_listOfSeats.get(SeatNum - 1).getPerson() == "") {
			_listOfSeats.get(SeatNum - 1).setPerson(personName);
			return "Seat assigned to you is " + _listOfSeats.get(SeatNum - 1).getSeat();
		} else if (_listOfSeats.get(SeatNum - 1).getPerson().equalsIgnoreCase(personName)) {
			return "Seat already booked against the name provided";
		} else if (openSeats == 0) {
			return "Sold out - No seat available";
		} else {
			return SeatNum + " is not available";
		}

	}

	/**
	 * Searches for a person in the inventory and responses to the client
	 * 
	 * @param personName
	 * @return String response for Client
	 */
	public String SearchPerson(String personName) {
		for (int i = 0; i < _listOfSeats.size(); i++) {
			if (_listOfSeats.get(i).getPerson().equalsIgnoreCase(personName)) {
				return "" + _listOfSeats.get(i).getSeat();
			}
		}
		return "No reservation found for " + personName;

	}

	/**
	 * Deletes a person in the inventory and responses to the client
	 * 
	 * @param personName
	 * @return String response for Client
	 */
	public String RemoveReservation(String personName) {
		for (int i = 0; i < _listOfSeats.size(); i++) {
			if (_listOfSeats.get(i).getPerson().equalsIgnoreCase(personName)) {
				_listOfSeats.get(i).setPerson("");
				return "" + _listOfSeats.get(i).getSeat();
			}
		}
		return "No reservation found for " + personName;

	}
	/**
	 * USED FOR DEBUGGING
	 * 
	 * @return String of Inventory
	 */
	public String getStatus() {
		String str = "Sync :";
		for (int i = 0; i < _listOfSeats.size(); i++) {
			str = str + _listOfSeats.get(i).getStatus() + ", ";
		}
		return str;
	}

}
