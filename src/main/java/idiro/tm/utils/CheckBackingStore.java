package idiro.tm.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Checks if the preferences tree is available or not
 *  
 * @author Etienne Dumoulin
 *
 */
public class CheckBackingStore{
	private static final String BACKING_STORE_AVAIL = "Backing_store_available";

	/**
	 * Test the availability of the tree
	 * 
	 * @return true if available
	 */
    public static boolean isAvailable() {
        Preferences prefs = Preferences.userRoot().node("");
        try {
            boolean oldValue = prefs.getBoolean(BACKING_STORE_AVAIL, false);
            prefs.putBoolean(BACKING_STORE_AVAIL, !oldValue);
            prefs.flush();
        } catch(BackingStoreException e) {
            return false;
        }
        return true;
    }

}