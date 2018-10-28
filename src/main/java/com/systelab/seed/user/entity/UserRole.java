package com.systelab.seed.user.entity;

import javax.xml.bind.annotation.XmlEnumValue;

public enum UserRole {
    @XmlEnumValue("USER") USER,
    @XmlEnumValue("ADMIN") ADMIN
}