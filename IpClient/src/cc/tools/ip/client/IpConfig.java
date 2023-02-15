package cc.tools.ip.client;

import java.util.*;

/**
 * This class manages program command line and configuration values
 * 
 * @author ccpgh
 * @version %I%, %G%
 * @since 0.1
 */
public class IpConfig {
	/**
	 * Constructor. This is private and should only by this class when creating a
	 * global singleton instance.
	 */
	private IpConfig() {
	}

	/**
	 * Displays table showing configured values
	 */
	public void dump() {
		System.out.println(new Date() + ": configuration");
		System.out.println("-protocol:         " + getProtocol());
		System.out.println("-hostname:         " + getHostname());
		System.out.println("-port:             " + getPort());
		System.out.println("-uri:              " + getURI());
		System.out.println("-certs:            " + getCerts());
		System.out.println("-debug:            " + (getDebug() ? "true" : "false"));
	}

	/**
	 * Displays usage help information for command line options
	 */
	public void doHelp() {
		System.out.println("usage:");
		System.out.println(
				" [-protocol (http|https)] -hostname (ip|domain) [-port <port>] [-uri <path>] -certs <dir> [-debug]");
		System.out.println("-protocol: optional.  defaults to https.");
		System.out.println("-hostname: mandatory. server host. can be an ip address or name.");
		System.out.println("-port:     optional.  server port. defaults to 80/443 based on protocol.");
		System.out.println("-uri:      optional.  url prefix to Ip server endpoints. defaults to '/tomcat/server/ip'.");
		System.out.println("-certs:    mandatory. path to client's private.crt and remote server's public.key files.");
		System.out.println("-debug:    optional.  flag to set debug mode. defaults to false.");
	}

	/*
	 * Loads configuration from file Returns boolean indicating result of loading
	 * and processing command line arguments
	 * 
	 * @param args String array containing command line arguments
	 * 
	 * @return boolean indicating whether load was successful.
	 */
	public boolean load(String[] args) {
		Map<String, String> values = new HashMap<String, String>();

		if (!transformArgs(args, values)) {
			return false;
		}

		if (!setProtocol(values) | !setHostname(values) | !setPort(values) | !setURI(values) | !setCerts(values)
				| !setDebug(values)) {
			return false;
		}
		
		String[] names = { "protocol", "hostname", "port", "uri", "certs", "debug" };
		for (String name : names ) {
			values.remove(name);
		}

		if (!values.keySet().isEmpty()) {
			for (String key : values.keySet() ) {
				_errors.add("invalid parameter '" + key + "'");
			}
			return false;
		}
		
		_isValid = true;
		return true;
	}

