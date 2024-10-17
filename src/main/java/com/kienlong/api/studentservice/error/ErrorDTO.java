package com.kienlong.api.studentservice.error;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorDTO {
	private Date timestamp;
	private int status;
	private List<String> error = new ArrayList<>();
	private String path;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getError() {
		return error;
	}

	public void setError(List<String> error) {
		this.error = error;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void addError(String message) {
		error.add(message);
	}

}
