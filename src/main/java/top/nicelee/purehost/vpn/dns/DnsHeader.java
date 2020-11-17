package top.nicelee.purehost.vpn.dns;

import java.nio.ByteBuffer;

import top.nicelee.purehost.vpn.ip.CommonMethods;

public class DnsHeader {

	public short IDs;
	public DnsFlags Flag;
	public short QuestionCounts;
	public short ResourceCounts;
	public short AResourceCounts;
	public short EResourceCounts;

	public static DnsHeader FromBytes(ByteBuffer buffer) {
		DnsHeader header = new DnsHeader(buffer.array(), buffer.arrayOffset() + buffer.position());
		header.IDs = buffer.getShort();
		header.Flag = DnsFlags.Parse(buffer.getShort());
		header.QuestionCounts = buffer.getShort();
		header.ResourceCounts = buffer.getShort();
		header.AResourceCounts = buffer.getShort();
		header.EResourceCounts = buffer.getShort();
		return header;
	}

	public void ToBytes(ByteBuffer buffer) {
		buffer.putShort(this.IDs);
		buffer.putShort(this.Flag.ToShort());
		buffer.putShort(this.QuestionCounts);
		buffer.putShort(this.ResourceCounts);
		buffer.putShort(this.AResourceCounts);
		buffer.putShort(this.EResourceCounts);
	}

	static final short offset_ID = 0;
	static final short offset_Flags = 2;
	static final short offset_QuestionCount = 4;
	static final short offset_ResourceCount = 6;
	static final short offset_AResourceCount = 8;
	static final short offset_EResourceCount = 10;

	public byte[] Data;
	public int Offset;

	public DnsHeader(byte[] data, int offset) {
        this.Offset = offset;
        this.Data = data;
    }

	public short getID() {
		return CommonMethods.readShort(Data, Offset + offset_ID);
	}

	public short getFlags() {
		return CommonMethods.readShort(Data, Offset + offset_Flags);
	}

	public short getQuestionCount() {
		return CommonMethods.readShort(Data, Offset + offset_QuestionCount);
	}

	public short getResourceCount() {
		return CommonMethods.readShort(Data, Offset + offset_ResourceCount);
	}

	public short getAResourceCount() {
		return CommonMethods.readShort(Data, Offset + offset_AResourceCount);
	}

	public short getEResourceCount() {
		return CommonMethods.readShort(Data, Offset + offset_EResourceCount);
	}

	public void setID(short value) {
		CommonMethods.writeShort(Data, Offset + offset_ID, value);
	}

	public void setFlags(short value) {
		CommonMethods.writeShort(Data, Offset + offset_Flags, value);
	}

	public void setQuestionCount(short value) {
		CommonMethods.writeShort(Data, Offset + offset_QuestionCount, value);
	}

	public void setResourceCount(short value) {
		CommonMethods.writeShort(Data, Offset + offset_ResourceCount, value);
	}

	public void setAResourceCount(short value) {
		CommonMethods.writeShort(Data, Offset + offset_AResourceCount, value);
	}

	public void setEResourceCount(short value) {
		CommonMethods.writeShort(Data, Offset + offset_EResourceCount, value);
	}
}
