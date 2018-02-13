package transcendental.client.lib;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by markus on 29.01.17.
 */
public class Packager {

	private static Gson g = new Gson();
	private Cipher encryptor, decryptor;

	public Packager(String pass) throws InvalidKeyException {
		setPass(pass);
	}

	private void setPass(String pass) throws InvalidKeyException {
		try {
			//MAYBE: change to CBC
			byte[] key = pass.getBytes("UTF-8");
			encryptor = Cipher.getInstance("AES/ECB/PKCS5Padding");
			decryptor = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			encryptor.init(Cipher.ENCRYPT_MODE, sks);
			decryptor.init(Cipher.DECRYPT_MODE, sks);
		} catch(UnsupportedEncodingException e) {
			Util.ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
		} catch(NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

	public byte[] packHello() {
		return pack(Package.Type.HELLO, "ROOM".getBytes());
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

	public Package deserialize(Reader input) throws JsonIOException, JsonSyntaxException {
		JsonReader jr = new JsonReader(input);

		SerializablePackage pkg_rootless = g.fromJson(jr, SerializablePackage.class);
		//if the connection was interupted, pkg_rootless is null, return that null
		if (pkg_rootless == null)
			return null;

		//Assign this Packager to pkg (through reinitialization):
		SerializablePackage pkg = new SerializablePackage(pkg_rootless);

		try {
			return new Package(pkg.getType(), pkg.getClientID(), pkg.getContent());
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return Package.BAD_PACKAGE;
		}
	}

	// converting methods
	private byte[] encrypt(byte[] cleartext) {
		try {
			//return cleartext;//FIdXME
			return encryptor.doFinal(cleartext);
		} catch(IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

	private byte[] decrypt(byte[] cryptotext) throws BadPaddingException {
		try {
			//	return cryptotext;//FIdXME
			return decryptor.doFinal(cryptotext);
		} catch(IllegalBlockSizeException e) {
			throw new Error(e);
		}
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
		} catch(UnsupportedEncodingException e) {
			Util.ohMyGodUtf8IsNotSupportedWhatShouldIDo(e);
			//is not reached because an Error is thrown
			return new byte[0];
		}
	}

	class SerializablePackage {
		@SerializedName("Type")
		private Package.Type type;

		@SerializedName("ClientID")
		private int clientID = 0;

		@SerializedName("Content")
		private String content;

		SerializablePackage(Package.Type type, byte[] content) {
			this.type = type;
			this.content = encodeBytes(encrypt(content));
		}

		SerializablePackage(Package.Type type, int clientID, byte[] content) {
			this.type = type;
			this.clientID = clientID;
			this.content = encodeBytes(encrypt(content));
		}

		private SerializablePackage(SerializablePackage pkg) {
			this.type = pkg.type;
			this.clientID = pkg.clientID;
			this.content = pkg.content;
		}

		Package.Type getType() {
			return type;
		}

		int getClientID() {
			return clientID;
		}

		byte[] getContent() throws BadPaddingException {
			return decrypt(decodeBytes(content));
		}

	}

}

