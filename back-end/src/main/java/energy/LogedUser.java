package energy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogedUser {
	private String username;
	private int quota;
	private String token;    
	private String datetime;
    private int used_quota;
    private boolean isAdmin=false;

	public LogedUser(String username, String token, int quota, int used_quota, Date datetime) {
        this.username = username;
        this.token = token;
		this.quota = quota;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        int curr_year = Integer.parseInt(dateFormat.format(date).substring(0, 4));
        int curr_month = Integer.parseInt(dateFormat.format(date).substring(5, 7));
        int curr_day = Integer.parseInt(dateFormat.format(date).substring(8, 10));

        int year = Integer.parseInt(dateFormat.format(datetime).substring(0, 4));
        int month = Integer.parseInt(dateFormat.format(datetime).substring(5, 7));
        int day = Integer.parseInt(dateFormat.format(datetime).substring(8, 10));

        if (curr_year > year)
            this.used_quota = 0;
        else if ((curr_year == year) && (curr_month > month))
            this.used_quota = 0;
        else if ((curr_year == year) && (curr_month == month) && (curr_day > day))
            this.used_quota = 0;
        else
            this.used_quota = used_quota;

        this.datetime = curr_year + "/" + curr_month + "/" + curr_day;

        if (username.equals("admin"))
            isAdmin = true;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getUsed_quota() {
        return used_quota;
    }

    public void setUsed_quota(int used_quota) {
        this.used_quota = used_quota;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}