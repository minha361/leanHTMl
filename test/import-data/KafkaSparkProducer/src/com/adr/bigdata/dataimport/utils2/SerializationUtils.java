package com.adr.bigdata.dataimport.utils2;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class SerializationUtils {
	
	  private static byte[] ser2bytes(Serializable obj) throws Exception {
		    byte[] objBytes = null;
		    ObjectOutputStream oos = null;
		    try {
		      ByteArrayOutputStream baos = new ByteArrayOutputStream();
		      oos = new ObjectOutputStream(baos);
		      oos.writeObject(obj);
		      oos.flush();
		      objBytes = baos.toByteArray();
		    } finally {
		      if (oos != null) {
		        try {
		          oos.close();
		        } catch (Exception ign) {}
		      }
		    }
		    return objBytes;
		  }
	
	  private static Serializable bytes2ser(byte[] objBytes) throws Exception {
		    Serializable ser = null;
		    ObjectInputStream ois = null;
		    try {
		      ois = new ObjectInputStream(new ByteArrayInputStream(objBytes));
		      ser = (Serializable) ois.readObject();
		    } finally {
		      if (ois != null) {
		        try {
		          ois.close();
		        } catch (Exception ignore) {}
		      }
		    }
		    return ser;
		  }
	  
	  private static Serializable assertSerializable(Object obj) {
		    Serializable ser = (Serializable)obj;
		    try {
		      bytes2ser(ser2bytes(ser));
		    } catch (Exception exc) {
		      throw new IllegalArgumentException("Object of type ["+obj.getClass().getName()+"] is not Serializable due to: "+exc);
		    }
		    return ser;
		  }
}
