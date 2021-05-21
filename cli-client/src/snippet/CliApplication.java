package snippet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
//import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.io.*;
import java.util.Arrays;
import java.util.*;
import java.lang.*;
//import java.net.http.HttpRequest.BodyPublishers;

public class CliApplication {

	public static List<String> correctInput(List<String> args) {
		int args1 = 0;
		int args2 = 0;
		int lastarg = 0;
		String temp = "";
		int i;
		List<String> newarguments = new ArrayList<String>();
		for (i = 0; i < args.size(); i++)
			if (!args.get(i).contains("--"))
				newarguments.add(args.get(i));
			else
				break;

		for (i = 0; i < args.size(); i++) {
			if (args.get(i).contains("--")) {
				args1 = i;
				lastarg = i;
				newarguments.add(args.get(i));
				for (int j = i + 1; j < args.size(); j++)
					if (args.get(j).contains("--")) {
						args2 = j;
						break;
					}
			}
			temp = "";
			boolean firstime = true;
			for (int r = args1 + 1; r < args2; r++) {
				if (firstime) {
					temp = args.get(r);
					firstime = false;
				} else
					temp = temp + "%20" + args.get(r);
			}
			if (!temp.equals(""))
				newarguments.add(temp);
			args1 = 0;
			args2 = 0;
		}

		temp = "";
		boolean firstime = true;
		if (lastarg != 0) {
			for (int r = lastarg + 1; r < args.size(); r++) {
				if (firstime) {
					temp = args.get(r);
					firstime = false;
				} else
					temp = temp + "%20" + args.get(r);
			}
			if (!temp.equals(""))
				newarguments.add(temp);
		}
		return newarguments;
	}

	public static boolean lookFor(List<String> args, int pointer, String specarg) {
		int i;
		boolean found = false;
		for (i = pointer; i < args.size(); i++)
			if (args.get(i).equals(specarg))
				found = true;
		if (found)
			return true;
		else
			return false;
	}

