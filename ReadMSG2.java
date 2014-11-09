/**
 * A test main to see if working correctly. <br>
 * 
 * The two included test documents are from ICOADS release 2.5 <br>
 * 
 * @author daniel.waybright@gmail.com
 * 
 */

package com.msg.read;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;


public class ReadMSG2 {
	
	public static void main(String[] args) {
		try {
			// Open file and view as a byte stream
			FileInputStream instream = new FileInputStream("doc/MSG2_1825.12.6.ENH");
			BufferedInputStream reader = new BufferedInputStream(instream);
			
			byte[] byteArray = new byte[64];
			
			// Fixed field makes it easy, either pull 64 bytes of a record or first
			// character will be EOF, returning the -1 to terminate the loop.
			while( reader.read(byteArray, 0, 64) != -1 ) {
				MSG2Record temp = new MSG2Record(byteArray);
				
				System.out.println( temp.toString() );
			}
			
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
