package utils;

public class JsonId {
	private String old_id;
	private String new_id;

	public JsonId(String old_id, String new_id) {
		this.old_id = old_id;
		this.new_id = new_id;
	}

	public String getNew() {
		return new_id;
	}
	public String getOld() {
		return old_id;
	}
}
