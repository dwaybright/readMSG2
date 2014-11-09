/**
 * This object stores a MSG2 Record Variable. <br>
 * Input is the 64 raw bytes for the record along with required info. <br>
 * 
 * Values based off of documentation at:  http://rda.ucar.edu/datasets/ds540.1/docs/msg  <br>
 * 
 * @author daniel.waybright@gmail.com
 */

package com.msg.read;


public class Variable {
	public float s1, s3, s5, mean, stdev, ht, x, y;
	public int numObs, meanDay;
	public String label;
	
	/**
	 * Holds a generic Variable for a MSG2 Record
	 * 
	 * @param label
	 * @param boxSize
	 * @param units
	 * @param base
	 * @param codeLOW
	 * @param codeHIGH
	 * @param offset
	 * @param record
	 */
	public Variable(String label, int boxSize, float units, int base, int codeLOW, int codeHIGH, int offset, byte[] record) {
		this.label = label;
		
		// Compute coded values for 10 statistics
		
		int s1_code      = ( (record[ 8 + offset] & 0xFF) << 8) + (record[ 8 + offset + 1] & 0xFF);
		int s3_code      = ( (record[16 + offset] & 0xFF) << 8) + (record[16 + offset + 1] & 0xFF);
		int s5_code      = ( (record[24 + offset] & 0xFF) << 8) + (record[24 + offset + 1] & 0xFF);
		int mean_code    = ( (record[32 + offset] & 0xFF) << 8) + (record[32 + offset + 1] & 0xFF);
		int numObs_code  = ( (record[40 + offset] & 0xFF) << 8) + (record[40 + offset + 1] & 0xFF);
		int stdev_code   = ( (record[48 + offset] & 0xFF) << 8) + (record[48 + offset + 1] & 0xFF);
		
		int meanDay_code = -9999, ht_code = -9999, x_code = -9999, y_code = -9999;
		
		switch(offset) {
			case 0:
				meanDay_code = ((record[56] & 0xF0) >> 4) & 0xFF;
				ht_code      = ((record[58] & 0xF0) >> 4) & 0xFF;
				x_code       = ((record[60] & 0xF0) >> 4) & 0xFF;
				y_code       = ((record[62] & 0xF0) >> 4) & 0xFF;
				break;
			case 2:
				meanDay_code = record[56] & 0x0F;
				ht_code      = record[58] & 0x0F;
				x_code       = record[60] & 0x0F;
				y_code       = record[62] & 0x0F;
				break;
			case 4:
				meanDay_code = ((record[57] & 0xF0) >> 4) & 0xFF;
				ht_code      = ((record[59] & 0xF0) >> 4) & 0xFF;
				x_code       = ((record[61] & 0xF0) >> 4) & 0xFF;
				y_code       = ((record[63] & 0xF0) >> 4) & 0xFF;
				break;
			case 6:
				meanDay_code = record[57] & 0x0F;
				ht_code      = record[59] & 0x0F;
				x_code       = record[61] & 0x0F;
				y_code       = record[63] & 0x0F;
				break;
			default:
				break;
		}
		
		// Assign to global variables with transformations
		
		if( s1_code >= codeLOW && s1_code <= codeHIGH) {
			this.s1 = (s1_code + base) * units;
		} else {
			this.s1 = -9999;
		}
		
		if( s3_code >= codeLOW && s3_code <= codeHIGH) {
			this.s3 = (s3_code + base) * units;
		} else {
			this.s3 = -9999;
		}
		
		if( s5_code >= codeLOW && s5_code <= codeHIGH) {
			this.s5 = (s5_code + base) * units;
		} else {
			this.s5 = -9999;
		}
		
		if( mean_code >= codeLOW && mean_code <= codeHIGH) {
			this.mean = (mean_code + base) * units;
		} else {
			this.s5 = -9999;
		}
		
		this.numObs  = numObs_code;
		this.stdev   = stdev_code   == 0 ? -9999 : (stdev_code   - 1) * units;
		this.meanDay = meanDay_code == 0 ? -9999 : (meanDay_code - 0) * 2;
		this.ht      = ht_code      == 0 ? -9999 : (ht_code      - 1) * (float)0.1;
		
		
		if( x_code == 1 ) {
			//if( boxSize == 0.5 ) { this.x = (float)0.01; }
			if( boxSize == 1   ) { this.x = (float)0.02; }
			if( boxSize == 2   ) { this.x = (float)0.05; }
		} else if ( x_code == 11 ){
			//if( boxSize == 0.5 ) { this.x = (float)0.49; }
			if( boxSize == 1   ) { this.x = (float)0.98; }
			if( boxSize == 2   ) { this.x = (float)1.95; }
		} else if( x_code > 1 && x_code < 11 ){
			//if( boxSize == 0.5 ) { this.x = (x_code - 1) * (float).05; }
			if( boxSize == 1   ) { this.x = (x_code - 1) * (float).1;  }
			if( boxSize == 2   ) { this.x = (x_code - 1) * (float).2;  }
		} else {
			this.x = -9999;
		}
			
		if( y_code == 1 ) {
			//if( boxSize == 0.5 ) { this.y = (float)0.01; }
			if( boxSize == 1   ) { this.y = (float)0.02; }
			if( boxSize == 2   ) { this.y = (float)0.05; }
		} else if ( y_code == 11 ){
			//if( boxSize == 0.5 ) { this.y = (float)0.49; }
			if( boxSize == 1   ) { this.y = (float)0.98; }
			if( boxSize == 2   ) { this.y = (float)1.95; }
		} else if( y_code > 1 && x_code < 11 ){
			//if( boxSize == 0.5 ) { this.y = (y_code - 1) * (float).05; }
			if( boxSize == 1   ) { this.y = (y_code - 1) * (float).1;  }
			if( boxSize == 2   ) { this.y = (y_code - 1) * (float).2;  }
		} else {
			this.y = -9999;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		ret.append(String.format("%12s\t",  this.label));
		ret.append(String.format("%8.2f\t", this.s1));
		ret.append(String.format("%8.2f\t", this.s3));
		ret.append(String.format("%8.2f\t", this.s5));
		ret.append(String.format("%8.2f\t", this.mean));
		ret.append(String.format("%8d\t",   this.numObs));
		ret.append(String.format("%8.2f\t", this.stdev));
		ret.append(String.format("%8d\t", this.meanDay));
		ret.append(String.format("%8.2f\t", this.ht));
		ret.append(String.format("%8.2f\t", this.x));
		ret.append(String.format("%8.2f\t", this.y));
		ret.append("\n");
		
		return ret.toString();
	}
}
