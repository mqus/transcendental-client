package transcendental.client.lib;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.codec.binary.Base64;

import java.io.Reader;

/**
 * Created by markus on 28.01.17.
 */
public class Package {
	private Type type;

	private int clientID =0;

	private byte[] content;

	public enum Type{
		@SerializedName("Hello")
		HELLO,
		@SerializedName("Text")
		TEXT,
		@SerializedName("Copy")
		COPY,
		@SerializedName("Request")
		REQUEST,
		@SerializedName("Data")
		DATA,
		@SerializedName("Reject")
		REJECT
	}

	public Package(Type type, int clientID, byte[] content) {
		this.type = type;
		this.clientID = clientID;
		this.content = content;
	}

	public Type getType() {
		return type;
	}

	public int getClientID() {
		return clientID;
	}

	public byte[] getContent() {
		return content;
	}

/*
	public void setType(Type type) {
		this.type = type;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public void setContent(byte[] content) {
		this.content = encodeBytes(content);
	}
*/
}