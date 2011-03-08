package stubs;

public class ValueIncrementer {
	private int value;
    
	public ValueIncrementer(int value) {
		this.value = value;
	}
    
	public int get() {
		int ret = value;
		++value;
		return ret;
	}
}
