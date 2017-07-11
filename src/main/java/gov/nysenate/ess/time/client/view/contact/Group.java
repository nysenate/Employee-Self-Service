
package gov.nysenate.ess.time.client.view.contact;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "group", namespace = "http://www.sendwordnow.com")
@XmlSeeAlso({
        gov.nysenate.ess.time.client.view.contact.ContactBatch.BatchGroupList.Group.class
})
public class Group {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "desc")
    protected String desc;


    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String value) {
        this.desc = value;
    }

}
