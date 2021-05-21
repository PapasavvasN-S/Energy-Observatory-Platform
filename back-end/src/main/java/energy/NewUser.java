package energy;

public class NewUser {
	private String username;
	private int quota;
	private String email;
	private String datetime;
	private int used_quota;

	public NewUser(String username, String email, int quota, int used_quota, String datetime) {
		this.username = username;
		this.quota = quota;
		this.setEmail(email);
		this.setUsed_quota(used_quota);
		this.setDatetime(datetime);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	public int getUsed_quota() {
		return used_quota;
	}

	public void setUsed_quota(int used_quota) {
		this.used_quota = used_quota;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}