	public static boolean email_check(String email) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
		Matcher mat = pattern.matcher(email);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean source_check(String source) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%/\\:+-]+.csv");
		Matcher mat = pattern.matcher(source);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean quotas_check(String quotas) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher mat = pattern.matcher(quotas);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean username_check(String username) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9]*");
		Matcher mat = pattern.matcher(username);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean datacheck_check(String data) {
		if (data.equals("ActualTotalLoad") | data.equals("AggregatedGenerationPerType")
				| data.equals("DayAheadTotalLoadForecast"))
			return true;
		else
			return false;
	}

	public static boolean date_check(String date, String datetype) {
		switch (datetype) {
			case "--date": {
				Pattern pattern = Pattern
						.compile("^((2000|2400|2800|(19|2[0-9](0[48]|[2468][048]|[13579][26])))-02-29)$"
								+ "|^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$"
								+ "|^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$"
								+ "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$");
				Matcher mat = pattern.matcher(date);
				if (mat.matches())
					return true;
				else
					return false;

			}
			case "--month": {
				Pattern pattern2 = Pattern.compile("^(((19|2[0-9])[0-9]{2})-(0[1-9]|1[012]))$");
				Matcher mat2 = pattern2.matcher(date);
				if (mat2.matches())
					return true;
				else
					return false;

			}
			case "--year": {
				Pattern pattern3 = Pattern.compile("^((19|2[0-9])[0-9]{2})$");
				Matcher mat3 = pattern3.matcher(date);
				if (mat3.matches())
					return true;
				else
					return false;

			}
			default:
				return false;
		}
	}

	public static boolean timeres_check(String timeres) {
		if (timeres.equals("PT15M") | timeres.equals("PT60M") | timeres.equals("PT30M"))
			return true;
		else
			return false;
	}

	public static boolean format_check(String format) {
		if (format.equals("json") | format.equals("csv"))
			return true;
		else
			return false;
	}

	public static boolean pass_check(String pass) {
		int len = pass.length();
		for (int i = 0; i < len; i++) {
			if (pass.charAt(i) == ' ') {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		File file = new File("softeng19bAPI.token");
		if (file.exists()) {
			file.delete();
		}
		while (true) {
			System.out.print("\n$");
			URL url;
			String time = " ";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String input = reader.readLine();
			String[] argus = input.split(" ");
			List<String> argulist = Arrays.asList(argus);
			List<String> arguments = correctInput(argulist);
			String base_url = "http://localhost:8765/energy/api/";
			if ((arguments.size() >= 1) && (arguments.get(0).equals("energy_group16"))) {
				if (arguments.size() >= 2) {
					switch (arguments.get(1)) {
						case "Admin": {
							if (arguments.size() >= 3) {
								switch (arguments.get(2)) {
									case "--newuser": {
										if ((arguments.size() >= 4) && username_check(arguments.get(3))) {
											if ((arguments.size() >= 5) && arguments.get(4).equals("--passw")) {
												if ((arguments.size() >= 6) && pass_check(arguments.get(5))) {
													if ((arguments.size() >= 7) && arguments.get(6).equals("--email")) {
														if ((arguments.size() >= 8) && email_check(arguments.get(7))) {
															if ((arguments.size() >= 9)
																	&& arguments.get(8).equals("--quota")) {
																if ((arguments.size() >= 10)
																		&& quotas_check(arguments.get(9))) {
																	String urlParameters = "username="
																			+ arguments.get(3) + "&password="
																			+ arguments.get(5) + "&email="
																			+ arguments.get(7) + "&quota="
																			+ arguments.get(9);
																	byte[] postData = urlParameters
																			.getBytes(StandardCharsets.UTF_8);
																	int postDataLength = postData.length;
																	base_url += arguments.get(1) + "/users";
																	url = new URL(base_url);
																	//System.out.println(url);
																	HttpURLConnection con = (HttpURLConnection) url
																			.openConnection();
																	con.setRequestMethod("POST");
																	file = new File("softeng19bAPI.token");
																	if (file.exists()) {
																		BufferedReader br = new BufferedReader(
																				new FileReader(file));
																		String token;
																		token = br.readLine();
																		con.setRequestProperty("X-OBSERVATORY-AUTH",
																				token);
																		br.close();
																	}
																	con.setRequestProperty("Content-Length",
																			Integer.toString(postDataLength));
																	con.setUseCaches(false);
																	con.setDoOutput(true);
																	try (DataOutputStream wr = new DataOutputStream(
																			con.getOutputStream())) {
																		wr.write(postData);
																	}
																	String response = "";
																	response = FullResponseBuilder.getFullResponse(con);
																	System.out.println(response);
																} else
																	System.out.println(
																			"Invalid Quotas. Quotas should be integer\n");
															} else
																System.out.println(
																		"Missing requested parameter: --quota\n");
														} else
															System.out.println(
																	"Invalid email. Email should be ****@****.****\n");
													} else
														System.out.println("Missing requested parameter: --email\n");
												} else
													System.out.println("Invalid Password. No space allowed\n");
											} else
												System.out.println("Missing requested parameter: --passw\n");
										} else
											System.out.println(
													"Invalid Username. Use only latin characters or numbers\n");
										break;
									}
									case "--moduser": {
										if ((arguments.size() >= 4) && username_check(arguments.get(3))) {
											if ((arguments.size() >= 5) && arguments.get(4).equals("--passw")) {
												if ((arguments.size() >= 6) && pass_check(arguments.get(5))) {
													if ((arguments.size() >= 7) && arguments.get(6).equals("--email")) {
														if ((arguments.size() >= 8) && email_check(arguments.get(7))) {
															if ((arguments.size() >= 9)
																	&& arguments.get(8).equals("--quota")) {
																if ((arguments.size() >= 10)
																		&& quotas_check(arguments.get(9))) {
																	String urlParameters = arguments.get(5) + ","
																			+ arguments.get(7) + "," + arguments.get(9);
																	byte[] postData = urlParameters
																			.getBytes(StandardCharsets.UTF_8);
																	int postDataLength = postData.length;
																	base_url += arguments.get(1) + "/users/"
																			+ arguments.get(3);
																	url = new URL(base_url);
																	//System.out.println(url);
																	HttpURLConnection con = (HttpURLConnection) url
																			.openConnection();
																	con.setRequestMethod("PUT");
																	file = new File("softeng19bAPI.token");
																	if (file.exists()) {
																		BufferedReader br = new BufferedReader(
																				new FileReader(file));
																		String token;
																		token = br.readLine();
																		con.setRequestProperty("X-OBSERVATORY-AUTH",
																				token);
																		br.close();
																	}
																	con.setRequestProperty("Content-Length",
																			Integer.toString(postDataLength));
																	con.setRequestProperty("Content-Type",
																			"application/json");
																	con.setFixedLengthStreamingMode(postDataLength);
																	con.setUseCaches(false);
																	con.setDoOutput(true);
																	try (DataOutputStream wr = new DataOutputStream(
																			con.getOutputStream())) {
																		wr.write(postData);
																	}
																	String response = "";
																	response = FullResponseBuilder.getFullResponse(con);
																	System.out.println(response);
																} else
																	System.out.println(
																			"Invalid Quotas. Quotas should be integer\n");
															} else
																System.out.println(
																		"Missing requested parameter: --quota\n");
														} else
															System.out.println(
																	"Invalid email. Email should be ****@****.****\n");
													} else
														System.out.println("Missing requested parameter: --email\n");
												} else
													System.out.println("Invalid Password. No space allowed\n");
											} else
												System.out.println("Missing requested parameter: --passw\n");
										} else
											System.out.println(
													"Invalid Username. Use only latin characters or numbers\n");
										break;
									}
									// System.out.println("Missing requested parameter: --newuser");
									case "--userstatus": {
										if ((arguments.size() >= 4) && username_check(arguments.get(3))) {
											base_url += arguments.get(1) + "/users/" + arguments.get(3);
											url = new URL(base_url);
											//System.out.println(url);
											HttpURLConnection con = (HttpURLConnection) url.openConnection();
											con.setRequestMethod("GET");
											file = new File("softeng19bAPI.token");
											if (file.exists()) {
												BufferedReader br = new BufferedReader(new FileReader(file));
												String token;
												token = br.readLine();
												con.setRequestProperty("X-OBSERVATORY-AUTH", token);
												br.close();
											}
											String response = "";
											response = FullResponseBuilder.getFullResponse(con);
											System.out.print(response);
											con.disconnect();
										} else
											System.out.println(
													"Invalid Username. Use only latin characters or numbers\n");
										break;
									}
									case "--newdata": {
										if ((arguments.size() >= 4) && datacheck_check(arguments.get(3))) {
											if (arguments.size() >= 5 && arguments.get(4).equals("--source")) {
												if (arguments.size() >= 6 && new File (arguments.get(5)).exists()){ //&& source_check(arguments.get(5))) {
													base_url += arguments.get(1) + "/" + arguments.get(3);
													String charset = "UTF-8";
													File uploadFile = new File(arguments.get(5));
													MultipartUtility multipart = new MultipartUtility(base_url,
															charset);
													multipart.addFilePart("fileUpload", uploadFile);

													List<String> response = multipart.finish();
													for (String line : response) {
														System.out.println(line);
													}
												} else
													System.out.print("Invalid csv filename or file doesn't exist");
											} else
												System.out.print("Missing requested parameter: --source");
										} else
											System.out.print(
													"Invalid Data type. Should be { ActualTotalLoad | AggregatedGenerationPerType | DayAheadTotalLoadForecast }");
										break;
									}
									default:
										System.out.print(
												"Missing requested argument {--newuser | --moduser | --userstatus | --newdata }");
										break;
								}
							} else
								System.out.print(
										"Missing requested argument {--newuser | --moduser | --userstatus | --newdata }");
							break;
						}
						case "ActualvsForecast":
						case "DayAheadTotalLoadForecast":
						case "ActualTotalLoad": {
							if (((arguments.size() >= 3) && arguments.get(2).equals("--area"))) {
								if (arguments.size() >= 4) {
									if (((arguments.size() >= 5) && arguments.get(4).equals("--timeres"))) {
										if ((arguments.size() >= 6) && timeres_check(arguments.get(5))) {
											if (arguments.size() >= 7) {
												switch (arguments.get(6)) {
													case "--date":
														time = "date";
														break;
													case "--month":
														time = "month";
														break;
													case "--year":
														time = "year";
														break;
													default:
														System.out.println(
																"Missing requested parameter: [--date | --month | --year]\n");
												}
												if ((arguments.size() >= 8)
														&& date_check(arguments.get(7), arguments.get(6))) {
													if (arguments.size() >= 9)
														if (arguments.get(8).equals("--format"))
															if (arguments.size() == 10
																	&& format_check(arguments.get(9)))
																base_url += arguments.get(1) + "/" + arguments.get(3)
																		+ "/" + arguments.get(5) + "/" + time + "/"
																		+ arguments.get(7) + "?format="
																		+ arguments.get(9);
															else {
																System.out.println(
																		"Invalid format type. Should be { csv | json (default) }\n");
																break;
															}
														else {
															System.out.println("Missing parameter: --format\n");
															break;
														}
													else
														base_url += arguments.get(1) + "/" + arguments.get(3) + "/"
																+ arguments.get(5) + "/" + time + "/" + arguments.get(7)
																+ "?format=json";
													url = new URL(base_url);
													//System.out.println(url);
													HttpURLConnection con = (HttpURLConnection) url.openConnection();
													con.setRequestMethod("GET");
													file = new File("softeng19bAPI.token");
													if (file.exists()) {
														BufferedReader br = new BufferedReader(new FileReader(file));
														String token;
														token = br.readLine();
														con.setRequestProperty("X-OBSERVATORY-AUTH", token);
														br.close();
													}
													String response = "";
													response = FullResponseBuilder.getFullResponse(con);
													System.out.print(response);
												} else
													System.out.println("Invalid date. Should be YYYY-MM-DD\n");
											} else
												System.out.println(
														"Missing requested parameter: [--date | --month | --year]\n");
										} else
											System.out.println(
													"Invalid value for timeres. Should be { PT15M | PT30M | PT60M }\n");
									} else
										System.out.println("Missing requested parameter: --timeres\n");
								} else
									System.out.println("Missing area value \n");

							} else
								System.out.println("Missing requested parameter: --area\n");
							break;
						}
						case "AggregatedGenerationPerType": {
							if (((arguments.size() >= 3) && arguments.get(2).equals("--area"))) {
								if (arguments.size() >= 4) {
									if (((arguments.size() >= 5) && arguments.get(4).equals("--timeres"))) {
										if ((arguments.size() >= 6) && timeres_check(arguments.get(5))) {
											if (((arguments.size() >= 7)
													&& arguments.get(6).equals("--productiontype"))) {
												if (arguments.size() >= 8) {
													if (arguments.size() >= 9) {
														switch (arguments.get(8)) {
															case "--date":
																time = "date";
																break;
															case "--month":
																time = "month";
																break;
															case "--year":
																time = "year";
																break;
															default:
																System.out.println(
																		"Missing requested parameter: [--date | --month | --year]\n");
														}
														if ((arguments.size() >= 10)
																&& date_check(arguments.get(9), arguments.get(8))) {
															if (arguments.size() >= 11)
																if (arguments.get(10).equals("--format"))
																	if (arguments.size() == 12
																			&& format_check(arguments.get(11)))
																		base_url += arguments.get(1) + "/"
																				+ arguments.get(3) + "/"
																				+ arguments.get(7).replace(" ", "%20") + "/" + arguments.get(5)
																				+ "/" + time + "/" + arguments.get(9)
																				+ "?format=" + arguments.get(11);
																	else {
																		System.out.println(
																				"Invalid format type. Should be { csv | json (default) }\n");
																		break;
																	}
																else {
																	System.out.println("Missing parameter: --format\n");
																	break;
																}
															else
																base_url += arguments.get(1) + "/" + arguments.get(3)
																		+ "/" + arguments.get(7) + "/"
																		+ arguments.get(5) + "/" + time + "/"
																		+ arguments.get(9) + "?format=json";
															url = new URL(base_url);
															//System.out.println(url);
															HttpURLConnection con = (HttpURLConnection) url
																	.openConnection();
															con.setRequestMethod("GET");
															file = new File("softeng19bAPI.token");
															if (file.exists()) {
																BufferedReader br = new BufferedReader(
																		new FileReader(file));
																String token;
																token = br.readLine();
																con.setRequestProperty("X-OBSERVATORY-AUTH", token);
																br.close();
															}
															String response = "";
															response = FullResponseBuilder.getFullResponse(con);
															System.out.print(response);
														} else
															System.out.println("Invalid date. Should be YYYY-MM-DD\n");
													} else
														System.out.println(
																"Missing requested parameter: [--date | --month | --year]\n");
												} else
													System.out.println("Missing production type value \n");
											} else
												System.out.println("Missing requester parameter: --productiontype\n");
										} else
											System.out.println(
													"Invalid value for timeres. Should be { PT15M | PT30M | PT60M }\n");

									} else
										System.out.println("Missing requested parameter: --timeres\n");
								} else
									System.out.println("Missing area value \n");

							} else
								System.out.println("Missing requested parameter: --area\n");
							break;
						}
						case "HealthCheck": {
							base_url += arguments.get(1);
							url = new URL(base_url);
							//System.out.println(url);
							HttpURLConnection con = (HttpURLConnection) url.openConnection();
							con.setRequestMethod("GET");
							String response = "";
							response = FullResponseBuilder.getFullResponse(con);
							System.out.print(response);
							break;
						}
						case "Reset": {
							base_url += arguments.get(1);
							url = new URL(base_url);
							//System.out.println(url);
							HttpURLConnection con = (HttpURLConnection) url.openConnection();
							con.setRequestMethod("POST");
							String response = "";
							response = FullResponseBuilder.getFullResponse(con);
							System.out.print(response);
							break;
						}
						case "Login": {
							if (((arguments.size() >= 4) && arguments.get(2).equals("--username"))) {
								if (((arguments.size() >= 6) && arguments.get(4).equals("--passw"))) {
									base_url += arguments.get(1);
									url = new URL(base_url);
									//System.out.println(url);
									String urlParameters = "&username=" + arguments.get(3) + "&password="
											+ arguments.get(5);
									byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
									int postDataLength = postData.length;
									HttpURLConnection con = (HttpURLConnection) url.openConnection();
									con.setRequestMethod("POST");
									con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
									con.setUseCaches(false);
									con.setDoOutput(true);
									try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
										wr.write(postData);
									}
									String response = "";
									response = FullResponseBuilder.getFullResponse(con);
									System.out.print(response);
								} else
									System.out.println("Missing requester parameter: --passw\n");
							} else
								System.out.println("Missing requested parameter: --username\n");
							break;
						}
						case "Logout": {
							base_url += arguments.get(1);
							url = new URL(base_url);
							//System.out.println(url);
							HttpURLConnection con = (HttpURLConnection) url.openConnection();
							con.setRequestMethod("POST");
							file = new File("softeng19bAPI.token");
							if (file.exists()) {
								BufferedReader br = new BufferedReader(new FileReader(file));
								String token;
								token = br.readLine();
								con.setRequestProperty("X-OBSERVATORY-AUTH", token);
								br.close();
							}
							String response = "";
							response = FullResponseBuilder.getFullResponse(con);
							System.out.print(response);
							break;
						}
						default:
							System.out.print("Missing SCOPE\n");
					}
				} else
					System.out.print("Missing SCOPE\n");
			} else
				System.out.print("Missing energy_group16 \n");
		}

	}
}