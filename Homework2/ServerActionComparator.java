import java.util.Comparator;

/**
 * Comparator to help sort the server action lists
 *
 */
public class ServerActionComparator implements Comparator<ServerCommand> {

	@Override
	public int compare(ServerCommand o1, ServerCommand o2) {
		if (o1.getClock().getTimestamp() == o2.getClock().getTimestamp()) {
			return 0;
		} else if (o1.getClock().getTimestamp() > o2.getClock().getTimestamp()) {
			return 1;
		} else {
			return -1;
		}
	}

}
