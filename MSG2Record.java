/**
 * This object stores a MSG2 Record Header and its 4 variables based on group. <br>
 * Input is the 64 raw bytes for the record. <br>
 * 
 * Values based off of documentation at:  http://rda.ucar.edu/datasets/ds540.1/docs/msg  <br>
 * 
 * @author daniel.waybright@gmail.com
 * 
 */

package com.msg.read;


public class MSG2Record {
	public int RPTIN;
	public int RPTID;
	private int year;
	private int month;
	private int boxSize;
	private float boxLatitude;
	private float boxLongitude;
	private int pid1;
	private int pid2;
	private int group;
	private int chksum;
	
	private Variable var1;
	private Variable var2;
	private Variable var3;
	private Variable var4;
	
	
	public MSG2Record(byte[] b) {
		calcHeader(b);
		
		switch( this.group ) 
		{
			//Variable(              label,        boxSize,       units,     base, codeLOW, codeHIGH, offset, record)
			case 3:  
				var1 = new Variable("S", this.boxSize, (float)0.01,   -501,  1,   4501,  0, b);
				var2 = new Variable("A", this.boxSize, (float)0.01,  -8801,  1,  14601,  2, b);
				var3 = new Variable("Q", this.boxSize, (float)0.01,     -1,  1,   4001,  4, b);
				var4 = new Variable("R", this.boxSize, (float) 0.1,     -1,  1,   1001,  6, b);
				break;
			case 4:
				var1 = new Variable("W", this.boxSize, (float)0.01,     -1,  1,  10221,  0, b);
				var2 = new Variable("U", this.boxSize, (float)0.01, -10221,  1,  20441,  2, b);
				var3 = new Variable("V", this.boxSize, (float)0.01, -10221,  1,  20441,  4, b);
				var4 = new Variable("P", this.boxSize, (float)0.01,  86999,  1,  20461,  6, b);
				break;
			case 5:
				var1 = new Variable("C", this.boxSize, (float) 0.1,     -1,  1,     81,  0, b);
				var4 = new Variable("R", this.boxSize, (float) 0.1,     -1,  1,   1001,  2, b);
				var3 = new Variable("X", this.boxSize, (float) 0.1, -30001,  1,  60001,  4, b);
				var4 = new Variable("Y", this.boxSize, (float) 0.1, -30001,  1,  60001,  6, b);
				break;
			case 6:
				var1 = new Variable("D=S-A",      this.boxSize, (float)0.01,  -6301,  1,  19101,  0, b);
				var2 = new Variable("E=(S-A)*W",  this.boxSize, (float) 0.1, -10001,  1,  20001,  2, b);
				var3 = new Variable("F=QS-Q",     this.boxSize, (float)0.01,  -4001,  1,   8001,  4, b);
				var4 = new Variable("G=(QS-Q)*W", this.boxSize, (float) 0.1, -10001,  1,  20001,  6, b);
				break;
			case 7:
				var1 = new Variable("UA", this.boxSize, (float) 0.1,  -20001, 1,   40001, 0, b);
				var2 = new Variable("VA", this.boxSize, (float) 0.1,  -20001, 1,   40001, 2, b);
				var3 = new Variable("UQ", this.boxSize, (float) 0.1,  -10001, 1,   20001, 4, b);
				var4 = new Variable("VQ", this.boxSize, (float) 0.1,  -10001, 1,   20001, 6, b);
				break;
			case 9:
				var1 = new Variable("FU",      this.boxSize, (float) 0.1,  -10001, 1,   20001, 0, b);
				var2 = new Variable("FV",      this.boxSize, (float) 0.1,  -10001, 1,   20001, 2, b);
				var3 = new Variable("B1=W**3", this.boxSize, (float) 0.5,      -1, 1,   65535, 4, b);
				var4 = new Variable("B2=W**3", this.boxSize, (float)   5,      -1, 1,   65535, 6, b);
				break;
			default:
				break;
		}
	}
	
	/**
	 * A helper function that parses the record header. <br>
	 * 
	 * @param record The raw byte[] holding the record.
	 */
	private void calcHeader(byte[] record) {
		// Since all primitives in Java are signed, we need to do this
		// to prevent potential negative values when computing.
		int b0 = record[0] & 0xFF;
		int b1 = record[1] & 0xFF;
		int b2 = record[2] & 0xFF;
		int b3 = record[3] & 0xFF;
		int b4 = record[4] & 0xFF;
		int b5 = record[5] & 0xFF;
		int b6 = record[6] & 0xFF;
		int b7 = record[7] & 0xFF;
		
		this.RPTIN        = ( b0 << 4) + ((b1 & 0xF0) >> 4);
		this.RPTID        = b1 & 0x0F;
		this.year         = b2 + 1799;
		this.month        = (b3 >> 4) & 0x0F; 
		this.boxSize      = ((b3 >> 1) & 0x07) - 1;
		this.boxLongitude = ( (((b3 & 0x01) << 9) | (b4 << 1) | ((b5 & 0x80) >> 7))  - 1 ) * (float)0.5;
		this.boxLatitude  = ( (((b5 & 0x7F) << 2) | (b6 >> 6)) - 181 ) * (float)0.5;
		this.pid1         = ((b6 >> 3) & 0x07) == 0 ? -9999 : (b6 >> 3) & 0x07;
		this.pid2         = (b6 & 0x07) - 1;
		this.group        = b7 >> 4;
		this.chksum       = b7 & 0x0F;
		
		// The ternary operator is neat, but if you've never seen it before,
		// Format:  condition ? true : false;
		// Where it tests the condition and then uses the appropriate true/false clause.
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		ret.append(String.format("YEAR %d. MONTH %d. BSZ %d. BLO %3.1f. BLA %3.1f. PID1 %d. PID2 %d. GRP %d. CK %d.\n",
					this.year, this.month, this.boxSize, this.boxLongitude, this.boxLatitude, this.pid1, this.pid2, this.group, this.chksum));
		
		ret.append(String.format("%12s\t", ""));
		ret.append(String.format("%8s\t", "S1"));
		ret.append(String.format("%8s\t", "S3"));
		ret.append(String.format("%8s\t", "S5"));
		ret.append(String.format("%8s\t", "M"));
		ret.append(String.format("%8s\t", "N"));
		ret.append(String.format("%8s\t", "S"));
		ret.append(String.format("%8s\t", "D"));
		ret.append(String.format("%8s\t", "HT"));
		ret.append(String.format("%8s\t", "X"));
		ret.append(String.format("%8s\t", "Y"));
		ret.append("\n");
		
		ret.append(this.var1.toString());
		ret.append(this.var2.toString());
		ret.append(this.var3.toString());
		ret.append(this.var4.toString());
		
		return ret.toString();
	}
}
