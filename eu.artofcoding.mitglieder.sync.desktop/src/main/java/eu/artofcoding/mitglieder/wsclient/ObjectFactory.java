
package eu.artofcoding.mitglieder.wsclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.artofcoding.mitglieder.wsclient package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ChangeAllDrupalDateToStringResponse_QNAME = new QName("http://web.mitglieder.artofcoding.eu/", "changeAllDrupalDateToStringResponse");
    private final static QName _ChangeAllDrupalDateToString_QNAME = new QName("http://web.mitglieder.artofcoding.eu/", "changeAllDrupalDateToString");
    private final static QName _MaintainMember_QNAME = new QName("http://web.mitglieder.artofcoding.eu/", "maintainMember");
    private final static QName _ChangeDrupalDateToStringResponse_QNAME = new QName("http://web.mitglieder.artofcoding.eu/", "changeDrupalDateToStringResponse");
    private final static QName _ChangeDrupalDateToString_QNAME = new QName("http://web.mitglieder.artofcoding.eu/", "changeDrupalDateToString");
    private final static QName _MaintainMemberResponse_QNAME = new QName("http://web.mitglieder.artofcoding.eu/", "maintainMemberResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.artofcoding.mitglieder.wsclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MemberData }
     * 
     */
    public MemberData createMemberData() {
        return new MemberData();
    }

    /**
     * Create an instance of {@link MemberData.Data }
     * 
     */
    public MemberData.Data createMemberDataData() {
        return new MemberData.Data();
    }

    /**
     * Create an instance of {@link ChangeAllDrupalDateToStringResponse }
     * 
     */
    public ChangeAllDrupalDateToStringResponse createChangeAllDrupalDateToStringResponse() {
        return new ChangeAllDrupalDateToStringResponse();
    }

    /**
     * Create an instance of {@link ChangeDrupalDateToStringResponse }
     * 
     */
    public ChangeDrupalDateToStringResponse createChangeDrupalDateToStringResponse() {
        return new ChangeDrupalDateToStringResponse();
    }

    /**
     * Create an instance of {@link MaintainMember }
     * 
     */
    public MaintainMember createMaintainMember() {
        return new MaintainMember();
    }

    /**
     * Create an instance of {@link ChangeAllDrupalDateToString }
     * 
     */
    public ChangeAllDrupalDateToString createChangeAllDrupalDateToString() {
        return new ChangeAllDrupalDateToString();
    }

    /**
     * Create an instance of {@link ChangeDrupalDateToString }
     * 
     */
    public ChangeDrupalDateToString createChangeDrupalDateToString() {
        return new ChangeDrupalDateToString();
    }

    /**
     * Create an instance of {@link MaintainMemberResponse }
     * 
     */
    public MaintainMemberResponse createMaintainMemberResponse() {
        return new MaintainMemberResponse();
    }

    /**
     * Create an instance of {@link MemberData.Data.Entry }
     * 
     */
    public MemberData.Data.Entry createMemberDataDataEntry() {
        return new MemberData.Data.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeAllDrupalDateToStringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://web.mitglieder.artofcoding.eu/", name = "changeAllDrupalDateToStringResponse")
    public JAXBElement<ChangeAllDrupalDateToStringResponse> createChangeAllDrupalDateToStringResponse(ChangeAllDrupalDateToStringResponse value) {
        return new JAXBElement<ChangeAllDrupalDateToStringResponse>(_ChangeAllDrupalDateToStringResponse_QNAME, ChangeAllDrupalDateToStringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeAllDrupalDateToString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://web.mitglieder.artofcoding.eu/", name = "changeAllDrupalDateToString")
    public JAXBElement<ChangeAllDrupalDateToString> createChangeAllDrupalDateToString(ChangeAllDrupalDateToString value) {
        return new JAXBElement<ChangeAllDrupalDateToString>(_ChangeAllDrupalDateToString_QNAME, ChangeAllDrupalDateToString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaintainMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://web.mitglieder.artofcoding.eu/", name = "maintainMember")
    public JAXBElement<MaintainMember> createMaintainMember(MaintainMember value) {
        return new JAXBElement<MaintainMember>(_MaintainMember_QNAME, MaintainMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeDrupalDateToStringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://web.mitglieder.artofcoding.eu/", name = "changeDrupalDateToStringResponse")
    public JAXBElement<ChangeDrupalDateToStringResponse> createChangeDrupalDateToStringResponse(ChangeDrupalDateToStringResponse value) {
        return new JAXBElement<ChangeDrupalDateToStringResponse>(_ChangeDrupalDateToStringResponse_QNAME, ChangeDrupalDateToStringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeDrupalDateToString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://web.mitglieder.artofcoding.eu/", name = "changeDrupalDateToString")
    public JAXBElement<ChangeDrupalDateToString> createChangeDrupalDateToString(ChangeDrupalDateToString value) {
        return new JAXBElement<ChangeDrupalDateToString>(_ChangeDrupalDateToString_QNAME, ChangeDrupalDateToString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaintainMemberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://web.mitglieder.artofcoding.eu/", name = "maintainMemberResponse")
    public JAXBElement<MaintainMemberResponse> createMaintainMemberResponse(MaintainMemberResponse value) {
        return new JAXBElement<MaintainMemberResponse>(_MaintainMemberResponse_QNAME, MaintainMemberResponse.class, null, value);
    }

}
