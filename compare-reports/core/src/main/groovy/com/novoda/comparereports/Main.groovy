package com.novoda.comparereports

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.novoda.comparereports.bean.Compare

public class Main {

    public static void main(String[] args) {
        ObjectMapper mapper = new XmlMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        new Compare().main(mapper)
    }

}
