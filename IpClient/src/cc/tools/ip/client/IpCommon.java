package cc.tools.ip.client;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;

/**
 * This class contains common utility functions. This class is final and should
 * not be instantiated.
 * 
 * @author ccpgh
 * @version %I%, %G%
 * @since 0.1
 */

final public class IpCommon {
	/**
	 * Constructor
	 */
	private IpCommon() {
		throw new IllegalStateException("Cannot be instantiated");
	}

	/**
	 * Returns a boolean indicating whether the parameter token is a valid token.
	 * Each text value read into the problem from an external source should be
	 * checked by this function. Only a very limited set of characters are
	 * white-listed and allowed. Errors are written to the errors parameter for
	 * access by the caller.
	 *
	 * @param token  the String token value to be checked.
	 * @param errors any errors are written to this variable
	 * @return boolean indicating whether parameter token is a valid input value or
	 *         not
	 */
	public static boolean validToken(String token, List<String> errors) {
		for (char c : token.toCharArray()) {
			if (!(Character.isLetterOrDigit(c) || c == '/' || c == '\\' || c == '.' || c == '-' || c == '_')) {
				errors.add("invalid character '" + c + "' found in token '" + token + "'");
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a boolean indicating whether the parameter inet4Ip value is a valid
	 * iPv4 address.
	 *
	 * @param inet4Ip the iPv4 address value to be checked.
	 * @return boolean indicating whether parameter inet4Ip is valid or not
	 */
	public static boolean isValidInet4AddressFormat(String inet4Ip) {
		if (inet4Ip == null || inet4Ip.isEmpty()) {
			return false;
		}
		return _pattern.matcher(inet4Ip == null ? "" : inet4Ip).matches();
	}

	/**
	 * Regular expression value used to validate iP4v ip addresses.
	 */
	private static final Pattern _pattern = Pattern
			.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
					+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

	/**
	 * Returns a boolean indicating whether file in parameter url can be loaded. The
	 * contents of the file are returned in the buffer argument.
	 * 
	 * @param uri    file to be loaded
	 * @param buffer for file contents to be loaded into
	 * @param debug  indicates whether debug mode is on
	 * @return boolean indicating whether file contents were loaded into buffer
	 *         parameter successfully
	 */
	public static boolean loadFile(String uri, StringBuilder buffer, boolean debug) {

		File file = new File(uri);

		if (!file.exists() || !file.canRead()) {
			System.err.println("error: uri '" + uri + "' not accessible");
			return false;
		}

		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
			byte bytes[] = new byte[(int) file.length()];
			inputStream.read(bytes);
			String data = new String(bytes, StandardCharsets.UTF_8);
			inputStream.close();
			buffer.append(data);
			return true;

		} catch (FileNotFoundException e) {
			System.err.println("exception: " + e.getClass().getName() + " - " + e.getMessage());
			if (debug) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("exception: " + e.getClass().getName() + " - " + e.getMessage());
			if (debug) {
				e.printStackTrace();
			}
		}

		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			System.err.println("exception: " + e.getClass().getName() + " - " + e.getMessage());
			if (debug) {
				e.printStackTrace();
			}
		}

		return false;
	}
}
