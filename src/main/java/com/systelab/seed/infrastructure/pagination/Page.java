package com.systelab.seed.infrastructure.pagination;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Page<T> implements Serializable {

    private long totalElements;

    @XmlAnyElement(lax = true)
    private List<T> content;

    public Page() {
        this.content = new ArrayList();
        this.totalElements = 0;
    }

    public Page(List<T> content, long total) {
        this.content = content;
        this.totalElements = total;
    }
}