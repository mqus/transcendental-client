package transcendental.client.lib;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.codec.binary.Base64;

import java.io.Reader;

/**
 * Created by markus on 28.01.17.
 */
public class Package {
	@SerializedName("Type")
	private Type type;

	@SerializedName("ClientID")
	private int clientID =0;

	@SerializedName("Content")
	private String content="kjkj";

	public enum Type{
		@SerializedName("Hello")
		HELLO,
		@SerializedName("Copy")
		COPY,
		@SerializedName("Request")
		REQUEST,
		@SerializedName("Data")
		DATA,
		@SerializedName("Reject")
		REJECT
/*
		@SerializedName("Hello")
		HELLO("Hello"),
		@SerializedName("Copy")
		COPY("Copy"),
		@SerializedName("Request")
		REQUEST("Request"),
		@SerializedName("Data")
		DATA("Data"),
		@SerializedName("Reject")
		REJECT("Reject");

		private final String str;


		Type(String str) {
			this.str=str;
		}

		public String toString(){
			return this.str;
		}*/

	}

	public Package(Type type, String content) {
		this.type = type;
		this.content = content;
	}

	public Package(Type type, byte[] content) {
		this.type = type;
		this.content = encodeBytes(content);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	static public byte[] createHelloPackage(byte[] room){
		Package pkg = new Package(Type.HELLO,room);
		return pkg.encode();
	}

	static public byte[] createCopyPackage(byte[] flavours){
		Package pkg = new Package(Type.COPY,flavours);
		return pkg.encode();
	}

	static public byte[] createRequestPackage(byte[] flavour, int to){
		Package pkg = new Package(Type.REQUEST,flavour);
		pkg.setClientID(to);
		return pkg.encode();
	}

	static public byte[] createDataPackage(byte[] data, int to){
		Package pkg = new Package(Type.DATA,data);
		pkg.setClientID(to);
		return pkg.encode();
	}

	static public byte[] createRejectPackage(byte[] rejectedFlavour, int to){
		Package pkg = new Package(Type.REJECT,rejectedFlavour);
		pkg.setClientID(to);
		return pkg.encode();
	}


	static Gson g=new Gson();
	static public Package decode(Reader input){
		return g.fromJson(input,Package.class);
	}

	static private String encodeBytes(byte[] bytes){
		return Base64.encodeBase64String(bytes);
	}

	public void encode(Appendable writer){
		g.toJson(this,writer);
	}
	private byte[] encode(){
		String s=g.toJson(this);
		return s.getBytes();
		//return (s+"\n").getBytes();
	}
}