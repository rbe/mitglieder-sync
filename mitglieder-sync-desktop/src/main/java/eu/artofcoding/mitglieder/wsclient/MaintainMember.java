
package eu.artofcoding.mitglieder.wsclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für maintainMember complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="maintainMember">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="memberData" type="{http://web.mitglieder.artofcoding.eu/}memberData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "maintainMember", propOrder = {
    "memberData"
})
public class MaintainMember {

    protected MemberData memberData;

    /**
     * Ruft den Wert der memberData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MemberData }
     *     
     */
    public MemberData getMemberData() {
        return memberData;
    }

    /**
     * Legt den Wert der memberData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberData }
     *     
     */
    public void setMemberData(MemberData value) {
        this.memberData = value;
    }

}
