package com.zuhlke.ta.sentiment.model;

public class User {
	final String userId;
	final String username;
	final String location;

	public User(String userId, String username, String location) {
		super();
		this.userId = userId;
		this.username = username;
		this.location = location;
	}

	public String getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
