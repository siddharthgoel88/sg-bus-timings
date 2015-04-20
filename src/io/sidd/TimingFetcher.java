package io.sidd;

public abstract class TimingFetcher {
	
	public boolean isDataAvailable() {
		return false;
	}
	
	public boolean isBusRunning() {
		return false;
	}
	
	public String timeToBus() {
		return "Unknown";
	}

}