	/*
	 * Returns boolean indicating whether args variable contains the help command
	 * line indicator '-h'
	 * 
	 * @param args String array containing command line arguments
	 * 
	 * @return boolean indicating whether help command line option has been
	 * requested
	 */
	public boolean checkIsHelp(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].compareToIgnoreCase("-h") == 0) {
				_isHelp = true;
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns boolean indicating argument transformation succeeded or not. values
	 * in command line arguments 'args' variable will be transformed into name/value
	 * pairs and deposited in results.
	 * 
	 * @param args String array containing command line arguments
	 * 
	 * @param results Array within which transformed results will be placed
	 * 
	 * @return boolean indicating argument transformation worked
	 */
	public boolean transformArgs(String[] args, Map<String, String> results) {
		String name = null;
		for (int i = 0; i < args.length; i++) {
			String token = args[i].trim();
			if (name == null || (!token.isEmpty() && token.charAt(0) == '-')) {
				token = token.toLowerCase();
				if (token.length() < 2) {
					_errors.add("empty value at position " + Integer.toString(i) + " '" + args[i] + "'");
					return false;
				}
				if (token.charAt(0) != '-') {
					_errors.add("ill-formed value at position " + Integer.toString(i) + " '" + args[i] + "'");
					return false;
				}
				name = token.substring(1);
				if (results.containsKey(name)) {
					_errors.add("duplicate value at position " + Integer.toString(i) + " '" + args[i] + "'");
					return false;
				}
				if (!IpCommon.validToken(name, _errors)) {
					_errors.add("invalid name at position " + Integer.toString(i) + " '" + args[i] + "'");
					return false;
				}
				results.put(name, "");
			} else {
				if (!IpCommon.validToken(token, _errors)) {
					_errors.add("invalid name at position " + Integer.toString(i) + " '" + args[i] + "'");
					return false;
				}
				results.put(name, token);
				name = null;
			}
		}
		return true;
	}

	/*
	 * Returns IpConfig singleton
	 * 
	 * @return IpConfig containing current configuration. @Ref(load) must be called
	 * first.
	 */
	public static IpConfig getIpConfig() {
		return IpConfig._ipConfig;
	}

	/*
	 * Returns List containing configuration processing errors.
	 * 
	 * @return List<String> containing errors raised during command line 
	 * parameter processing.
	 */
	public List<String> getErrors() {
		List<String> errors = new ArrayList<String>();
		errors.addAll(_errors);
		return errors;
	}

	/*
	 * Returns isHelp value
	 * 
	 * @return boolean indicating whether help command line option is present
	 */
	public boolean getIsHelp() {
		return _isHelp;
	}

	/*
	 * Returns isValid value
	 * 
	 * @return boolean indicating whether configuration is valid or not
	 */
	public boolean getIsValid() {
		return _isValid;
	}

	/*
	 * Returns -protocol configuration value
	 * 
	 * @return String containing configured -protocol value
	 */
	public String getProtocol() {
		return _protocol;
	}

	/*
	 * Returns -hostname configuration value
	 * 
	 * @return String containing configured -hostname value
	 */
	public String getHostname() {
		return _hostname;
	}

	/*
	 * Returns -port configuration value
	 * 
	 * @return int containing configured -port value
	 */
	public int getPort() {
		return _port;
	}

	/*
	 * Returns -uri configuration value
	 * 
	 * @return String containing configured -uri value
	 */
	public String getURI() {
		return _uri;
	}

	/*
	 * Returns -certs configuration value
	 * 
	 * @return String containing configured -certs value
	 */
	public String getCerts() {
		return _certs;
	}

	/*
	 * Returns url for srever get endpoint
	 * 
	 * @return String containing 'get' endpoint url value
	 */
	public String getGetUrl() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getBaseUrl());
		buffer.append("/get");
		return buffer.toString();
	}

	/*
	 * Returns url for srever set endpoint
	 * 
	 * @return String containing 'set' endpoint url value
	 */
	public String getSetUrl() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getBaseUrl());
		buffer.append("/set");
		return buffer.toString();
	}

	/*
	 * Returns debug value
	 * 
	 * @return boolean indicating whether debug mode is on or not.
	 */
	public boolean getDebug() {
		return _debug;
	}

	/*
	 * Returns target Url base value
	 * 
	 * @return String containing base url
	 */
	private String getBaseUrl() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getProtocol());
		buffer.append("://");
		buffer.append(getHostname());
		buffer.append(":");
		buffer.append(getPort());
		buffer.append(getURI());
		return buffer.toString();
	}

	/*
	 * Setter function for parameter -protocol Optional parameter. Can be http or
	 * https
	 * 
	 * @return boolean indicating success or fail
	 */
	private boolean setProtocol(Map<String, String> values) {
		String protocol = null;
		if (values.containsKey("protocol")) {
			protocol = values.get("protocol");
			if (protocol.compareTo("http") != 0 && protocol.compareTo("https") != 0) {
				_errors.add("-protocol must be value 'http' or 'https'");
				return false;
			}
		} else {
			protocol = "https";
			values.put("protocol", protocol);
		}

		_protocol = protocol;
		return true;
	}

	/*
	 * Setter function for parameter -hostname Mandatory parameter. Can be ip
	 * address or domain name
	 * 
	 * @return boolean indicating success or fail
	 */
	private boolean setHostname(Map<String, String> values) {
		if (values.containsKey("hostname")) {
			_hostname = values.get("hostname");
			return true;
		}

		_errors.add("mandatory parameter -hostname missing");
		return false;
	}

	/*
	 * Setter function for parameter -port Port must be more than 0 Optional
	 * parameter Port defaults to default port for protocol setting - either 80 or
	 * 443
	 * 
	 * @return boolean indicating success or fail
	 */
	private boolean setPort(Map<String, String> values) {
		int port = _protocol.compareTo("http") == 0 ? 80 : (_protocol.compareTo("https") == 0 ? 443 : -1);

		if (values.containsKey("port")) {
			try {
				port = Integer.parseInt(values.get("port"));
			} catch (NumberFormatException e) {
				_errors.add("-port is invalid number");
				return false;
			}
		} else {
			values.put("port", Integer.toString(port));
		}

		_port = port;
		if (_port < 0) {
			_errors.add("-port is not a valid port number");
			return false;
		}

		return true;
	}

	/*
	 * Setter function for parameter -uri Optional parameter
	 * 
	 * @return boolean indicating success or fail
	 */
	private boolean setURI(Map<String, String> values) {
		String uri = null;
		if (values.containsKey("uri")) {
			uri = values.get("uri");
		} else {
			uri = "/tomcat/server/ip";
			values.put("uri", uri);
		}

		_uri = uri;
		return true;
	}

	/*
	 * Setter function for parameter -certs Mandatory parameter
	 * 
	 * @return boolean indicating success or fail
	 */
	private boolean setCerts(Map<String, String> values) {
		if (values.containsKey("certs")) {
			_certs = values.get("certs");
			return true;
		}

		_errors.add("mandatory parameter -certs missing");
		return false;
	}

	/*
	 * Setter function for parameter -debug Mandatory parameter
	 * 
	 * @return boolean indicating success or fail
	 */
	private boolean setDebug(Map<String, String> values) {
		if (values.containsKey("debug")) {
			String value = values.get("debug");
			if (!(value == null || value.isBlank() || value.isEmpty())) {
				_errors.add("flag parameter -debug should not have a value");
				return false;
			}
			_debug = true;
			return true;
		}

		_debug = false;
		return true;
	}

	/*
	 * Singleton IpConfig value
	 */
	private static IpConfig _ipConfig = new IpConfig();

	/*
	 * Flag indicating whether help has been requested.
	 */
	private boolean _isHelp = false;

	/*
	 * Flag indicating whether configuration is valid or not.
	 */
	private boolean _isValid = false;

	/**
	 * Contains a list of errors encountered during processing of command line
	 * values
	 */
	private List<String> _errors = new ArrayList<String>();

	/*
	 * The connection protocol - either http or https.
	 */
	private String _protocol = new String();

	/*
	 * The server name that the server is running on. Could be an Ip address (Ip4
	 * only) or domain name.
	 */
	private String _hostname = new String();

	/*
	 * The port this server should be contacted on.
	 */
	private int _port = -1;

	/*
	 * The servlet Uri value
	 */
	private String _uri = new String();

	/**
	 * The location of the private client and public server security certificates
	 */
	private String _certs = new String();

	/*
	 * Flag indicating whether debug mode is on or not.
	 */
	private boolean _debug = false;

}
