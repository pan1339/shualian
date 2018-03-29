package com.dajing.bean;

public class SLAccount {
	private String username;
	private String uuid;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SLAccount() {
		super();
	}

	public SLAccount(String username, String password, String uuid) {
		super();
		this.username = username;
		this.uuid = uuid;
		this.password = password;
	}

	@Override
	public String toString() {
		return "SLAccount [username=" + username + ", uuid=" + uuid + ", password=" + password + "]";
	}

	public SLAccount(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public SLAccount(String username) {
		super();
		this.username = username;
	}

}
