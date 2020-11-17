package top.nicelee.purehost.vpn.dns;

import java.nio.ByteBuffer;

public class DnsPacket {
    public DnsHeader Header;
    public Question[] Questions;
    public Resource[] Resources;
    public Resource[] AResources;
    public Resource[] EResources;

    public int Size;

    public static DnsPacket FromBytes(ByteBuffer buffer) {
        if (buffer.limit() < 12){
            //System.out.println("DNS size < 12");
            return null;
        }

        if (buffer.limit() > 512){
            //System.out.println("DNS size > 512");
            return null;
        }

        DnsPacket packet = new DnsPacket();
        packet.Size = buffer.limit();
        packet.Header = DnsHeader.FromBytes(buffer);

        if (packet.Header.QuestionCounts > 2 || packet.Header.ResourceCounts > 50 || packet.Header.AResourceCounts > 50 || packet.Header.EResourceCounts > 50) {
            //System.out.println("DNS sources too complicated");
            return null;
        }

        packet.Questions = new Question[packet.Header.QuestionCounts];
        packet.Resources = new Resource[packet.Header.ResourceCounts];
        packet.AResources = new Resource[packet.Header.AResourceCounts];
        packet.EResources = new Resource[packet.Header.EResourceCounts];

        for (int i = 0; i < packet.Questions.length; i++) {
            packet.Questions[i] = Question.FromBytes(buffer);
        }

        for (int i = 0; i < packet.Resources.length; i++) {
            packet.Resources[i] = Resource.FromBytes(buffer);
        }

        for (int i = 0; i < packet.AResources.length; i++) {
            packet.AResources[i] = Resource.FromBytes(buffer);
        }

        for (int i = 0; i < packet.EResources.length; i++) {
            packet.EResources[i] = Resource.FromBytes(buffer);
        }

        return packet;
    }

    public void ToBytes(ByteBuffer buffer) {
        Header.QuestionCounts = 0;
        Header.ResourceCounts = 0;
        Header.AResourceCounts = 0;
        Header.EResourceCounts = 0;

        if (Questions != null)
            Header.QuestionCounts = (short) Questions.length;
        if (Resources != null)
            Header.ResourceCounts = (short) Resources.length;
        if (AResources != null)
            Header.AResourceCounts = (short) AResources.length;
        if (EResources != null)
            Header.EResourceCounts = (short) EResources.length;

        this.Header.ToBytes(buffer);

        for (int i = 0; i < Header.QuestionCounts; i++) {
            this.Questions[i].ToBytes(buffer);
        }

        for (int i = 0; i < Header.ResourceCounts; i++) {
            this.Resources[i].ToBytes(buffer);
        }

        for (int i = 0; i < Header.AResourceCounts; i++) {
            this.AResources[i].ToBytes(buffer);
        }

        for (int i = 0; i < Header.EResourceCounts; i++) {
            this.EResources[i].ToBytes(buffer);
        }
    }

    public static String ReadDomain(ByteBuffer buffer, int dnsHeaderOffset) {
        StringBuilder sb = new StringBuilder();
        int len = 0;
        while (buffer.hasRemaining() && (len = (buffer.get() & 0xFF)) > 0) {
            if ((len & 0xc0) == 0xc0)
            {
               
                int pointer = buffer.get() & 0xFF;
                pointer |= (len & 0x3F) << 8;

                ByteBuffer newBuffer = ByteBuffer.wrap(buffer.array(), dnsHeaderOffset + pointer, dnsHeaderOffset + buffer.limit());
                sb.append(ReadDomain(newBuffer, dnsHeaderOffset));
                return sb.toString();
            } else {
                while (len > 0 && buffer.hasRemaining()) {
                    sb.append((char) (buffer.get() & 0xFF));
                    len--;
                }
                sb.append('.');
            }
        }

        if (len == 0 && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static void WriteDomain(String domain, ByteBuffer buffer) {
        if (domain == null || domain == "") {
            buffer.put((byte) 0);
            return;
        }

        String[] arr = domain.split("\\.");
        for (String item : arr) {
            if (arr.length > 1) {
                buffer.put((byte) item.length());
            }

            for (int i = 0; i < item.length(); i++) {
                buffer.put((byte) item.codePointAt(i));
            }
        }
    }
}
