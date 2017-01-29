package transcendental.client.lib;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.codec.binary.Base64;

import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Created by markus on 29.01.17.
 */
public class Packager {
	private byte[] pass;
	private static Gson g = new Gson();

	public Packager(String pass) {
		try {
			this.pass = pass.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Util.ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
		}
	}

	public void setPass(String pass) {
		try {
			this.pass = pass.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Util.ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
		}
	}

	public byte[] packHello() {
		return pack(Package.Type.HELLO, pass);
	}

	public byte[] packCopy(byte[] flavors) {
		return pack(Package.Type.COPY, flavors);
	}

	public byte[] packText(byte[] text) {
		return pack(Package.Type.TEXT, text);
	}

	public byte[] packRequest(byte[] flavor, int to) {
		return pack(Package.Type.REQUEST, to, flavor);
	}

	public byte[] packData(byte[] data, int to) {
		return pack(Package.Type.DATA, to, data);
	}

	public byte[] packReject(byte[] availableFlavors, int to) {
		return pack(Package.Type.REJECT, to, availableFlavors);
	}

	private byte[] pack(Package.Type type, byte[] content) {
		SerializablePackage pkg = new SerializablePackage(type, content);
		return serialize(pkg);
	}

	private byte[] pack(Package.Type type, int clientID, byte[] content) {
		SerializablePackage pkg = new SerializablePackage(type, clientID, content);
		return serialize(pkg);
	}

	class SerializablePackage {
		@SerializedName("Type")
		private Package.Type type;

		@SerializedName("ClientID")
		private int clientID = 0;

		@SerializedName("Content")
		private String content;

		public SerializablePackage(Package.Type type, byte[] content) {
			this.type = type;
			this.content = encodeBytes(encrypt(content));
		}

		public SerializablePackage(Package.Type type, int clientID, byte[] content) {
			this.type = type;
			this.clientID = clientID;
			this.content = encodeBytes(encrypt(content));
		}

		public Package.Type getType() {
			return type;
		}

		public int getClientID() {
			return clientID;
		}

		public byte[] getContent() {
			return decrypt(decodeBytes(content));
		}

	}

	// converting methods
	private byte[] encrypt(byte[] cleartext) {
		//TODO
		return cleartext;
	}

	private byte[] decrypt(byte[] cryptotext) {
		//TODO
		return cryptotext;
	}

	private static String encodeBytes(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	private static byte[] decodeBytes(String encoded) {
		return Base64.decodeBase64(encoded);
	}

	private static byte[] serialize(SerializablePackage pkg) {
		String s = g.toJson(pkg);
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Util.ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
			//is not reached
			return new byte[0];
		}
	}

	public Package deserialize(Reader input) {

		SerializablePackage pkg = g.fromJson(input, SerializablePackage.class);

		return new Package(pkg.getType(), pkg.getClientID(), pkg.getContent());
	}

}

